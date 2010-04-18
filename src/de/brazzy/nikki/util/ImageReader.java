package de.brazzy.nikki.util;
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.border.EtchedBorder;

import mediautil.gen.Rational;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mortennobel.imagescaling.ResampleOp;
import com.mortennobel.imagescaling.ThumpnailRescaleOp;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Rotation;
import de.brazzy.nikki.model.Waypoint;

/**
 * Extracts all data from an image file
 * 
 * @author Michael Borgwardt
 */
public class ImageReader extends ImageDataIO
{

    private static final int THUMBNAIL_SIZE = 180;
    private static byte[] errorIcon;
    static
    {
        try
        {
            errorIcon = IOUtils.toByteArray(ImageReader.class.getResourceAsStream("noimage.jpg"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private DateTimeZone scanZone;
    private Rotation rotation;
    private BufferedImage mainImage;
    private Boolean thumbnailNew;
    
    /**
     * @param file to read from
     * @param zone to use when image does not contain time zone in EXIF
     * @throws LLJTranException
     */
    public ImageReader(File file, DateTimeZone zone) throws LLJTranException
    {
        super(file, LLJTran.READ_INFO);
        this.file = file;
        this.scanZone = zone;
    }

    /**
     * @return Image with all fields filled from file data
     */
    public Image createImage()
    {
        Image image = new Image();
        image.setFileName(file.getName());

        try
        {            
            image.setThumbnail(getThumbnail());
            image.setWaypoint(getWaypoint());
            image.setTitle(getTitle());
            image.setDescription(getDescription());
            image.setExport(isExport());
            image.setTime(getTime());
            image.setModified(thumbnailNew.booleanValue());
            llj.freeMemory();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            image.setDescription(Texts.ERROR_PREFIX+e.getMessage());
            image.setThumbnail(errorIcon);
        }
        
        return image;
    }

    private Rotation getRotation()
    {
        if(rotation != null)
        {
            return rotation;
        }
        if(exifData != null)
        {
            int orientation = exifData.getOrientation();
            if(orientation > 0)
            { // see http://sylvana.net/jpegcrop/exif_orientation.html
                if(orientation==8)
                {
                    return Rotation.LEFT;
                }
                if(orientation==3)
                {
                    return Rotation.ROT180D;
                }
                if(orientation==6)
                {
                    return Rotation.RIGHT;
                }
            }            
        }
        return Rotation.NONE;
    }

    /**
     * @return thumbnail, scaled down from image data if not
     * found in EXIF, auto-rotated if rotation is known
     */
    public byte[] getThumbnail() throws Exception
    {
        if(exifData != null)
        {
            byte[] thumb = exifData.getThumbnailBytes();
            if(thumb != null && thumb.length > 0)
            {
                thumbnailNew = Boolean.FALSE;
                return adjustForRotation(thumb);
            }
        }
        thumbnailNew = Boolean.TRUE;
        return scale(THUMBNAIL_SIZE, false, true);
    }

    private byte[] adjustForRotation(byte[] result) throws LLJTranException, IOException
    {
        if(getRotation() != null && getRotation() != Rotation.NONE)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LLJTran llj = new LLJTran(new ByteArrayInputStream(result));
            llj.read(true);
            llj.transform(out, getRotation().getLLJTranConstant(), 0);
            return out.toByteArray();
        }
        else
        {
            return result;                    
        }
    }

    public DateTime getTime() throws ParseException
    {
        DateTime time = null;
        if(exifData != null)
        {
            String date = exifData.getDataTimeOriginalString();
            if(date != null)
            {
                DateTimeZone zone = getTimeZone();
                if(zone==null)
                {
                    return null;
                }
                time = TIME_FORMAT.withZone(zone).parseDateTime(
                        date.substring(0, 19));
            }            
        }
        return time;
    }

    private String getUTF8(int tagName)
    {
        if(nikkiIFD == null)
        {
            return null;
        }
        Entry entry = nikkiIFD.getEntry(tagName, 0);
        if(entry==null)
        {
            return null;
        }

        Object[] values = entry.getValues();
        byte[] bytes = new byte[values.length];
        for(int i=0; i<values.length; i++)
        {
            bytes[i] = (byte)((Integer)values[i]).intValue();
        }

        try
        {
            return new String(bytes, "UTF-8");
        }
        catch(UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("Can't happen", ex);
        }
    }

    public String getTitle()
    {
        return getUTF8(ENTRY_TITLE_INDEX);
    }

    public String getDescription()
    {
        return getUTF8(ENTRY_DESCRIPTION_INDEX);
    }

    public boolean isExport()
    {
        if(nikkiIFD == null)
        {
            return false;
        }
        Integer export = (Integer)nikkiIFD.getEntry(ENTRY_EXPORT_INDEX, 0).getValue(0);
        return export != null && export != 0;
    }
    
    /**
     * @return {@link Boolean#FALSE} when a thumbnail was found in EXIF,
     *         {@link Boolean#FALSE} when it was computed by scaling down
     *         image, null when not yet requested
     */
    public Boolean isThumbnailNew()
    {
        return thumbnailNew;
    }

    public Waypoint getWaypoint() throws ParseException
    {
        if(gpsIFD == null)
        {
            return null;
        }
        
        Waypoint result = new Waypoint();
        result.setTimestamp(getTime());

        Entry latRef = gpsIFD.getEntry(Exif.GPSLatitudeRef, 0);
        Entry lat = gpsIFD.getEntry(Exif.GPSLatitude, 0);
        Entry lonRef = gpsIFD.getEntry(Exif.GPSLongitudeRef, 0);
        Entry lon = gpsIFD.getEntry(Exif.GPSLongitude, 0);
        
        if(latRef!=null && lat!=null)
        {
            GeoCoordinate c = new GeoCoordinate();
            c.setDirection(Cardinal.parse((String) latRef.getValue(0)));            
            c.setMagnitude(readGpsMagnitude(lat));
            result.setLatitude(c);
        }
        if(lonRef!=null && lon!=null)
        {
            GeoCoordinate c = new GeoCoordinate();
            c.setDirection(Cardinal.parse((String) lonRef.getValue(0)));
            c.setMagnitude(readGpsMagnitude(lon));
            result.setLongitude(c);
        }

        return result;
    }
    
    /**
     * Reads a GPS coordinate from EXIF, possible consisting of
     * separate arc degree, minute and second values
     */
    public static float readGpsMagnitude(Entry e)
    {
        Object[] values = e.getValues();
        float result = ((Rational)values[0]).floatValue();
        if(values.length > 1){
            result += ((Rational)values[1]).floatValue() / 60.0;            
        }
        if(values.length > 2){
            result += ((Rational)values[2]).floatValue() / (60.0*60.0);
        }
        return result;
    }

    /**
     * @return time zone read from EXIF if present, default passed into
     * constructor otherwise
     */
    public DateTimeZone getTimeZone()
    {
        if(nikkiIFD == null)
        {
            return this.scanZone;
        }
        
        Entry entry = nikkiIFD.getEntry(ENTRY_TIMEZONE_INDEX, 0);
        if(entry != null && entry.getValue(0) != null)
        {
            String zoneID = (String)entry.getValue(0);
            return DateTimeZone.forID(zoneID);
        }
        else
        {
            return this.scanZone;
        }
    }

    /**
     * Scales image to given size, preserving aspec ratio
     * 
     * @param toWidth width to scale to
     * @param paintBorder whether to paint an etched border around the image
     * @param isThumbnail if true, faster low-quality scaling will be used
     */
    public byte[] scale(int toWidth, boolean paintBorder, boolean isThumbnail) throws IOException, LLJTranException
    {
        if(mainImage == null)
        {
            mainImage = ImageIO.read(file);
        }
        
        int toHeight = heightForWidth(mainImage, toWidth);

        BufferedImageOp op = isThumbnail ? 
                new ThumpnailRescaleOp(toWidth, toHeight) :
                new ResampleOp(toWidth, toHeight);        
        
        BufferedImage scaledImage = op.filter(mainImage, null);
        if(paintBorder)
        {
            Graphics2D g = scaledImage.createGraphics();
            g.setPaintMode();
            new EtchedBorder(EtchedBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY)
            .paintBorder(null, g, 0, 0, toWidth, toHeight);
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "jpg", out);
        return adjustForRotation(out.toByteArray());
    }
    
    private static int heightForWidth(java.awt.Image img, int width)
    {
        return (int) (img.getHeight(null) / (double)img.getWidth(null) * width);
    }


}

package de.brazzy.nikki.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.border.EtchedBorder;

import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import org.apache.commons.io.IOUtils;

import com.mortennobel.imagescaling.ResampleOp;
import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.model.Rotation;
import java.io.UnsupportedEncodingException;
import mediautil.gen.Rational;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;

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

    private TimeZone scanZone;
    private Rotation rotation;
    private int lastWidth;
    private int lastHeight;
    private BufferedImage mainImage;
    
    public ImageReader(File file, TimeZone zone)
    {
        super(file, LLJTran.READ_INFO);
        this.file = file;
        this.scanZone = zone == null ? TimeZone.getDefault() : zone;
    }

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
            image.setZone(getTimeZone());
            image.setTime(getTime());
            image.setModified(false);
            llj.freeMemory();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            image.setDescription("ERROR: "+e.getMessage());
            image.setThumbnail(errorIcon);
        }
        
        return image;
    }

    public Rotation getRotation()
    {
        if(rotation != null)
        {
            return rotation;
        }
        if(metadata != null)
        {
            int orientation = metadata.getOrientation();
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

    public byte[] getThumbnail() throws Exception
    {
        if(metadata != null)
        {
            byte[] thumb = metadata.getThumbnailBytes();
            if(thumb != null && thumb.length > 0)
            {
                return adjustForRotation(thumb);
            }
        }
        return scale(THUMBNAIL_SIZE, false);
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

    public Date getTime() throws ParseException
    {
        Date time = null;
        if(metadata != null)
        {
            String date = metadata.getDataTimeOriginalString();
            if(date != null)
            {
                DateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                format.setTimeZone(getTimeZone());

                time = format.parse(date.substring(0, 19));
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
        return getUTF8(ENTRY_TITLE);
    }

    public String getDescription()
    {
        return getUTF8(ENTRY_DESCRIPTION);
    }

    public boolean isExport()
    {
        if(nikkiIFD == null)
        {
            return false;
        }
        Integer export = (Integer)nikkiIFD.getEntry(ENTRY_EXPORT, 0).getValue(0);
        return export != null && export != 0;
    }

    public Waypoint getWaypoint() throws ParseException
    {
        if(gpsIFD == null)
        {
            return null;
        }
        Entry e;
        GeoCoordinate lat = new GeoCoordinate();
        GeoCoordinate lon = new GeoCoordinate();
        Waypoint result = new Waypoint();
        result.setLatitude(lat);
        result.setLongitude(lon);
        result.setTimestamp(getTime());

        e = gpsIFD.getEntry(Exif.GPSLatitudeRef, 0);
        lat.setDirection(Cardinal.parse((String) e.getValue(0)));
        e = gpsIFD.getEntry(Exif.GPSLatitude, 0);
        lat.setMagnitude(((Rational)e.getValue(0)).floatValue());

        e = gpsIFD.getEntry(Exif.GPSLongitudeRef, 0);
        lon.setDirection(Cardinal.parse((String) e.getValue(0)));
        e = gpsIFD.getEntry(Exif.GPSLongitude, 0);
        lon.setMagnitude(((Rational)e.getValue(0)).floatValue());

        return result;
    }

    public TimeZone getTimeZone()
    {
        if(nikkiIFD == null)
        {
            return this.scanZone;
        }
        String zoneID = (String)nikkiIFD.getEntry(ENTRY_TIMEZONE, 0).getValue(0);
        if(zoneID != null)
        {
            return TimeZone.getTimeZone(zoneID);
        }
        else
        {
            return this.scanZone;
        }
    }

    public byte[] scale(int toWidth, boolean paintBorder) throws IOException, LLJTranException
    {
        if(mainImage == null)
        {
            mainImage = ImageIO.read(file);
        }
        
        this.lastWidth = toWidth;
        this.lastHeight = heightForWidth(mainImage, toWidth);

        ResampleOp op = new ResampleOp(toWidth, this.lastHeight);        
        BufferedImage scaledImage = op.filter(mainImage, null);
        if(paintBorder)
        {
            Graphics2D g = scaledImage.createGraphics();
            g.setPaintMode();
            new EtchedBorder(EtchedBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY)
            .paintBorder(null, g, 0, 0, this.lastWidth, this.lastHeight);
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "jpg", out);
        byte[] result = out.toByteArray();
        return adjustForRotation(out.toByteArray());
    }
    
    private static int heightForWidth(java.awt.Image img, int width)
    {
        return (int) (img.getHeight(null) / (double)img.getWidth(null) * width);
    }

    public int getLastWidth()
    {
        return lastWidth;
    }

    public int getLastHeight()
    {
        return lastHeight;
    }

}

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

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Rotation;
import java.io.FileInputStream;
import java.io.InputStream;
import mediautil.gen.directio.SplitInputStream;
import mediautil.image.jpeg.Exif;

public class ImageReader
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

    private File file;
    private TimeZone zone;
    private Exif metadata;
    private Rotation rotation;
    private int lastWidth;
    private int lastHeight;
    private BufferedImage mainImage;
    private Exception exception;
    
    public ImageReader(File file, TimeZone zone)
    {
        super();
        this.file = file;
        this.zone = zone;
        try
        {
            LLJTran llj = new LLJTran(file);
            llj.read(LLJTran.READ_INFO, true);
            this.metadata = (Exif) llj.getImageInfo();
            this.rotation = getRotation();
        }
        catch(Exception e)
        {
            this.exception = e;
        }
    }

    public Image createImage()
    {
        Image image = new Image();
        image.setFileName(file.getName());

        try
        {            
            if(metadata != null)
            {
                image.setTime(getTime());
            }
            
            image.setThumbnail(getThumbnail());
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
        if(rotation != null && rotation != Rotation.NONE)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LLJTran llj = new LLJTran(new ByteArrayInputStream(result));
            llj.read(true);
            llj.transform(out, rotation.getLLJTranConstant(), 0);
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
                format.setTimeZone(zone);

                time = format.parse(date.substring(0, 19));
            }            
        }
        return time;
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

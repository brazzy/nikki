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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.border.EtchedBorder;

import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.SanselanConstants;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import com.mortennobel.imagescaling.ResampleOp;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Rotation;

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
    private JpegImageMetadata metadata;
    private Rotation rotation;
    private int lastWidth;
    private int lastHeight;
    private BufferedImage mainImage;
    
    public ImageReader(File file, TimeZone zone)
    {
        super();
        this.file = file;
        this.zone = zone;
        try
        {
            this.metadata = (JpegImageMetadata) Sanselan.getMetadata(file, Collections.singletonMap(SanselanConstants.PARAM_KEY_READ_THUMBNAILS, Boolean.TRUE));            
            this.rotation = getRotation();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ImageReadException e)
        {
            e.printStackTrace();            
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
            
            byte[] th = getThumbnail();
            if(th == null)
            {
                th = scale(THUMBNAIL_SIZE, false);
            }
            image.setThumbnail(th);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            image.setDescription("ERROR: "+e.getMessage());
            image.setThumbnail(errorIcon);
        }
        
        return image;
    }

    private Rotation getRotation() throws ImageReadException
    {
        if(metadata != null)
        {
            TiffField orientField = metadata.findEXIFValue(TiffConstants.EXIF_TAG_ORIENTATION);
            if(orientField != null && orientField.getValue() != null)
            { // see http://sylvana.net/jpegcrop/exif_orientation.html
                Integer o = (Integer) orientField.getValue();
                if(new Integer(8).equals(o))
                {
                    return Rotation.LEFT;
                }
                if(new Integer(3).equals(o))
                {
                    return Rotation.ROT180D;
                }
                if(new Integer(6).equals(o))
                {
                    return Rotation.RIGHT;
                }
            }            
        }
        return Rotation.NONE;
    }

    private byte[] getThumbnail() throws Exception
    {
        if(metadata != null)
        {
            @SuppressWarnings("unchecked")
            List<TiffImageMetadata.Directory> dirs = metadata.getExif().getDirectories();
            for(TiffImageMetadata.Directory dir : dirs)
            {
                if(dir.getJpegImageData() !=null)
                {
                    return adjustForRotation(dir.getJpegImageData().data);
                }
            }            
        }
        return null;
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

    private Date getTime() throws ImageReadException, ParseException
    {
        Date time = null;
        if(metadata != null)
        {
            TiffField timeField = metadata.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            if(timeField != null && timeField.getValue() != null)
            {
                DateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                format.setTimeZone(zone);

                time = format.parse(((String)timeField.getValue()).substring(0, 19));
            }            
        }
        return time;
    }

    public byte[] scale(int toWidth, boolean paintBorder) throws IOException, ImageWriteException, LLJTranException
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
    
    public static void main(String[] args) throws Exception
    {
        File f = new File("E:\\tmp\\test\\IMG_3487.JPG");
        File th = new File("E:\\tmp\\test\\IMG_3487_th.JPG");
        ImageReader reader = new ImageReader(f, TimeZone.getDefault());
        byte[] b = reader.scale(400, true);
        FileUtils.writeByteArrayToFile(th, b);
        Runtime.getRuntime().exec(new String[]{"C:\\Programme\\IrfanView\\i_view32.exe", th.getAbsolutePath()});
        System.out.println(reader.getLastHeight());
    }

}

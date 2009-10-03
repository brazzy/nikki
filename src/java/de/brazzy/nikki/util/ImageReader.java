package de.brazzy.nikki.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import mediautil.image.jpeg.LLJTran;

import org.apache.commons.io.FileUtils;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.SanselanConstants;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import de.brazzy.nikki.model.Image;

public class ImageReader
{    
    private static final int THUMBNAIL_SIZE = 180;
    private static final Double ROTATE_RIGHT = new Double(Math.PI/2.0);
    private static final Double ROTATE_180D = new Double(Math.PI);
    private static final Double ROTATE_LEFT = new Double(Math.PI*1.5);
    private static byte[] errorIcon;
    static
    {
        try
        {
            errorIcon = FileUtils.readFileToByteArray(new File(ImageReader.class.getResource("noimage.jpg").toURI()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private File file;
    private JpegImageMetadata metadata;
    private Double rotation;

    public ImageReader(File file)
    {
        super();
        this.file = file;
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
                th = scale(THUMBNAIL_SIZE);
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

    private Double getRotation() throws ImageReadException
    {
        TiffField orientField = metadata.findEXIFValue(TiffConstants.EXIF_TAG_ORIENTATION);
        if(orientField != null && orientField.getValue() != null)
        { // see http://sylvana.net/jpegcrop/exif_orientation.html
            Integer o = (Integer) orientField.getValue();
            if(new Integer(8).equals(o))
            {
                return ROTATE_LEFT;
            }
            if(new Integer(3).equals(o))
            {
                return ROTATE_180D;
            }
            if(new Integer(6).equals(o))
            {
                return ROTATE_RIGHT;
            }
        }
        return null;
    }

    private byte[] getThumbnail() throws Exception
    {
        @SuppressWarnings("unchecked")
        List<TiffImageMetadata.Directory> dirs = metadata.getExif().getDirectories();
        for(TiffImageMetadata.Directory dir : dirs)
        {
            if(dir.getJpegImageData() !=null)
            {
                byte[] result = dir.getJpegImageData().data;
                if(rotation != null)
                {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    LLJTran llj = new LLJTran(new ByteArrayInputStream(result));
                    llj.read(true);
                    if(rotation == ROTATE_180D)
                    {
                        llj.transform(out, LLJTran.ROT_180, 0);                        
                    }
                    else if(rotation == ROTATE_LEFT)
                    {
                        llj.transform(out, LLJTran.ROT_270, 0);                        
                    }
                    else if(rotation == ROTATE_RIGHT)
                    {
                        llj.transform(out, LLJTran.ROT_90, 0);
                    }
                    else
                    {
                        throw new IllegalArgumentException(String.valueOf(rotation));
                    }
                    return out.toByteArray();
                }
                else
                {
                    return result;                    
                }
            }
        }
        return null;
    }

    private Date getTime() throws ImageReadException, ParseException
    {
        Date time = null;
        TiffField timeField = metadata.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if(timeField != null && timeField.getValue() != null)
        {
            time = dateFormat.parse(((String)timeField.getValue()).substring(0, 19));
        }
        return time;
    }

    public byte[] scale(int toWidth) throws IOException, ImageWriteException
    {
        BufferedImage fullSize = ImageIO.read(file);            
        double scale = (double)toWidth/fullSize.getWidth();
        AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
        BufferedImage scaledImage;
        int heightForWidth = heightForWidth(fullSize, toWidth);
        if(rotation!=null)
        {
            if(rotation==ROTATE_RIGHT)
            {
                scaledImage = new BufferedImage(
                        heightForWidth, toWidth, ((int)BufferedImage.TYPE_INT_RGB));
                xform.preConcatenate(AffineTransform.getRotateInstance(rotation.doubleValue(), heightForWidth/2, heightForWidth/2));
            }
            else if(rotation==ROTATE_LEFT)
            {
                scaledImage = new BufferedImage(
                        heightForWidth, toWidth, ((int)BufferedImage.TYPE_INT_RGB));
                xform.preConcatenate(AffineTransform.getRotateInstance(rotation.doubleValue(), toWidth/2, toWidth/2));
            }
            else
            {
                scaledImage = new BufferedImage(
                        toWidth, heightForWidth, ((int)BufferedImage.TYPE_INT_RGB));
            }
        }        
        else
        {
            scaledImage = new BufferedImage(
                    toWidth, heightForWidth, ((int)BufferedImage.TYPE_INT_RGB));
        }
        
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(fullSize, xform, null);
        graphics2D.dispose();
        byte[] bytes = Sanselan.writeImageToBytes(scaledImage, ImageFormat.IMAGE_FORMAT_PNG, new HashMap<Object, Object>());
        return bytes;
    }
    
    private static int heightForWidth(java.awt.Image img, int width)
    {
        return (int) (img.getHeight(null) / (double)img.getWidth(null) * width);
    }

    public static void main(String[] args) throws Exception
    {
        File f = new File("E:\\tmp\\test\\IMG_3572.JPG");
        File th = new File("E:\\tmp\\test\\IMG_3572_th.PNG");
        ImageReader reader = new ImageReader(f);
        byte[] b = reader.scale(400);
        FileUtils.writeByteArrayToFile(th, b);
        Runtime.getRuntime().exec(new String[]{"C:\\Programme\\IrfanView\\i_view32.exe", th.getAbsolutePath()});
    }
}

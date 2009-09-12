package de.brazzy.nikki.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Directory;

public class ImageReader
{
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    public static Image createImage(File file, Directory dir) throws Exception
    {
        Image image = new Image();
        image.setTitle(file.getName());
        image.setFileName(file.getName());
        image.setDirectory(dir);
        BufferedImage fullSize = ImageIO.read(file);
        
        BufferedImage scaledImage = new BufferedImage(
                (int)100, heightForWidth(fullSize, 100), ((int)BufferedImage.TYPE_INT_RGB));
        Graphics2D graphics2D = scaledImage.createGraphics();
        double scale = 100.0d/fullSize.getWidth();
        AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(fullSize, xform, null);
        graphics2D.dispose();
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "JPEG", outStream);
        outStream.close();
        image.setThumbnail(outStream.toByteArray());
        
        JpegImageMetadata md =  (JpegImageMetadata) Sanselan.getMetadata(file);
        if(md != null)
        {                
            TiffField f = md.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL)            ;
            if(f != null && f.getValue() != null)
            {
                image.setTime(dateFormat.parse(((String)f.getValue()).substring(0, 19)));
            }                
        }
        
        return image;
    }
    
    private static int heightForWidth(java.awt.Image img, int width)
    {
        return (int) (img.getHeight(null) / (double)img.getWidth(null) * 100);
    }

}

package de.brazzy.nikki.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Directory;

public class ImageReader
{
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
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

    public static Image createImage(File file, Directory dir)
    {
        System.out.println("Starting: "+file.getName());
        long start = System.currentTimeMillis();
        Image image = new Image();
        image.setTitle(file.getName());
        image.setFileName(file.getName());
        image.setDirectory(dir);

        try
        {
            BufferedImage fullSize = ImageIO.read(file);
            System.out.println("Reading+Decoding: "+(System.currentTimeMillis()-start));
            start = System.currentTimeMillis();
            
            BufferedImage scaledImage = new BufferedImage(
                    (int)100, heightForWidth(fullSize, 100), ((int)BufferedImage.TYPE_INT_RGB));
            Graphics2D graphics2D = scaledImage.createGraphics();
            double scale = 100.0d/fullSize.getWidth();
            AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.drawImage(fullSize, xform, null);
            graphics2D.dispose();
            System.out.println("Scaling: "+(System.currentTimeMillis()-start));
            start = System.currentTimeMillis();

            byte[] bytes = Sanselan.writeImageToBytes(scaledImage, ImageFormat.IMAGE_FORMAT_PNG, new HashMap());
            System.out.println("Encoding thumbnail: "+(System.currentTimeMillis()-start));
            start = System.currentTimeMillis();
            
            image.setThumbnail(bytes);
            
            JpegImageMetadata md =  (JpegImageMetadata) Sanselan.getMetadata(file);
            if(md != null)
            {                
                TiffField f = md.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL)            ;
                if(f != null && f.getValue() != null)
                {
                    image.setTime(dateFormat.parse(((String)f.getValue()).substring(0, 19)));
                }                
            }
            System.out.println("Metadata: "+(System.currentTimeMillis()-start)+"\n");
            start = System.currentTimeMillis();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            image.setDescription("ERROR: "+e.getMessage());
            image.setThumbnail(errorIcon);
        }
        
        return image;
    }
    
    private static int heightForWidth(java.awt.Image img, int width)
    {
        return (int) (img.getHeight(null) / (double)img.getWidth(null) * 100);
    }

}

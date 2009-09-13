package de.brazzy.nikki.model;

import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.tiff.constants.TagInfo
import org.apache.sanselan.formats.tiff.constants.TiffConstants
import org.apache.sanselan.formats.tiff.TiffField
import java.io.FilenameFilter
import java.text.SimpleDateFormat
import java.util.HashMap
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.text.DateFormat
import java.awt.RenderingHints
import de.brazzy.nikki.util.ImageReader
import java.beans.XMLDecoder
class Directory extends ListDataModel<Day>{
    public static final String PERSIST_FILE = "Nikki.db";
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    
    Map<Image> images = [:];
    List<WaypointFile> waypointFiles = [];    
    File path;
    
    public String toString()
    {
        path.name
    }
    
    public void scan()
    {
//        def persist = new File(path, PERSIST_FILE)
//        if(persist.exists)
//        {
//            def dec = new XMLDecoder(
//                    new BufferedOutputStream(
//                        new FileOutputStream(persist)))
//            
//        }
        
        def filter = { dir, name ->
            name.toUpperCase().endsWith(".JPG")
        } as FilenameFilter
        
        def imageFiles = path.listFiles(filter)
        
        def days = new HashMap()
        this.data.each{
            days[it.date] = it
        }
        
        imageFiles.each{
            if(!this.images[it.name])
            {
                Image image = ImageReader.createImage(it, this)
                this.images[it.name] = image
                
                def date = image.time == null ? null : DateFormat.getDateInstance().parse(image.time.dateString)
                def day = days[date]
                if(day)
                {
                    day.images.add(image)
                }
                else
                {
                    day = new Day(date:date, images:[image])
                    days.put(date, day)
                    this.data.add(day)
                }                
            }
            
            this.data.sort{it.date}
            fireContentsChanged(this, 0, this.data.size()-1)
        }

        
    }    
}

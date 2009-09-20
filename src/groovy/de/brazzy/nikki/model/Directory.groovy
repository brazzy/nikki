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
import java.beans.XMLDecoderimport java.beans.XMLEncoderimport java.util.Dateimport javax.swing.SwingWorker
class Directory extends ListDataModel<Day>{
    public static final String PERSIST_FILE = "Nikki.db";    
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    
    Map<Image> images = [:];
    List<WaypointFile> waypointFiles = [];    
    File path;
    
    public String toString()
    {
        path.name
    }
    
    public void scan(SwingWorker worker)
    {
        worker.setProgress(0);
        def persist = new File(path, PERSIST_FILE)
        
        if(this.images.size()==0 && persist.exists())
        {
            def input = new ObjectInputStream(
                        new BufferedInputStream(
                        new FileInputStream(persist)))
            this.data = input.readObject()
            this.data.each
            {
                it.directory = this
            }
            this.images = input.readObject()
            this.waypointFiles = input.readObject()
            input.close()
        }
        
        def filter = { dir, name ->
            name.toUpperCase().endsWith(".JPG")
        } as FilenameFilter
        
        def imageFiles = path.listFiles(filter)
        
        def days = new HashMap()
        this.data.each{
            days[it.date] = it
        }

        int count = 0;
        imageFiles.each{
            if(!this.images[it.name])
            {
                Image image = new ImageReader(it).createImage()
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
                
                image.day = day
            }
            
            worker.progress = new Integer((int)(++count / imageFiles.length * 100))
        }

        this.data.sort{
            it.date==null ?
            new Date(1800,1,1) :
            it.date
        }
        fireContentsChanged(this, 0, this.data.size()-1)
        
        def output = new ObjectOutputStream(
                     new BufferedOutputStream(
                     new FileOutputStream(persist)))
        output.writeObject(this.data)
        output.writeObject(this.images)
        output.writeObject(this.waypointFiles)
        output.close()
    }    
}

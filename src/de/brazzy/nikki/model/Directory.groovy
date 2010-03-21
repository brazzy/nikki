package de.brazzy.nikki.model;

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
import de.brazzy.nikki.util.ScanResult;
import de.brazzy.nikki.util.TimezoneFinder;

import java.beans.XMLDecoder
import java.beans.XMLEncoder
import java.util.Date
import javax.swing.SwingWorker
import java.util.TimeZone
import org.joda.time.DateTimeZone

class Directory extends ListDataModel<Day>{
    public static final long serialVersionUID = 1;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    private static final def FILTER_JPG = { dir, name ->
        name.toUpperCase().endsWith(".JPG")
    } as FilenameFilter
    private static final def FILTER_NMEA = { dir, name ->
        name.toUpperCase().endsWith(".NMEA")
    } as FilenameFilter

    
    Map<String, Image> images = [:];
    Map<String, WaypointFile> waypointFiles = [:];    
    File path;
    
    public String toString()
    {
        (path?.name ?: "<unknown directory>") +" ("+images.size()+", "+waypointFiles.size()+")"
    }
    
    public ScanResult scan(SwingWorker worker, DateTimeZone zone, TimezoneFinder tzFinder)
    {
        worker?.progress = 0;

        int count = 0;
        def imageFiles = path.listFiles(FILTER_JPG)
        Map days = this.data.groupBy{it.date}
        days.entrySet().each{it.value = it.value[0]}
        
        for(file in imageFiles){
            if(!this.images[file.name])
            {
                ImageReader reader = new ImageReader(file, zone)
                if(reader.timeZone==null)
                {
                    return ScanResult.TIMEZONE_MISSING
                }
                Image image = reader.createImage()
                this.images[file.name] = image

                def date = image.time?.toLocalDate()
                def day = days[date]
                if(day)
                {
                    day.images.add(image)
                }
                else
                {
                    day = new Day(date:date, images:[image], directory: this)
                    days.put(date, day)
                    this.add(day)
                } 
                def modified = image.modified
                image.day = day
                image.modified = modified
            }
            
            worker?.progress = new Integer((int)(++count / imageFiles.length * 100))
        }

        def nmeaFiles = path.listFiles(FILTER_NMEA);
        for(file in nmeaFiles){
            if(!this.waypointFiles[file.name])
            {
                WaypointFile wf = WaypointFile.parse(this, file, tzFinder)
                this.waypointFiles[file.name] = wf            
            }
        }

        this.data.sort{
            it.date==null ?
            new Date(1800,1,1) :
            it.date
        }        
        
        fireContentsChanged(this, 0, this.data.size()-1)
        worker?.progress = 0
        return ScanResult.COMPLETE
    }  

    public void save(SwingWorker worker)
    {
        worker?.progress = 0;
        def count = 0;
        for(image in images.values()){
            if(new File(this.path, image.fileName).exists())
            {
                try
                {
                    image.save(this.path)
                }
                catch(Exception ex)
                {
                    ex.printStackTrace()
                }
            }
            worker?.progress = new Integer((int)(++count/images.size() * 100));
        }

        worker?.progress = 0;
    }

}

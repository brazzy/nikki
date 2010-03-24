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
import org.joda.time.LocalDate;

/**
 * Represents on filesystem directory containing images and GPS tracks
 * from one journey
 * 
 * @author Michael Borgwardt
 */
class Directory extends ListDataModel<Day> implements Comparable<Directory>
{
    public static final long serialVersionUID = 1;
    
    private static final def FILTER_JPG = { dir, name ->
        name.toUpperCase().endsWith(".JPG")
    } as FilenameFilter
    private static final def FILTER_NMEA = { dir, name ->
        name.toUpperCase().endsWith(".NMEA")
    } as FilenameFilter


    /**
     * All the images in this directory, keyed on the file name
     */
    Map<String, Image> images = [:];
    
    /**
     * All the GPS tracks in this directory, keyed on the file name
     */
    Map<String, WaypointFile> waypointFiles = [:];    
    
    /**
     * This directory's filesystem path.
     *  Must not be null (TODO: enforce. Currently not possible, as Groovy
     *  ignores "private")
     */
    File path
    
    public String toString()
    {
        path.name+" ("+images.size()+", "+waypointFiles.size()+")"
    }

    /**
     * Scans the filesystem for image and GPS files and processes the data in them
     * 
     * @param worker for updating progress
     * @param zone time zone to which the camera time was set when the images were taken. 
     *             Can be null, which assumes that all images already have time zone
     *             set in their EXIF data
     * @param tzFinder finds time zones for waypoints
     * @return ScanResult.TIMEZONE_MISSING if zone was null and images were found that
     *         have no time zone in their EXIF data
     */
    public ScanResult scan(SwingWorker worker, DateTimeZone zone, TimezoneFinder tzFinder)
    {
        worker?.progress = 0;

        int count = 0;
        def imageFiles = path.listFiles(FILTER_JPG)
        Map days = this.dataList.groupBy{it.date}
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
        
        fireContentsChanged(this, 0, this.size-1)
        worker?.progress = 0
        return ScanResult.COMPLETE
    }  

    /**
     * Saves all changed image data to the EXIF headers
     * 
     * @param worker to update progress
     */
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

    @Override
    public int hashCode()
    {
        return path.hashCode()
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this.is(obj))
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Directory other = (Directory) obj
        return path.equals(other.path)
    }
    @Override
    public int compareTo(Directory other)
    {
        return path.name.compareTo(other.path.name)
    }
    
    public Day getDay(LocalDate date)
    {
        int index = Collections.binarySearch(dataList, new Day(date:date))
        if(index >= 0)
        {
            return getAt(index)
        }
        else
        {
            return null
        }
    }
}

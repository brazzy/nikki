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
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import java.util.Date
import javax.swing.SwingWorker
import java.util.TimeZone
import de.brazzy.nikki.util.RelativeDateFormat

class Directory extends ListDataModel<Day>{
    public static final long serialVersionUID = 1;
    
    public static final String PERSIST_FILE = "Nikki.db";    
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
    TimeZone zone = TimeZone.getDefault();
    
    public String toString()
    {
        path.name+" ("+images.size()+", "+waypointFiles.size()+")"
    }
    
    public void scan(SwingWorker worker)
    {
        worker?.progress = 0;
        readPersistent()
        
        def days = new HashMap()
        this.data.each{
            days[it.date] = it
        }

        int count = 0;
        def imageFiles = path.listFiles(FILTER_JPG)
        
        imageFiles.each{
            if(!this.images[it.name])
            {
                Image image = new ImageReader(it, zone).createImage()
                this.images[it.name] = image

                // Datum gemäß der Foto-Zeitzone verwenden
                def format = new RelativeDateFormat(image.zone == null ? zone : image.zone)

                def date = image.time == null ? null : format.stripTime(image.time)
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
                
                image.day = day
            }
            
            worker?.progress = new Integer((int)(++count / imageFiles.length * 100))
        }

        def nmeaFiles = path.listFiles(FILTER_NMEA);
        nmeaFiles.each{
            if(!this.waypointFiles[it.name])
            {
                WaypointFile wf = WaypointFile.parse(this, it)
                this.waypointFiles[it.name] = wf            
            }
        }

        this.data.sort{
            it.date==null ?
            new Date(1800,1,1) :
            it.date
        }        
        
        fireContentsChanged(this, 0, this.data.size()-1)
        worker?.progress = 0
    }  

    public void saveNew(SwingWorker worker)
    {
        worker?.progress = 0;
        def count = 0;
        images.values().each{
            if(new File(this.path, it.fileName).exists())
            {
                try
                {
                    it.save(this.path)
                }
                catch(Exception ex)
                {
                    ex.printStackTrace()
                }
            }
            worker?.progress = new Integer((int)(++count / images.size() * 100));
        }

        worker?.progress = 0;
    }

    public void save()
    { // TODO: entfernen
        def persist = new File(this.path, PERSIST_FILE)        
        def output = new ObjectOutputStream(
                new BufferedOutputStream(
                new FileOutputStream(persist)))
       output.writeObject(this.zone)
       output.writeObject(this.data)
       output.writeObject(this.images)
       output.writeObject(this.waypointFiles)
       output.close()
    }
    
    public boolean hasPersistent()
    { // TODO: entfernen
        return new File(this.path, PERSIST_FILE).exists()
    }
    
    private void readPersistent()
    { // TODO: entfernen
        def persist = new File(this.path, PERSIST_FILE)        
        if(this.images.size()==0 && persist.exists())
        {
            def input = new ObjectInputStream(
                        new BufferedInputStream(
                        new FileInputStream(persist)))
            def next = input.readObject()
            if(next instanceof TimeZone) // alte Daten behandeln, TODO: entfernen
            {
                this.zone = next
                this.data = input.readObject()
            }
            else
            {
                this.data = next
            }
            this.data.each
            {
                it.directory = this
            }
            this.images = input.readObject()
            this.images.values().each{
                if(!it.zone)
                {
                    it.zone = this.zone
                }
            }

            this.waypointFiles = input.readObject()
            input.close()
        }
    }
}

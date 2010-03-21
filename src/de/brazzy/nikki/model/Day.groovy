package de.brazzy.nikki.model;

import javax.swing.table.AbstractTableModel
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.KmlFactory
import de.micromata.opengis.kml.v_2_2_0.Coordinate
import de.micromata.opengis.kml.v_2_2_0.Document
import de.micromata.opengis.kml.v_2_2_0.Placemark
import de.micromata.opengis.kml.v_2_2_0.LineString
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.util.WaypointComparator;

import javax.swing.SwingWorker
import java.util.TimeZone
import java.text.DateFormat
import java.util.zip.CRC32
import java.text.DecimalFormat

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat;

/**
 * Represents one day during a journey, as experienced subjectively by
 * a traveler (while possible passing through several time zones).
 * 
 * @author Michael Borgwardt
 */
class Day extends AbstractTableModel
{
    public static final long serialVersionUID = 1

    /** 
     * Waypoints further apart than this will result in beginning a new path
     * (thus creating a gap) in the exported KML file
     */
    public static final Duration WAYPOINT_THRESHOLD = Duration.standardSeconds(90)

    /** Images taken on this day */
    List<Image> images = []
                          
    /** Waypoints recorded on this day */
    List<Waypoint> waypoints = []

    /** Date represented by this day */
    LocalDate date
    
    /** Contains the files (images and GPS logs) used by this Day instance */
    Directory directory
    
    public String toString()
    {
        DateTimeFormatter format = ISODateTimeFormat.date()
        (date==null? "unknown" : format.print(date))+" ("+images.size()+", "+waypoints.size()+")"
    }

    /**
     * From AbstractTableModel
     */
    public int getRowCount()
    {
        images.size()
    }
    
    /**
     * From AbstractTableModel
     */
    public int getColumnCount()
    {
        1
    }
    
    /**
     * From AbstractTableModel
     */
    public Object getValueAt(int row, int column)
    {
        images[row]
    }
    
    /**
     * From AbstractTableModel
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) 
    {
        true
    }

    /**
     * Assigns coordinates to each image on this day, based on the
     * waypoint with the most similar timestamp
     */
    public void geotag(ReadablePeriod offset = Seconds.seconds(0))
    {
        waypoints.sort(new WaypointComparator())
        images*.geotag(offset)
    }

    /**
     * Exports this day's data (Annotated images and GPS tracks)
     * to a KMZ file
     * 
     * @param out stream to write the data to
     * @param worker for updating progress
     */
    public void export(ZipOutputStream out, SwingWorker worker)
    {
        worker?.progress = 0;
        Kml kml = KmlFactory.createKml()
        Document doc = kml.createAndSetDocument()
        doc.createAndAddStyle().withId("track")
            .createAndSetLineStyle()
            .withWidth(4.0)
            .withColor("801977FF")
        
        int count = 0;
        out.method = ZipOutputStream.STORED
        def entry = new ZipEntry("images/");
        entry.size = 0;
        entry.crc = 0;
        out.putNextEntry(entry)
        out.closeEntry()
        
        entry = new ZipEntry("thumbs/");
        entry.size = 0;
        entry.crc = 0;
        out.putNextEntry(entry)
        out.closeEntry()
        
        def imgIndexFmt = new DecimalFormat("000 ");
        def imgIndex = 0;
        images.sort{ it.time }
        images.each{ Image image ->
            if(image.export)
            {
                Placemark pm = doc.createAndAddPlacemark()
                    .withName(imgIndexFmt.format(imgIndex++) + (image.title ?: ""))
                    .withDescription(image.longDescription)
                    .withVisibility(true)
                 pm.createAndSetPoint()
                    .withCoordinates([new Coordinate(image.waypoint.longitude.value, image.waypoint.latitude.value)])
                 pm.createAndAddStyle()
                   .createAndSetIconStyle()
                   .withScale(1.5) // adjusts default icon size (64) for out icon size (96)
                   .createAndSetIcon()
                   .withHref("thumbs/"+image.fileName)
    
                ImageReader reader = new ImageReader(new File(directory.path, image.fileName), null)
                store(reader.scale(592, false), "images/"+image.fileName, out)
                store(reader.scale(96, true), "thumbs/"+image.fileName, out)
            }
            worker?.progress = new Integer((int)(++count / images.size * 100))
        }
        
        LineString ls;        
        DateTime previous = new DateTime(1900, 1, 1,0 ,0,0,0);
        waypoints.each{ waypoint ->
            def gap = new Duration(previous, waypoint.timestamp)
            if(gap.isLongerThan(WAYPOINT_THRESHOLD))
            {
                ls = createLine(doc)
            }
            ls.addToCoordinates(waypoint.longitude.value, waypoint.latitude.value)
            previous = waypoint.timestamp
        }
        
        out.method = ZipOutputStream.DEFLATED
        out.putNextEntry(new ZipEntry("diary.kml"))
        kml.marshal(out)
        out.closeEntry()
        out.close()
        worker?.progress = 0
    }
    
    /**
     * Stores one image
     * 
     * @param imgData image data
     * @param name file name
     * @param out stream to write to
     */
    private void store(byte[] imgData, String name, ZipOutputStream out)
    {
        def entry = new ZipEntry(name)
        entry.size = imgData.length
        def crc = new CRC32()
        crc.update(imgData)
        entry.crc = crc.value
        out.putNextEntry(entry)
        out.write(imgData)
        out.closeEntry()
    }

    /**
     * @return creates a new line representing the start of a new GPS track 
     *         or segment
     */
    private LineString createLine(Document doc)
    {
        return doc.createAndAddPlacemark()
            .withStyleUrl("#track")
            .createAndSetLineString()
            .withTessellate(Boolean.TRUE)
            .withExtrude(Boolean.TRUE)
            .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
    }
}

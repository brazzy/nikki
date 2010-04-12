package de.brazzy.nikki.model;
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
class Day extends AbstractTableModel implements Comparable<Day>
{
    public static final long serialVersionUID = 1
    
    private static final DateTimeFormatter DISPLAY_FORMAT = ISODateTimeFormat.date()
    
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
    
    /** 
     *  Contains the files (images and GPS logs) used by this Day instance.
     *  Must not be null (Enforcement currently not possible, as Groovy
     *  ignores "private")
     */
    Directory directory
    
    public String toString()
    {
        return (date==null? "unknown" : DISPLAY_FORMAT.print(date)) +
               " ("+images.size()+", "+waypoints.size()+")"
    }

    /**
     * From AbstractTableModel
     */
    @Override
    public int getRowCount()
    {
        images.size()
    }
    
    /**
     * From AbstractTableModel
     */
    @Override
    public int getColumnCount()
    {
        1
    }
    
    /**
     * From AbstractTableModel
     */
    @Override
    public Object getValueAt(int row, int column)
    {
        images[row]
    }
    
    /**
     * From AbstractTableModel
     */
    @Override
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
        createDirEntry(out, "images/");
        createDirEntry(out, "thumbs/");
        
        def imgIndex = 0;
        images.sort{ it.time }
        for(Image image : images) {
            image.exportTo(out, doc, imgIndex++)
            worker?.progress = new Integer((int)(++count / images.size * 100))
        }

        exportWaypoints(doc)
        
        out.method = ZipOutputStream.DEFLATED
        out.putNextEntry(new ZipEntry("diary.kml"))
        kml.marshal(out)
        out.closeEntry()
        out.close()
        worker?.progress = 0
    }
    
    private static void createDirEntry(ZipOutputStream out, String dirName)
    {
        def entry = new ZipEntry(dirName);
        entry.size = 0;
        entry.crc = 0;
        out.putNextEntry(entry)
        out.closeEntry()
    }
    
    private void exportWaypoints(Document doc)
    {
        LineString ls;        
        DateTime previous = new DateTime(1900, 1, 1,0 ,0,0,0);
        for(Waypoint wp : waypoints) {
            def gap = new Duration(previous, wp.timestamp)
            if(gap.isLongerThan(WAYPOINT_THRESHOLD))
            {
                ls = startLineSegment(doc)
            }
            ls.addToCoordinates(wp.longitude.value, wp.latitude.value)
            previous = wp.timestamp
        }
    }
    
    private LineString startLineSegment(Document doc)
    {
        return doc.createAndAddPlacemark()
            .withStyleUrl("#track")
            .createAndSetLineString()
            .withTessellate(Boolean.TRUE)
            .withExtrude(Boolean.TRUE)
            .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + directory.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this.is(obj))
            return true
        if (obj == null)
            return false
        if (getClass() != obj.getClass())
            return false
        Day other = (Day) obj
        if (date == null)
        {
            if (other.date != null)
                return false
        }
        else if (!date.equals(other.date))
            return false
        return directory.equals(other.directory)
    }
    
    @Override
    public int compareTo(Day other)
    {
        if(date==null)
        {
            return other.date==null ? 0 : -1                
        }
        return other.date == null ? 1 : date.compareTo(other.date)
    }    
}

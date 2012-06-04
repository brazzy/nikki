package de.brazzy.nikki.model;
/*
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.swing.table.AbstractTableModel
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.KmlFactory
import de.micromata.opengis.kml.v_2_2_0.Document
import de.micromata.opengis.kml.v_2_2_0.LineString
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

import de.brazzy.nikki.util.NikkiWorker;
import de.brazzy.nikki.util.Texts;

import javax.swing.SwingWorker

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat;

/**
 * Represents one day during a journey, as experienced subjectively by
 * a traveler (while possible passing through several time zones).
 *
 * @author Michael Borgwardt
 */
class Day extends AbstractTableModel implements Comparable<Day> {
    public static final long serialVersionUID = 1

    private static final DateTimeFormatter DISPLAY_FORMAT = ISODateTimeFormat.date()

    /**
     * Waypoints further apart than this in time will result in beginning a new path
     * (thus creating a gap) in the exported KML file
     */
    public static final Duration WAYPOINT_NEWLINE_THRESHOLD_TIME = Duration.standardSeconds(90)

    /**
     * Waypoints closer together than this (in meters) will be merged in the exported
     * KML file.
     */
    public static final int WAYPOINT_MERGE_THRESHOLD_DISTANCE = 20;

    /** Images taken on this day */
    private final ListDataModel<Image> images = new ListDataModel<Image>()

    private ImageSortField imageSortOrder

    /** Waypoints recorded on this day */
    final SortedSet<Waypoint> waypoints = new TreeSet()

    /** Date represented by this day */
    final LocalDate date

    /**
     *  Contains the files (images and GPS logs) used by this Day instance.
     *  Must not be null (Enforcement currently not possible, as Groovy
     *  ignores "private")
     */
    final Directory directory

    public Day(Map arguments){
        this.date = arguments?.date
        this.directory = arguments?.directory
        setImageSortOrder(this.date ?
                ImageSortField.TIME :
                ImageSortField.FILENAME)
    }

    public String toString() {
        return (date==null? Texts.Main.UNKNOWN_DAY : DISPLAY_FORMAT.print(date)) +
        " ("+images.size()+", "+waypoints.size()+")"
    }

    public setImageSortOrder(ImageSortField order){
        if(order == ImageSortField.TIME && !this.date) {
            throw new IllegalArgumentException("Cannot set sort order to time on unknown day")
        }
        if(imageSortOrder != order) {
            imageSortOrder = order
            images.comparator = order.comparator
            fireTableStructureChanged();
            fireTableDataChanged();
        }
    }

    /**
     * From AbstractTableModel
     */
    @Override
    public int getRowCount() {
        images.size()
    }

    /**
     * From AbstractTableModel
     */
    @Override
    public int getColumnCount() {
        1
    }

    /**
     * From AbstractTableModel
     */
    @Override
    public Object getValueAt(int row, int column) {
        images[row]
    }

    /**
     * From AbstractTableModel
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        true
    }

    /**
     * Exports this day's data (Annotated images and GPS tracks)
     * to a KMZ file
     *
     * @param out stream to write the data to
     * @param worker for updating progress
     */
    public void export(ZipOutputStream out, NikkiWorker worker) {
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
        for(Image image : images) {
            worker?.labelUpdate = image.fileName
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

    private static void createDirEntry(ZipOutputStream out, String dirName) {
        def entry = new ZipEntry(dirName);
        entry.size = 0;
        entry.crc = 0;
        out.putNextEntry(entry)
        out.closeEntry()
    }

    private void exportWaypoints(Document doc) {
        def wpList = prepareWaypoints()
        LineString ls
        DateTime previous = new DateTime(1900, 1, 1,0 ,0,0,0);
        Waypoint previousWP = wpList.first()
        for(Waypoint wp : wpList) {
            if(wp.startNewLine) {
                ls = startLineSegment(doc)
            }

            ls.addToCoordinates(wp.longitude.value, wp.latitude.value)
            previous = wp.timestamp
            previousWP = wp
        }
    }

    private LineString startLineSegment(Document doc) {
        return doc.createAndAddPlacemark()
        .withStyleUrl("#track")
        .createAndSetLineString()
        .withTessellate(Boolean.TRUE)
        .withExtrude(Boolean.TRUE)
        .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + directory.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this.is(obj))
            return true
        if (obj == null)
            return false
        if (!(obj instanceof Day))
            return false
        Day other = (Day) obj
        if (date == null) {
            if (other.date != null)
                return false
        }
        else if (!date.equals(other.date))
            return false
        return directory.equals(other.directory)
    }

    @Override
    public int compareTo(Day other) {
        if(date==null) {
            return other.date==null ? 0 : -1
        }
        return other.date == null ? 1 : date.compareTo(other.date)
    }

    /**
     * Prepare waypoints for export:
     *
     * - merge waypoints that are very close together in space
     * - mark start of new line segment
     */
    public SortedSet<Waypoint> prepareWaypoints(){
        def result = new TreeSet()
        result.addAll(images.asList().waypoint)
        if(waypoints){
            def previous = waypoints.first()
            result.add(previous)
            def previousAdded = previous
            for(Waypoint wp : waypoints){
                def dist = previousAdded.distanceInMeters(wp)
                def time = new Duration(previous.timestamp, wp.timestamp)

                if(dist >= WAYPOINT_MERGE_THRESHOLD_DISTANCE){
                    result.add(wp)
                    previousAdded = wp
                    if(time.isLongerThan(WAYPOINT_NEWLINE_THRESHOLD_TIME)){
                        wp.startNewLine = true
                    }
                }
                previous = wp
            }
        }
        if(result){
            result.iterator().next().startNewLine = true
        }
        return result;
    }
}

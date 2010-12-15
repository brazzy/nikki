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

import de.brazzy.nikki.util.ImageReader
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.Document
import de.micromata.opengis.kml.v_2_2_0.Coordinate
import de.micromata.opengis.kml.v_2_2_0.KmlFactory
import de.micromata.opengis.kml.v_2_2_0.Placemark

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import de.brazzy.nikki.util.ImageWriter
import org.joda.time.DateTime
import org.joda.time.ReadablePeriod
import org.joda.time.Seconds

class Image implements Serializable{
    public static final long serialVersionUID = 1;
    
    /** Color used for the pin that marks the image position in the offset finder */
    public static final String OFFSET_FINDER_COLOR = "801977FF";
    
    /** Size factor the pin that marks the image position in the offset finder */
    public static final double OFFSET_FINDER_SCALE = 3.0
    
    /** 
     * Set to true to show that data has been added or changed that
     * should be persisted  
     */
    boolean modified
    
    /** Name of the image data file */
    String fileName
    
    /** Briefly describes the image */
    String title
    
    /** Longer description of the image */
    String description
    
    /** Time at which the image was taken */
    DateTime time
    
    /** Day instance that contains the image */
    Day day
    
    /** Contains the GPS coordinates assigned to the image */
    Waypoint waypoint
    
    /** Scaled-down version for display */
    byte[] thumbnail
    
    /** Determines whether the image will be part of an export to KMZ */
    boolean export
    
    /**
     * Sets the modified property when other properties change content
     */
    void setProperty(String name, value) {
        if(name == "modified") {
            this.modified = value
            return
        }
        if(this[name]==value) {
            return
        }
        this.modified = true
        this.@"$name"=value
    }
    
    public String toString(){
        fileName
    }
    
    /**
     * Allows the copying of the timestamp from another image
     * when an image has none; will result in the image being
     * moved to the correct Day
     */
    public void pasteTime(DateTime time) {
        if(time != this.time) {
            if(time?.toLocalDate() == this.time?.toLocalDate()) {
                setTime(time)
            }
            else {
                def dir = day.directory
                dir.removeImage(this)
                setTime(time)
                dir.addImage(this)
            }
        }
    }
    
    private String getHtmlForExport() {
        def writer = new StringWriter()  
        def builder = new groovy.xml.MarkupBuilder(writer) 
        builder.html(){ 
            body(){ 
                img(src: "images/"+fileName)
                p(description)
            }
        } 
        return writer.toString()
    }
    
    /**
     * Saves the image data to the file's EXIF headers
     */
    public void save(File directory) {
        if(modified) {
            new ImageWriter(this, directory).saveImage()
        }
        modified = false
    }
    
    /**
     * Displays a KML file that marks the image's current position
     * as well as the position and offsets of all known waypoints,
     * to allow choosing a more correct offset for geotagging.
     */
    public void offsetFinder(OutputStream out) {
        if(!waypoint) {
            geotag()
        }
        Kml kml = KmlFactory.createKml()
        Document doc = kml.createAndSetDocument()
        
        doc.createAndAddStyle().withId("image")
                .createAndSetIconStyle()
                .withScale(OFFSET_FINDER_SCALE)
                .withColor(OFFSET_FINDER_COLOR)
        
        doc.createAndAddPlacemark()
                .withName(title)
                .withStyleUrl("#image")
                .withVisibility(true)
                .createAndSetPoint()
                .withCoordinates([
                    new Coordinate(waypoint.longitude.value, waypoint.latitude.value)
                ])
        
        NumberFormat nf = NumberFormat.getIntegerInstance();
        day.waypoints.each{ Waypoint point ->
            doc.createAndAddPlacemark()
                    .withName(nf.format((point.timestamp.millis-time.millis)/1000))
                    .withVisibility(true)
                    .createAndSetPoint()
                    .withCoordinates([
                        new Coordinate(point.longitude.value, point.latitude.value)
                    ])
        }
        
        kml.marshal(out)
        out.close()
    }
    
    /**
     * Finds and sets the Waypoint that is closest in time to this
     * image's timestamp.
     * 
     * @param offset to adjust for incorrectly set camera time
     */
    public void geotag(ReadablePeriod offset = Seconds.seconds(0), SortedSet<Waypoint> waypoints) {
        def imagetime = this.time.plus(offset)
        Waypoint imageTimestamp = new Waypoint(timestamp: imagetime)
        def beforeSet = waypoints.headSet(imageTimestamp)
        def afterSet = waypoints.tailSet(imageTimestamp)        
        def result
        
        if(afterSet.isEmpty()) // after all WPs
        {
            result = beforeSet.last()
        }
        else if(beforeSet.isEmpty()) // before all WPs
        {
            result = afterSet.first()
        }
        else if(afterSet.first().compareTo(imageTimestamp) == 0) { // direct hit
            result = afterSet.first()
        }
        else {
            def before = beforeSet.last()
            def after =  afterSet.first()
            def distBefore = Seconds.secondsBetween(before.timestamp, imagetime)
            def distAfter = Seconds.secondsBetween(imagetime, after.timestamp)
            
            if(distBefore.isGreaterThan(distAfter)) {
                result = after
            }
            else {
                result = before
            }
        }
        
        if(result.timestamp!=waypoint?.timestamp) {
            waypoint = result
            time = time.withZone(result.timestamp.zone)
            modified = true
        }
    }
    
    /**
     * Write this image to a KMZ file
     * 
     * @param out for writing image data itself to
     * @param doc for writing KML data to
     * @param imgIndex for display in title
     */
    void exportTo(ZipOutputStream out, Document doc, int imgIndex) {
        if(export) {
            def imgIndexFmt = new DecimalFormat("000 ");
            Placemark pm = doc.createAndAddPlacemark()
                    .withName(imgIndexFmt.format(imgIndex) + (title ?: ""))
                    .withDescription(htmlForExport)
                    .withVisibility(true)
            pm.createAndSetPoint()
                    .withCoordinates([
                        new Coordinate(waypoint.longitude.value, waypoint.latitude.value)
                    ])
            pm.createAndAddStyle()
                    .createAndSetIconStyle()
                    .withScale(1.5) // adjusts default icon size (64) for out icon size (96)
                    .createAndSetIcon()
                    .withHref("thumbs/"+fileName)
            
            ImageReader reader = new ImageReader(new File(day.directory.path, fileName), null)
            store(reader.scale(592, false, false), "images/"+fileName, out)
            store(reader.scale(96, true, true), "thumbs/"+fileName, out)
        }
    }
    
    /**
     * Stores one image
     * 
     * @param imgData image data
     * @param name file name
     * @param out stream to write to
     */
    private static void store(byte[] imgData, String name, ZipOutputStream out) {
        def entry = new ZipEntry(name)
        entry.size = imgData.length
        def crc = new CRC32()
        crc.update(imgData)
        entry.crc = crc.value
        out.putNextEntry(entry)
        out.write(imgData)
        out.closeEntry()
    }
}

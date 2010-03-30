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

import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.util.WaypointComparator
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
import org.joda.time.Interval
import org.joda.time.Period
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
    void setProperty(String name, value)
    {
        if(name == "modified")
        {
            this.modified = value
            return
        }
        if(this[name]==value)
        {
            return
        }
        this.modified = true
        this.@"$name"=value
    }

    /**
     * Allows the copying of the timestamp from another image
     * when an image has none; will result in the image being
     * moved to the correct Day
     */
    public void pasteTime(DateTime time)
    {
        if(time != this.time)
        {
            if(time?.toLocalDate() == this.time?.toLocalDate())
            {
                setTime(time)                
            }
            else
            {
                def dir = day.directory
                dir.removeImage(this)
                setTime(time)
                dir.addImage(this)                            
            }
        }
    }

    private String getHtmlForExport()
    {
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
    public void save(File directory)
    {
        if(modified)
        {
            new ImageWriter(this, directory).saveImage()
        }
        modified = false
    }

    /**
     * Displays a KML file that marks the image's current position
     * as well as the position and offsets of all known waypoints,
     * to allow choosing a more correct offset for geotagging.
     */
    public void offsetFinder(OutputStream out)
    {
        if(!waypoint)
        {
            day.waypoints.sort(new WaypointComparator())
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
                .withCoordinates([new Coordinate(waypoint.longitude.value, waypoint.latitude.value)])

        NumberFormat nf = NumberFormat.getIntegerInstance();
        day.waypoints.each{ Waypoint point ->
            doc.createAndAddPlacemark()
            .withName(nf.format((point.timestamp.millis-time.millis)/1000))
            .withVisibility(true)
            .createAndSetPoint()
                .withCoordinates([new Coordinate(point.longitude.value, point.latitude.value)])
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
    public void geotag(ReadablePeriod offset = Seconds.seconds(0))
    {
        def imagetime = this.time.plus(offset)
        def index = Collections.binarySearch(day.waypoints, 
                new Waypoint(timestamp: imagetime), 
                new WaypointComparator())
        def result
            
        if(index>=0) // direct hit
        {
            result = day.waypoints[index]
        }
        else if(-index==day.waypoints.size()+1) // after all WPs
        {
            result = day.waypoints[day.waypoints.size()-1]
        }
        else if(index == -1) // before all WPs
        {
            result = day.waypoints[0]
        }
        else
        {
            def before = day.waypoints[-(index+2)]
            def after = day.waypoints[-(index+1)]
            def distBefore = Seconds.secondsBetween(before.timestamp, imagetime)
            def distAfter = Seconds.secondsBetween(imagetime, after.timestamp)
                                      
            if(distBefore.isGreaterThan(distAfter))
            {
                result = after
            }
            else
            {
                result = before
            }
        }
        
        if(result.timestamp!=waypoint?.timestamp)
        {
            waypoint = result
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
    void exportTo(ZipOutputStream out, Document doc, int imgIndex)
    {
        if(export)
        {
            def imgIndexFmt = new DecimalFormat("000 ");
            Placemark pm = doc.createAndAddPlacemark()
                .withName(imgIndexFmt.format(imgIndex++) + (title ?: ""))
                .withDescription(htmlForExport)
                .withVisibility(true)
            pm.createAndSetPoint()
                .withCoordinates([new Coordinate(waypoint.longitude.value, waypoint.latitude.value)])
            pm.createAndAddStyle()
               .createAndSetIconStyle()
               .withScale(1.5) // adjusts default icon size (64) for out icon size (96)
               .createAndSetIcon()
               .withHref("thumbs/"+fileName)

            ImageReader reader = new ImageReader(new File(day.directory.path, fileName), null)
            store(reader.scale(592, false), "images/"+fileName, out)
            store(reader.scale(96, true), "thumbs/"+fileName, out)
        }
    }
    
    /**
     * Stores one image
     * 
     * @param imgData image data
     * @param name file name
     * @param out stream to write to
     */
    private static void store(byte[] imgData, String name, ZipOutputStream out)
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
}

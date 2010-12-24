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


import java.util.SortedSet;

import javax.swing.SwingWorker

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.NikkiWorker;

/**
 * Represents on filesystem directory containing images and GPS tracks
 * from one journey, visible as a list of days.
 * 
 * @author Michael Borgwardt
 */
class Directory extends ListDataModel<Day> implements Comparable<Directory> {
    public static final long serialVersionUID = 1
    
    /**
     * All the images in this directory, keyed on the file name
     */
    Map<String, Image> images = [:]
    
    /**
     * All the GPS tracks in this directory, keyed on the file name
     */
    Map<String, WaypointFile> waypointFiles = [:]
    
    /** All Waypoints found in this directory */
    SortedSet<Waypoint> waypoints = new TreeSet<Waypoint>()
    
    /**
     * This directory's filesystem path.
     *  Must not be null (Enforcement currently not possible, as Groovy
     *  ignores "private")
     */
    File path
    
    public String toString() {
        path.name+" ("+images.size()+", "+waypointFiles.size()+")"
    }
    
    /**
     * Adds an Image, creates a Day as well if necessary
     */
    public Day addImage(Image image) {
        this.images[image.fileName] = image
        if(waypoints && !image.waypoint){
            image.geotag(waypoints)
        }
        def timestamp = image.time
        if(image.waypoint?.timestamp?.zone){
            timestamp = timestamp?.withZone(image.waypoint?.timestamp?.zone)
        }
        def date = timestamp?.toLocalDate()
        def day = getDay(date)
        if(!day) {
            day = new Day(date:date, directory: this)
            this.add(day)
        } 
        day.images.add(image)
        
        def modified = image.modified
        image.day = day
        image.modified = modified
        return day
    }
    
    /**
     * Removes an Image, deletes Day if empty
     */
    public void removeImage(Image image) {
        if(!this.images.remove(image.fileName)) {
            throw new IllegalStateException("tried to remove non-present image ${image.fileName}")
        }
        
        def date = image.time?.toLocalDate()
        def day = getDay(date)
        if(day) {
            day.images.remove(image)
            
            image.day = null
            if(day.images.size() == 0 && day.waypoints.size() == 0) {
                remove(day)
            }
        }
        else {
            throw new IllegalStateException("tried to remove image for unknown day $date")
        }
    }
    
    /**
     * Removes a Waypoint, deletes Day if empty
     */
    public void removeWaypoint(Waypoint wp) {
        def date = wp.timestamp.toLocalDate()
        def day = getDay(date)
        if(day) {
            waypoints.remove(wp);
            day.waypoints.remove(wp)
            wp.day = null
            if(day.images.size() == 0 && day.waypoints.size() == 0) {
                remove(day)
            }
        }
        else {
            throw new IllegalStateException("tried to remove waypoint for unknown day $date")
        }
    }
    
    /**
     * Returns the Day in this Directory that corresponds to the given date
     */
    public Day getDay(LocalDate date) {
        int index = Collections.binarySearch(dataList, new Day(date:date))
        if(index >= 0) {
            return getAt(index)
        }
        else {
            return null
        }
    }
    
    /**
     * Saves all changed image data to the EXIF headers
     * 
     * @param worker to update progress
     * @return any exceptions encountered during the operation, keyed on file name
     */
    public Map<String, Exception> save(NikkiWorker worker) {
        worker?.progress = 0
        def count = 0
        def exceptions = [:]
        for(Image image in images.values()){
            worker?.labelUpdate = image.fileName
            try {
                image.save(this.path)
            }
            catch(Exception ex) {
                Logger.getLogger(getClass()).error(
                        "Error saving data in image " + image.fileName, ex)
                exceptions[image.fileName]=ex
            }
            worker?.progress = new Integer((int)(++count/images.size() * 100))
        }
        worker?.progress = 0
        return exceptions
    }
    
    public boolean isModified() {
        for(entry in images.values()) {
            if(entry.modified) {
                return true
            }
        }
        return false
    }
    
    /**
     * Adds waypoint file and all waypoints therein
     * to correct Day, creating new Day if necessary
     */
    public addWaypointFile(WaypointFile wf) {
        for(Waypoint wp in wf.waypoints) {
            addWaypoint(wp)
        }
        waypointFiles[wf.fileName] = wf
    }
    
    /**
     * Adds waypoint to correct Day, creating new Day if necessary
     */
    public addWaypoint(Waypoint wp) {
        def date = wp.timestamp.toLocalDate()
        Day d = getDay(date)
        if(!d) {
            d = new Day(directory: this, date: date)
            add(d)
        }
        wp.day = d
        d.waypoints.add(wp)
        waypoints.add(wp)
    }
    
    /**
     * Re-geotags all images and reassigns them to different days if necessary
     */
    public void geotag(ReadablePeriod offset = Seconds.seconds(0)){
        for(image in images.values()){
            image.geotag(waypoints)
            removeImage(image)
            addImage(image)
        }
    }
    
    @Override
    public int hashCode() {
        return path.hashCode()
    }
    @Override
    public boolean equals(Object obj) {
        if (this.is(obj))
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Directory))
            return false;
        Directory other = (Directory) obj
        return path.equals(other.path)
    }
    @Override
    public int compareTo(Directory other) {
        return path.name.compareTo(other.path.name)
    }
}

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


import javax.swing.SwingWorker
import org.joda.time.LocalDate;

/**
 * Represents on filesystem directory containing images and GPS tracks
 * from one journey, visible as a list of days.
 * 
 * @author Michael Borgwardt
 */
class Directory extends ListDataModel<Day> implements Comparable<Directory>
{
    public static final long serialVersionUID = 1;
    
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
     *  Must not be null (Enforcement currently not possible, as Groovy
     *  ignores "private")
     */
    File path
    
    public String toString()
    {
        path.name+" ("+images.size()+", "+waypointFiles.size()+")"
    }

    /**
     * Adds an Image, creates a Day as well if necessary
     */
    public void addImage(Image image)
    {
        this.images[image.fileName] = image
        def date = image.time?.toLocalDate()
        def day = getDay(date)
        if(day)
        {
        }
        else
        {
            day = new Day(date:date, directory: this)
            this.add(day)
        } 
        day.images.add(image)
        if(image.waypoint)
        {
            day.waypoints.add(image.waypoint)
        }
        
        def modified = image.modified
        image.day = day
        image.modified = modified
    }

    /**
     * Removes an Image, deletes Day if empty
     */
    public void removeImage(Image image)
    {
        if(!this.images.remove(image.fileName))
        {
            throw new IllegalStateException("tried to remove non-present image ${image.fileName}")
        }
        
        def date = image.time?.toLocalDate()
        def day = getDay(date)
        if(day)
        {
            day.images.remove(image)
            image.day = null
            if(day.images.size() == 0)
            {
                remove(day)
            }
        }
        else
        {
            throw new IllegalStateException("tried to remove image for unknown day $date")            
        }
    }

    /**
     * Returns the Day in this Directory that corresponds to the given date
     */
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
    
    public boolean isModified()
    {
        for(entry in images.values())
        {
            if(entry.modified)
            {
                return true
            }
        }
        return false
    }

    /**
     * Adds waypoint file and all waypoints therein
     * to correct Day, creating new Day if necessary
     */
    private addWaypointFile(WaypointFile wf)
    {
        for(Waypoint wp in wf.waypoints)
        {
            def date = wp.timestamp.toLocalDate()
            Day d = getDay(date)
            if(!d)
            {
                d = new Day(directory: this, date: date)
                add(d)
            }
            wp.day = d
            d.waypoints.add(wp)            
        }
        waypointFiles[wf.fileName] = wf
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
}

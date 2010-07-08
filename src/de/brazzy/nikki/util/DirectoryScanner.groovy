package de.brazzy.nikki.util

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

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.SwingWorker;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.WaypointFile;
import de.brazzy.nikki.util.log_parser.LogParser;
import de.brazzy.nikki.util.log_parser.ParserFactory;

/**
 * Populates Directory instances with image and GPS data
 * 
 * @author Michael Borgwardt
 */
class DirectoryScanner {
    
    private static final def FILTER_JPG = { dir, name ->
        name.toUpperCase().endsWith(".JPG")
    } as FilenameFilter
    
    /** finds time zones for waypoints */
    TimezoneFinder finder;
    
    /** Yields parsers for parsing GPS logs */
    ParserFactory parserFactory;
    
    /**
     * time zone to which the camera time was set when the images were taken. 
     * Can be null, which assumes that all images already have time zone
     * set in their EXIF data
     */
    DateTimeZone zone;
    
    /**
     * Scans a directory for image and GPS files and populates it with 
     * the data in them
     * 
     * @param dir the directory to scan
     * @param worker for updating progress
     * @return ScanResult.TIMEZONE_MISSING if zone was null and images were found that
     *         have no time zone in their EXIF data
     */
    public ScanResult scan(Directory dir, SwingWorker worker){
        worker?.progress = 0;
        
        int count = 0;
        def imageFiles = dir.path.listFiles(FILTER_JPG)
        
        for(file in imageFiles){
            if(!dir.images[file.name]){
                ImageReader reader = new ImageReader(file, this.zone)
                if(reader.timeZone==null){
                    return ScanResult.TIMEZONE_MISSING
                }
                dir.addImage(reader.createImage())
            }
            
            worker?.progress = new Integer((int)(++count / imageFiles.length * 100))
        }
        
        parseWaypointFiles(dir)
        removeMissing(dir)
        
        dir.fireContentsChanged(dir, 0, dir.size-1)
        worker?.progress = 0
        return ScanResult.COMPLETE
    }
    
    private removeMissing(Directory dir){
        def files = dir.path.list() as Set;
        def toRemove = []
        for(image in dir.images.values()){
            if(!files.contains(image.fileName)){
                toRemove.add(image)
            }
        }
        for(image in toRemove){
            dir.removeImage(image)
        }
        
        toRemove = []
        for(waypointFile in dir.waypointFiles.values()){
            if(!files.contains(waypointFile.fileName)){
                toRemove.add(waypointFile)
            }
        }
        for(waypointFile in toRemove){
            dir.waypointFiles.remove(waypointFile.fileName)
            for(waypoint in waypointFile.waypoints){
                dir.removeWaypoint(waypoint)
            }
        }
    }
    
    private parseWaypointFiles(Directory dir){
        def parserMap = parserFactory.findParsers(dir.path, dir.path.list())
        
        for(entry in parserMap){
            if(!dir.waypointFiles[entry.key]){
                def wf = parseWaypointFile(new File(dir.path, entry.key), entry.value)
                wf.directory = dir
                dir.addWaypointFile(wf)
            }
        }
    }
    
    /**
     * Parses one GPS log file
     */
    public WaypointFile parseWaypointFile(File file, LogParser parser){
        WaypointFile wf = new WaypointFile(fileName: file.getName())
        def wpIterator = parser.parse(new BufferedInputStream(new FileInputStream(file)))
        while(wpIterator.hasNext()){
            def wp = wpIterator.next()
            def wpZone = finder.find(wp.latitude.value, wp.longitude.value)
            if(wpZone) {
                wp.timestamp = wp.timestamp.withZone(wpZone)                
            }
            wp.file = wf
            wf.waypoints.add(wp)
        }
        return wf
    }
}

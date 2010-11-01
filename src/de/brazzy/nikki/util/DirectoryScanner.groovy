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
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import slash.navigation.base.BaseNavigationPosition;
import slash.navigation.base.NavigationFormat;

import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.Directory;
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.model.WaypointFile;
import de.brazzy.nikki.util.ParserFactory;

/**
 * Populates Directory instances with image and GPS data
 * 
 * @author Michael Borgwardt
 */
class DirectoryScanner {
    
    /** finds time zones for waypoints */
    TimezoneFinder finder
    
    /** Yields parsers for parsing GPS logs */
    ParserFactory parserFactory
    
    /**
     * time zone to which the camera time was set when the images were taken. 
     * Can be null, which assumes that all images already have time zone
     * set in their EXIF data
     */
    DateTimeZone zone
    
    /**
     * Any exceptions encountered during scanning, keyed on file name
     */
    Map<String, Exception> exceptions = [:]
    
    /**
     * Scans a directory for image and GPS files and populates it with 
     * the data in them
     * 
     * @param dir the directory to scan
     * @param worker for updating progress
     * @return ScanResult.TIMEZONE_MISSING if zone was null and images were found that
     *         have no time zone in their EXIF data
     */
    public ScanResult scan(Directory dir, NikkiWorker worker){
        worker?.progress = 0
        
        int count = 0;
        def allFiles = dir.path.list() as Set;
        def imageFiles = allFiles.findAll{
            it.toUpperCase().endsWith(".JPG") ||
            it.toUpperCase().endsWith(".JPEG")
        }
        def otherFiles = new HashSet(allFiles)
        otherFiles.removeAll(imageFiles)
        otherFiles = otherFiles.findAll{
            !new File(dir.path, it).isDirectory() &&
            !it.toUpperCase().endsWith(".AVI") &&
            !it.toUpperCase().endsWith(".THM") &&            
            !it.toUpperCase().endsWith(".DB")
        }
        def totalFileNum = imageFiles.size() + otherFiles.size()
        
        removeMissing(dir, allFiles)
        parseWaypointFiles(dir, otherFiles, worker)
        
        for(fileName in imageFiles){
            worker?.labelUpdate = fileName
            if(!dir.images[fileName]){
                ImageReader reader = new ImageReader(new File(dir.path, fileName), this.zone)
                if(reader.timeZone==null){
                    return ScanResult.TIMEZONE_MISSING
                }
                dir.addImage(reader.createImage())
            }
            
            worker?.progress = new Integer((int)(++count / totalFileNum * 100))
        }
        
        dir.fireContentsChanged(dir, 0, dir.size-1)
        worker?.progress = 0
        return ScanResult.COMPLETE
    }
    
    private removeMissing(Directory dir, Set<String> files){
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
    
    private parseWaypointFiles(Directory dir, Set<String> files, NikkiWorker worker){
        for(fileName in files){
            worker?.labelUpdate = fileName
            if(!dir.waypointFiles[fileName]){
                try{
                    def wf = parseWaypointFile(new File(dir.path, fileName))
                    wf.directory = dir
                    dir.addWaypointFile(wf)
                }catch(Exception e){
                    Logger.getLogger(getClass()).error(
                            "Error reading waypoint file " + fileName, e)
                    exceptions[fileName] = e
                }
            }
        }
    }
    
    
    /**
     * Parses one GPS log file
     */
    public WaypointFile parseWaypointFile(File file){
        WaypointFile wf = new WaypointFile(fileName: file.getName())
        NavigationFormat format = parserFactory.findParser(file)
        def routes = format.read(new BufferedInputStream(new FileInputStream(file)))
        def positions = routes*.positions.flatten()
        
        for(BaseNavigationPosition pos in positions){
            if(pos.latitude && pos.longitude && pos.time){
                def lat = new GeoCoordinate(magnitude: Math.abs(pos.latitude), 
                        direction: pos.latitude > 0 ? Cardinal.NORTH : Cardinal.SOUTH)
                def lon = new GeoCoordinate(magnitude: Math.abs(pos.longitude), 
                        direction: pos.longitude > 0 ? Cardinal.EAST : Cardinal.WEST)
                def ts =  new DateTime(pos.time.timeInMillis, DateTimeZone.UTC)
                def zone  = finder.find(lat.value, lon.value)                
                if(zone){
                    ts = ts.withZone(zone)
                }
                
                def wp = new Waypoint(latitude: lat, longitude: lon, timestamp: ts)
                wp.file = wf
                wf.waypoints.add(wp)                
            }
        }
        return wf
    }
}

package de.brazzy.nikki.util

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

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.SwingWorker;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;
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
    private static final def FILTER_NMEA = { dir, name ->
        name.toUpperCase().endsWith(".NMEA")
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
            if(!dir.images[file.name])
            {
                ImageReader reader = new ImageReader(file, this.zone)
                if(reader.timeZone==null)
                {
                    return ScanResult.TIMEZONE_MISSING
                }
                dir.addImage(reader.createImage())
            }
            
            worker?.progress = new Integer((int)(++count / imageFiles.length * 100))
        }

        parseWaypointFiles(dir)
        
        dir.fireContentsChanged(dir, 0, dir.size-1)
        worker?.progress = 0
        return ScanResult.COMPLETE
    }
    
    private parseWaypointFiles(Directory dir)
    {
        def parserMap = parserFactory.findParsers(dir.path, dir.path.list())
        
        for(entry in parserMap){
            if(!dir.waypointFiles[entry.key])
            {
                def wf = parseWaypointFile(new File(dir.path, entry.key), entry.value)
                wf.directory = dir
                dir.addWaypointFile(wf)
            }
        }
    }
    
    /**
     * Parses one GPS log file
     */
    public WaypointFile parseWaypointFile(File file, LogParser parser)
    {
        WaypointFile wf = new WaypointFile(fileName: file.getName())
        def wpIterator = parser.parse(new BufferedInputStream(new FileInputStream(file)))
        while(wpIterator.hasNext())
        {
            def wp = wpIterator.next()
            def wpZone = finder.find(wp.latitude.value, wp.longitude.value)
            if(wpZone)
            {
                wp.timestamp = wp.timestamp.withZone(wpZone)                
            }
            wp.file = wf
            wf.waypoints.add(wp)
        }
        return wf
    }
}

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

import org.joda.time.DateTimeZone

import de.brazzy.nikki.util.TimezoneFinder

/**
 * Represents a GPS log file
 */
class WaypointFile implements Serializable{
    public static final long serialVersionUID = 1;

    /** All the waypoints in the file*/
    List<Waypoint> waypoints = [];
    
    /** Contains this file*/
    Directory directory;
    
    String fileName;

    /**
     * Parses a GPS log file in NMEA format
     * 
     * @param dir contains the file
     * @param file the file itself
     * @param finder used to assign the timezone of the nearsed town to waypoints
     */
    public static WaypointFile parse(Directory dir, File file, TimezoneFinder finder)
    {
        def result = new WaypointFile(directory: dir, fileName: file.name)
        file.eachLine{ line ->
            if(line.startsWith('$GPRMC'))
            {
                result.waypoints.add(Waypoint.parse(result, line, finder))
            }
        }
        return result;
    }
}

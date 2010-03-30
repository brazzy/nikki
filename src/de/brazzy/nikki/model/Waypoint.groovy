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

import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import de.brazzy.nikki.util.TimezoneFinder

/**
 * An entry in a GPS track, with timestamp and GPS coordinates
 */
class Waypoint implements Serializable{
    public static final long serialVersionUID = 1;
    
    private static final DateTimeFormatter PARSE_FORMAT = DateTimeFormat.forPattern('ddMMyyHHmmss.SSS').withZone(DateTimeZone.UTC)

    /** GPS log file in which the waypoint is recorded */
    WaypointFile file

    /** Day to which the waypoint is assigned (based on timezone of nearest town) */
    Day day
    
    /** Timestamp of the log entry (based on timezone of nearest town) */
    DateTime timestamp
    
    GeoCoordinate latitude
    
    GeoCoordinate longitude
    
    /**
     * Parses a log entry in NMEA format
     * 
     * @param wpFile file in which the waypoint is recorded
     * @param line log entry data
     * @param finder used to assign the timezone of the nearest town
     */
    public static Waypoint parse(WaypointFile wpFile, String line, TimezoneFinder finder)
    {        
        def result = new Waypoint(file:wpFile)
        
        def data = line.trim().tokenize(',')        
        result.latitude = GeoCoordinate.parse(data[3], data[4])
        result.longitude = GeoCoordinate.parse(data[5], data[6])        
        DateTimeZone zone = finder.find((float)result.latitude.value, (float)result.longitude.value)
        def timestamp = PARSE_FORMAT.parseDateTime(data[9]+data[1])        
        result.timestamp = zone!=null ? timestamp.withZone(zone) : timestamp
                
        setDay(result)

        return result;
    }
    
    private static setDay(Waypoint result)
    {
        def wpFile = result.file
        def date = result.timestamp.toLocalDate()
        Day d = wpFile?.directory?.getDay(date)
        if(!d)
        {
            d = new Day(directory: wpFile?.directory, date: date)
            wpFile?.directory?.add(d)
        }
        result.day = d
        d.waypoints.add(result)
    }
    
    public String toString()
    {
        return timestamp.toString()
    }
}

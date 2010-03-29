package de.brazzy.nikki.model;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.util.TimezoneFinder;

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

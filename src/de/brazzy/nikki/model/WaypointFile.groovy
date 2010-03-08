package de.brazzy.nikki.model;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.util.TimezoneFinder;

class WaypointFile implements Serializable{
    public static final long serialVersionUID = 1;

    List<Waypoint> waypoints = [];
    Directory directory;
    String fileName;

    public static WaypointFile parse(Directory dir, File file, TimezoneFinder finder)
    {
        def result = new WaypointFile(directory: dir, fileName: file.name)
        file.eachLine{ line ->
            if(line.startsWith('$GPRMC'))
            {
                def wp = Waypoint.parse(result, line, finder)
                result.waypoints.add(wp)
            }
        }
        return result;
    }
}

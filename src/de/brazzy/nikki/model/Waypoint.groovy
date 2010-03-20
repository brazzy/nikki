package de.brazzy.nikki.model;

import java.text.SimpleDateFormat
import java.text.DateFormat

import org.joda.time.DateTime;
import org.joda.time.Instant
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

import de.brazzy.nikki.util.TimezoneFinder;

class Waypoint implements Serializable{
    public static final long serialVersionUID = 1;
    
    WaypointFile file
    Day day
    DateTime timestamp
    GeoCoordinate latitude
    GeoCoordinate longitude
    
    public static Waypoint parse(WaypointFile wpFile, String line, TimezoneFinder finder)
    {
        
        def result = new Waypoint()
        result.file = wpFile
        def data = line.trim().tokenize(',')
        result.latitude = GeoCoordinate.parse(data[3], data[4])
        result.longitude = GeoCoordinate.parse(data[5], data[6])
        
        DateTimeZone zone = finder.find((float)result.latitude.value, (float)result.longitude.value)
        def PARSE_FORMAT = DateTimeFormat.forPattern('ddMMyyHHmmss.SSS').withZone(zone ?: DateTimeZone.UTC)        
        result.timestamp = PARSE_FORMAT.parseDateTime(data[9]+data[1])

        def date = result.timestamp.toLocalDate()
        Day d = wpFile?.directory?.data?.find{
            it.date == date
        }
        if(!d)
        {
            d = new Day(directory: wpFile?.directory, date: date)
            wpFile?.directory?.add(d)
        }
        result.day = d
        d.waypoints.add(result)            

        return result;
    }
    
    public String toString()
    {
        return timestamp.toString()
    }
}

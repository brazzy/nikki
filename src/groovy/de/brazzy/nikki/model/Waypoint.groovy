package de.brazzy.nikki.model;

import java.text.SimpleDateFormatimport java.text.DateFormatimport java.util.TimeZoneimport de.brazzy.nikki.util.RelativeDateFormatclass Waypoint implements Serializable, Comparable{
    public static final long serialVersionUID = 1;
    
    WaypointFile file;
    Day day;
    Date timestamp;
    GeoCoordinate latitude;
    GeoCoordinate longitude;
    
    public static Waypoint parse(Directory dir, WaypointFile wpFile, String line)
    {
        final PARSE_FORMAT = new SimpleDateFormat('ddMMyyHHmmss.SSS')
        PARSE_FORMAT.timeZone = TimeZone.getTimeZone(TimeZone.GMT_ID)
        
        def result = new Waypoint()
        result.file = wpFile
        def data = line.trim().tokenize(',')
        result.timestamp = PARSE_FORMAT.parse(data[9]+data[1])
        result.latitude = GeoCoordinate.parse(data[3], data[4])
        result.longitude = GeoCoordinate.parse(data[5], data[6])

        def dateFformat = new RelativeDateFormat(dir.zone)

	    Day d = dir.data.find{
            dateFformat.sameDay(it.date, result.timestamp)
	    }
        if(!d)
        {
            d = new Day(directory: dir, date: dateFformat.stripTime(result.timestamp))
            dir.add(d)
        }
        result.day = d
        d.waypoints.add(result)            

        return result;
    }
    
    public int compareTo(Object o)
    {
        return this.timestamp.compareTo(o.timestamp)
    }
    
    public String toString()
    {
        return timestamp.toString()
    }
}

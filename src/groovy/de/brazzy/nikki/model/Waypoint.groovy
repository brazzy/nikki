package de.brazzy.nikki.model;

import java.text.SimpleDateFormatimport java.text.DateFormatimport java.util.TimeZoneclass Waypoint implements Serializable, Comparable{
    public static final long serialVersionUID = 1;
    private static final DATE_FORMAT = new SimpleDateFormat('ddMMyyHHmmss.SSS')
    
    WaypointFile file;
    Day day;
    Date timestamp;
    GeoCoordinate latitude;
    GeoCoordinate longitude;
    
    public static Waypoint parse(Directory dir, WaypointFile wpFile, String line)
    {
        def result = new Waypoint()
        result.file = wpFile
        def data = line.trim().tokenize(',')
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(TimeZone.GMT_ID))
        result.timestamp = DATE_FORMAT.parse(data[9]+data[1])
        result.latitude = GeoCoordinate.parse(data[3], data[4])
        result.longitude = GeoCoordinate.parse(data[5], data[6])
        
	    Day d = dir.data.find{
	        it.date.dateString.equals(result.timestamp.dateString)
	    }
        if(!d)
        {
            d = new Day(directory: dir, date: DateFormat.getDateInstance().parse(result.timestamp.dateString))
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

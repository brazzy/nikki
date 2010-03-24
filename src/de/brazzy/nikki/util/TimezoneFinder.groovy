package de.brazzy.nikki.util;

import org.joda.time.DateTimeZone;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

/**
 * Responsible for finding the timezones in which GPS waypoints lie, which is necessary
 * to determine to which subjective day a waypoint belongs. This is done by using
 * a database derived from geonames.org to find the settlement that is geographically
 * closest to the waypoint and using its time zone.
 * 
 * @see de.brazzy.nikki.util.PrepTimezoneData
 * @see timezones.dat
 * 
 * @author Michael Borgwardt
 */
class TimezoneFinder {
    
    /**
     * Spatial index to find the geographically nearest settlement,
     * value is an index into the zones list.
     * 
     * TODO: this breaks down near the poles and the international date
     * line. This could be fixed by having several overlapping indices
     * using transformed coordinates.
     */
    RTree tree
    
    /**
     * List of time zones in no particular order.
     */
    List<DateTimeZone> zones
    
    /**
     * Creates a finder containing no data, which can be used for tests
     */
    public TimezoneFinder()
    {
        this.zones = []
        this.tree = new RTree()
        tree.init(new Properties())
    }

    /**
     * Parses the list of time zones and settelements
     * 
     * @param zoneData input to parse
     * 
     * @see de.brazzy.nikki.util.PrepTimezoneData
     * @see timezones.dat
     */
    public TimezoneFinder(InputStream zoneData)
    {
        ObjectInputStream data = new ObjectInputStream(zoneData)
        for(def zone=data.readUTF();zone != "";zone=data.readUTF())
        {
            zones.add(DateTimeZone.forID(zone))
        }
        
        def lat=data.readFloat()
        while(!Float.isNaN(lat))
        {
            def lng=data.readFloat()
            def zone=data.readShort()
            tree.add(new Rectangle(lat, lng, lat, lng), zone)
            lat=data.readFloat()
        }
    }

    /**
     * Finds the timezone of a waypoint
     * 
     * @return the timezone of the settelement geographically
     *         closest to the coordinates
     */
    public DateTimeZone find(float latitude, float longitude)
    {
        def result;
        def point = new Point(latitude, longitude)
        def callback = { result = zones[it]; return false } as IntProcedure
        tree.nearest(point, callback, Float.POSITIVE_INFINITY)
        return result
    }

}

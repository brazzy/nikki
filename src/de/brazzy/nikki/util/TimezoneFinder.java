package de.brazzy.nikki.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
public class TimezoneFinder {
    
    /**
     * Spatial index to find the geographically nearest settlement,
     * value is an index into the zones list.
     * 
     * TODO: this breaks down near the poles and the international date
     * line. This could be fixed by having several overlapping indices
     * using transformed coordinates.
     */
    private RTree tree;
    
    /**
     * List of time zones in no particular order.
     */
    private List<DateTimeZone> zones;
    
    /**
     * Creates a finder containing no data, which can be used for tests
     */
    public TimezoneFinder()
    {
        this.zones = new ArrayList<DateTimeZone>();
        this.tree = new RTree();
        tree.init(new Properties());
    }

    /**
     * Parses the list of time zones and settelements
     * 
     * @param zoneData input to parse
     * 
     * @see de.brazzy.nikki.util.PrepTimezoneData
     * @see timezones.dat
     */
    public TimezoneFinder(InputStream zoneData) throws IOException
    {
        this();
        ObjectInputStream data = new ObjectInputStream(zoneData);
        parseZones(data);
        parseTowns(data);
    }

    private void parseTowns(ObjectInputStream data) throws IOException
    {
        float lat=data.readFloat();
        while(!Float.isNaN(lat))
        {
            float lng=data.readFloat();
            short zone=data.readShort();
            tree.add(new Rectangle(lat, lng, lat, lng), zone);
            lat=data.readFloat();
        }
    }
    
    private void parseZones(ObjectInputStream data) throws IOException
    {
        for(String zone=data.readUTF();!zone.equals("");zone=data.readUTF())
        {
            zones.add(DateTimeZone.forID(zone));
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
        final DateTimeZone[] result = new DateTimeZone[1];
        Point point = new Point(latitude, longitude);
        IntProcedure callback = new IntProcedure(){
            public boolean execute(int id)
            {
                result[0] = zones.get(id);
                return false; 
            }};
        tree.nearest(point, callback, Float.POSITIVE_INFINITY);
        return result[0];
    }

}

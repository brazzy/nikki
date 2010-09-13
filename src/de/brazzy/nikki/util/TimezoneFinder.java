package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

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
 * Responsible for finding the timezones in which GPS waypoints lie, which is
 * necessary to determine to which subjective day a waypoint belongs. This is
 * done by using a database derived from geonames.org to find the settlement
 * that is geographically closest to the waypoint and using its time zone.
 * 
 * @see de.brazzy.nikki.util.PrepTimezoneData
 * @see timezones.dat
 * 
 * @author Michael Borgwardt
 */
public class TimezoneFinder {

    /**
     * Spatial index to find the geographically nearest settlement, value is an
     * index into the zones list.
     */
    private RTree tree;

    /**
     * List of time zones in no particular order.
     */
    private List<DateTimeZone> zones;

    /**
     * Creates a finder containing no data, which can be used for tests
     */
    public TimezoneFinder() {
        this.zones = new ArrayList<DateTimeZone>();
        this.tree = new RTree();
        Properties props = new Properties();
        props.put("MaxNodeEntries", "50");
        props.put("MinNodeEntries", "20");
        tree.init(props);
    }

    /**
     * Parses the list of time zones and settelements
     * 
     * @param zoneData
     *            input to parse
     * 
     * @see de.brazzy.nikki.util.PrepTimezoneData
     * @see timezones.dat
     */
    public TimezoneFinder(InputStream zoneData) throws IOException {
        this();
        ObjectInputStream data = new ObjectInputStream(zoneData);
        parseZones(data);
        parseTowns(data);
    }

    private void parseTowns(ObjectInputStream data) throws IOException {
        float lat = data.readFloat();
        while (!Float.isNaN(lat)) {
            float lng = data.readFloat();
            short zone = data.readShort();
            tree.add(new Rectangle(lat, lng, lat, lng), zone);
            lat = data.readFloat();
        }
    }

    private void parseZones(ObjectInputStream data) throws IOException {
        for (String zone = data.readUTF(); !zone.equals(""); zone = data
                .readUTF()) {
            zones.add(DateTimeZone.forID(zone));
        }
    }

    /**
     * Finds the timezone of a waypoint
     * 
     * @return the timezone of the settelement geographically closest to the
     *         coordinates
     */
    public DateTimeZone find(double latitude, double longitude) {
        return find((float) latitude, (float) longitude);
    }

    /**
     * Finds the timezone of a waypoint
     * 
     * @return the timezone of the settelement geographically closest to the
     *         coordinates
     */
    public DateTimeZone find(float latitude, float longitude) {
        final DateTimeZone[] result = new DateTimeZone[1];
        Point point = new Point(latitude, longitude);
        IntProcedure callback = new IntProcedure() {
            public boolean execute(int id) {
                result[0] = zones.get(id);
                return false;
            }
        };
        tree.nearest(point, callback, Float.POSITIVE_INFINITY);
        return result[0];
    }

}

package de.brazzy.nikki.test

import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import de.brazzy.nikki.model.Waypoint
import java.util.Date
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import java.text.DateFormat
import java.text.SimpleDateFormat
import de.brazzy.nikki.model.Image
/**
 * @author Brazil
 */
public class WaypointTest extends GroovyTestCase{


    public void testCoordValue()
    {
            GeoCoordinate a;
            a = new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: 1.5d)
            assertEquals(-1.5d, a.value)
            a = new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 1.0d)
            assertEquals(1.0d, a.value)
            a = new GeoCoordinate(direction: Cardinal.EAST, magnitude: 2.0d)
            assertEquals(2.0d, a.value)
            a = new GeoCoordinate(direction: Cardinal.WEST, magnitude: 3.0d)
            assertEquals(-3.0d, a.value)
    }

    public void testParseCoord()
    {
        def coord = GeoCoordinate.parse("4810.0900","S")
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.SOUTH)
        assertTrue(coord.value.toString(), -49 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -48)

        coord = GeoCoordinate.parse("01134.9470","W")
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.WEST)
        assertTrue(coord.value.toString(), -12 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -11)
    }
    
    public void testParseWaypoint()
    {
        final String line = '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D'
        Waypoint wp = Waypoint.parse(new Directory(), null, line);

        assertEquals(wp.timestamp, Date.parse("yyyy-MM-dd HH:mm:ss.SSS Z", "2009-07-27 07:12:32.000 GMT"))

        def coord = wp.latitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.NORTH)
        assertTrue(coord.value.toString(), 48 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 49)

        coord = wp.longitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.EAST)
        assertTrue(coord.value.toString(), 11 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 12)
    }

    public void testParseWaypointFile()
    {
        // TODO
    }

}

package de.brazzy.nikki.test

import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.WaypointFile
import java.text.DateFormat
import java.text.SimpleDateFormat

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

        assertEquals(wp.timestamp, Date.parse("yyyy-MM-dd HH:mm:ss.SSS z", "2009-07-27 07:12:32.000 GMT"))

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
        def fmt = new SimpleDateFormat("z yyyy-MM-dd HH:mm:ss");
        Directory dir = new Directory()

        WaypointFile f = WaypointFile.parse(dir, new File(getClass().getResource(AbstractNikkiTest.WAYPOINTS1).toURI()))

        assertSame(dir, f.directory)
        assertEquals(AbstractNikkiTest.WAYPOINTS1, f.fileName)
        assertEquals(2, f.waypoints.size())

        Waypoint wp1 = f.waypoints[0]
        assertSame(f, wp1.file)
        assertEquals(fmt.parse("GMT 2009-11-11 05:09:04"), wp1.timestamp)
        assertTrue(133 < wp1.longitude.value)
        assertTrue(wp1.longitude.value < 134)
        assertTrue(-24 < wp1.latitude.value)
        assertTrue(wp1.latitude.value < -23)

        Waypoint wp2 = f.waypoints[1]
        assertSame(f, wp2.file)
        assertEquals(fmt.parse("GMT 2009-11-11 06:00:33"), wp2.timestamp)
        assertTrue(wp1.longitude.value < wp2.longitude.value)
        assertTrue(wp2.latitude.value > wp1.latitude.value)
    }

}

package de.brazzy.nikki.test

import de.brazzy.nikki.model.GeoCoordinateimport de.brazzy.nikki.model.Cardinalimport de.brazzy.nikki.model.Waypointimport java.util.Dateimport de.brazzy.nikki.model.Directory/**
 * @author Brazil
 */
public class WaypointParseTest extends GroovyTestCase{

    public void testCoord()
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
    
    public void testWaypoint()
    {
        final String line = '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D'
        Waypoint wp = Waypoint.parse(new Directory(), null, line);

        assertEquals(wp.timestamp, Date.parse("yyyy-MM-dd HH:mm:ss.SSS", "2009-07-27 07:12:32.000"))

        def coord = wp.longitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.NORTH)
        assertTrue(coord.value.toString(), 48 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 49)

        coord = wp.latitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.EAST)
        assertTrue(coord.value.toString(), 11 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 12)
    }
}

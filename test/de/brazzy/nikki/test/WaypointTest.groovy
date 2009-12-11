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
    
    public void testGeotag()
    {
        def fmt = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        def dat = "GMT 2009-01-01 12:"
        
        def day = new Day()
        def wp12 = new Waypoint(timestamp: fmt.parse(dat+"12:00"))
        def wp14 = new Waypoint(timestamp: fmt.parse(dat+"14:00"))
        def wp16 = new Waypoint(timestamp: fmt.parse(dat+"16:00"))
        
        def im11 = new Image(time: fmt.parse(dat+"11:00"))
        def im13l = new Image(time: fmt.parse(dat+"12:45"))
        def im15h = new Image(time: fmt.parse(dat+"15:10"))
        def im17 = new Image(time: fmt.parse(dat+"17:00"))
        def im12 = new Image(time: fmt.parse(dat+"12:00"))
        def im14 = new Image(time: fmt.parse(dat+"14:00"))
        def im16 = new Image(time: fmt.parse(dat+"16:00"))
        
        day.images = [im13l, im11, im14, im16, im12, im17, im15h]
        day.waypoints = [wp14, wp16, wp12]
        
        day.geotag()
        
        assertSame(wp12, im12.waypoint)
        assertSame(wp14, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp12, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
    }

}

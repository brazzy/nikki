package de.brazzy.nikki.test
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.util.TimezoneFinder

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * @author Michael Borgwardt
 */
public class WaypointTest extends GroovyTestCase{

    private static final DateTimeZone TZ_2 = DateTimeZone.forID("Etc/GMT-2")
    
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

    public void testTimezoneShift()
    {
        assertEquals(1000*60*60*2, TZ_2.getStandardOffset(0))
        assertEquals(new DateTime(2009, 7, 27, 7, 12, 32, 0, DateTimeZone.UTC),
                new DateTime(2009, 7, 27, 7+2, 12, 32, 0, TZ_2))  
    }

    public void testParseWaypointFile()
    {
        final MockTimezoneFinder finder = new MockTimezoneFinder()
        finder.addCall(-24f, 133.2f, TZ_2)
        finder.addCall(-23.7f, 133.8f, null)
        
        Directory dir = new Directory()

        WaypointFile f = WaypointFile.parse(dir, new File(getClass().getResource(AbstractNikkiTest.WAYPOINTS1).toURI()), finder)
        finder.finished()

        assertSame(dir, f.directory)
        assertEquals(AbstractNikkiTest.WAYPOINTS1, f.fileName)
        assertEquals(2, f.waypoints.size())

        Waypoint wp1 = f.waypoints[0]
        assertEquals(TZ_2, wp1.timestamp.zone)
        assertSame(f, wp1.file)
        assertEquals(new DateTime(2009, 11, 11, 5+2, 9, 4, 0, TZ_2), wp1.timestamp)
        assertTrue(133 < wp1.longitude.value)
        assertTrue(wp1.longitude.value < 134)
        assertTrue(-24 < wp1.latitude.value)
        assertTrue(wp1.latitude.value < -23)

        Waypoint wp2 = f.waypoints[1]
        assertEquals(DateTimeZone.UTC, wp2.timestamp.zone)
        assertSame(f, wp2.file)
        assertEquals(new DateTime(2009, 11, 11, 6 , 0, 33, 0, DateTimeZone.UTC), wp2.timestamp)
        assertTrue(wp1.longitude.value < wp2.longitude.value)
        assertTrue(wp2.latitude.value > wp1.latitude.value)
    }
}

private class MockTimezoneFinder extends TimezoneFinder
{
    def queue = []
    
    public addCall(float lat, float lng, DateTimeZone result)
    {
        queue.add([lat, lng, result])
    }
    
    public DateTimeZone find(float latitude, float longitude)
    {
        def entry = queue.remove(0)
        assert Math.abs(entry[0] - latitude) < 0.1, "error: $latitude"
        assert Math.abs(entry[1] - longitude) < 0.1, "error: $longitude"
        return entry[2]
    }
    
    public void finished()
    {
        assert queue.size() == 0
    }
}


package de.brazzy.nikki.test;
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

import static org.junit.Assert.*;
import de.brazzy.nikki.model.Day;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.model.WaypointFile;
import de.brazzy.nikki.util.DirectoryScanner;
import de.brazzy.nikki.util.ScanResult;
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.util.ParserFactory;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author Michael Borgwardt
 *
 */
class DirectoryScannerTest extends AbstractNikkiTest {
    DirectoryScanner scanner = new DirectoryScanner(
    finder:new MockTimezoneFinder(),
    parserFactory:new ParserFactory())
    
    public void testScan() {
        scanner.finder.addCall(Float.NaN,Float.NaN, DateTimeZone.UTC)
        scanner.finder.addCall(Float.NaN,Float.NaN, DateTimeZone.UTC)
        
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        
        assertEquals(ScanResult.COMPLETE, scanner.scan(tmpDir, null))
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, tmpDir.size())
        
        Day day = tmpDir[0]
        Image image = day.images[0]
        
        assertSame(tmpDir, day.directory)
        assertEquals(3, day.waypoints.size())
        assertEquals(1, day.images.size())
        assertSame(day.images[0], tmpDir.images[IMAGE1])
        assertSame(day.waypoints.first(), 
                tmpDir.waypointFiles[WAYPOINTS1].waypoints[0])
        assertSame(day.waypoints.headSet(day.waypoints.last()).last(), 
                tmpDir.waypointFiles[WAYPOINTS1].waypoints[1])
        assertSame(day.waypoints.last(), image.waypoint)
        assertEquals(DAY1, day.date)
        assertEquals(DATE1+" (1, 3)", day.toString())
        
        assertEquals(IMAGE1, image.fileName)
        assertEquals("Überschrift", image.title)
        assertEquals(TZ_DARWIN, image.time.zone)
        assertNotNull(image.thumbnail)
        assertSame(day, image.day)
        assertEquals(DAY1, image.time.toLocalDate())
        assertFalse(image.modified)
    }
    
    public void testRescan() {
        scanner.finder.addCall(Float.NaN,Float.NaN, DateTimeZone.UTC)
        scanner.finder.addCall(Float.NaN,Float.NaN, DateTimeZone.UTC)
        
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        Image image = addImage(DAY1, IMAGE1)
        WaypointFile file = addWaypointFile(DAY1, WAYPOINTS1)
        Day day1 = tmpDir[0] 
        
        assertEquals(1, tmpDir.size())
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, day1.images.size())
        assertEquals(3, day1.waypoints.size())
        
        copyFile(IMAGE2)
        copyFile(WAYPOINTS2)
        
        assertEquals(ScanResult.TIMEZONE_MISSING, scanner.scan(tmpDir, null))
        assertEquals(1, tmpDir.size())
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, day1.images.size())
        assertEquals(3, day1.waypoints.size())
        
        scanner.zone = ZONE
        assertEquals(ScanResult.COMPLETE, scanner.scan(tmpDir, null))
        assertEquals(2, tmpDir.size())
        assertEquals(2, tmpDir.images.size())
        assertEquals(2, tmpDir.waypointFiles.size())
        
        assertEquals(DAY1, day1.date)
        assertSame(day1.directory, tmpDir)
        assertEquals(3, day1.waypoints.size())
        assertSame(file.waypoints[0], day1.waypoints.first())
        assertSame(file.waypoints[1], day1.waypoints.headSet(day1.waypoints.last()).last())
        
        assertEquals(1, day1.images.size())
        Image image1 = day1.images[0]
        assertSame(image1, tmpDir.images[IMAGE1])
        assertEquals("testTitle", image1.title)
        assertEquals("testDescription", image1.description)
        assertEquals(day1, image1.day)
        assertEquals(THUMB, image1.thumbnail)
        assertTrue(image1.export)
        assertEquals(TIME1, image1.time)
        Waypoint wp = image1.waypoint
        assertNotNull(wp)
        assertEquals(day1, wp.day)
        assertEquals(TIME1, wp.timestamp)
        assertEquals(-5d, wp.latitude.value, 0.001)
        assertEquals(25d, wp.longitude.value, 0.001)
        
        Day day2 = tmpDir[1]
        
        assertEquals(2, day2.waypoints.size())
        assertEquals(1, day2.images.size())
        Image image2 = day2.images[0]
        assertEquals(IMAGE2, image2.fileName)
        assertNull(image2.title)
        assertNull(image2.description)
        assertNull(image2.waypoint)
        assertFalse(image2.export)
        assertNotNull(image2.thumbnail)
        assertSame(day2, image2.day)
        assertEquals(DAY2, image2.time.toLocalDate())
        assertEquals(ZONE, image2.time.zone)
        assertTrue(Math.abs(day2.waypoints.last().latitude.value+23) < 1.0)
        assertEquals(DAY2, day2.waypoints.last().timestamp.toLocalDate())
    }
    
    public void testRescanRemove() {
        copyFile(IMAGE1)
        copyFile(WAYPOINTS2)
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY2, IMAGE2)
        WaypointFile file1 = addWaypointFile(DAY1, WAYPOINTS1)
        WaypointFile file2 = addWaypointFile(DAY2, WAYPOINTS2)
        Day day1 = tmpDir[0] 
        Day day2 = tmpDir[1] 
        
        assertEquals(2, tmpDir.size())
        assertEquals(2, tmpDir.images.size())
        assertEquals(2, tmpDir.waypointFiles.size())
        assertEquals(1, day1.images.size)
        assertEquals(3, day1.waypoints.size())
        assertEquals(1, day2.images.size)
        assertEquals(3, day2.waypoints.size())
        
        scanner.zone = ZONE
        assertEquals(ScanResult.COMPLETE, scanner.scan(tmpDir, null))
        
        assertEquals(2, tmpDir.size())
        assertEquals(1, tmpDir.images.size())
        assertSame(image1, tmpDir.images.values().iterator().next())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertSame(file2, tmpDir.waypointFiles.values().iterator().next())
        assertEquals(1, day1.images.size)
        assertEquals(1, day1.waypoints.size())
        assertSame(day1.images[0].waypoint, day1.waypoints.first())
        assertEquals(0, day2.images.size)
        assertEquals(2, day2.waypoints.size())
        assertEquals(day2.waypoints as Set, file2.waypoints as Set)
        
        assertTrue(new File(tmpDir.path, image1.fileName).delete())
        
        assertEquals(ScanResult.COMPLETE, scanner.scan(tmpDir, null))
        assertEquals(1, tmpDir.size())
        assertEquals(0, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        day1 = tmpDir[0] 
        assertEquals(0, day1.images.size)
        assertEquals(2, day1.waypoints.size())
        assertEquals(day1.waypoints as Set, file2.waypoints as Set)
    }
    
    public void testParseWaypointFile() {
        scanner.finder.addCall(-24f, 133.2f, TZ_2)
        scanner.finder.addCall(-23.7f, 133.8f, null)
        
        
        WaypointFile f = scanner.parseWaypointFile(
                new File(getClass().getResource(AbstractNikkiTest.WAYPOINTS1).toURI()),)
        scanner.finder.finished()
        
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
    
    private static final DateTimeZone TZ_2 = DateTimeZone.forID("Etc/GMT-2")
    
    /**
     * Tests understanding of illogical timezone names
     * ("Etc/GMT-2" has offset of +2)
     */
    public void testTimezoneShift() {
        assertEquals(1000*60*60*2, TZ_2.getStandardOffset(0))
        assertEquals(new DateTime(2009, 7, 27, 7, 12, 32, 0, DateTimeZone.UTC).toInstant(),
                new DateTime(2009, 7, 27, 7+2, 12, 32, 0, TZ_2).toInstant())  
    }
}

private class MockTimezoneFinder extends TimezoneFinder {
    def queue = []
    
    public addCall(float lat, float lng, DateTimeZone result) {
        queue.add([lat, lng, result])
    }
    
    public DateTimeZone find(float latitude, float longitude) {
        def entry = queue.remove(0)
        if(!Float.isNaN(entry[0])) {
            assert Math.abs(entry[0] - latitude) < 0.1, "error: $latitude"            
        }
        if(!Float.isNaN(entry[1])) {
            assert Math.abs(entry[1] - longitude) < 0.1, "error: $longitude"
        }
        return entry[2]
    }
    
    public void finished() {
        assert queue.size() == 0
    }
}

package de.brazzy.nikki.test;
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

import static org.junit.Assert.*;
import de.brazzy.nikki.model.Day;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.model.WaypointFile;
import de.brazzy.nikki.util.DirectoryScanner;
import de.brazzy.nikki.util.ScanResult;
import de.brazzy.nikki.util.TimezoneFinder;

/**
 * @author Michael Borgwardt
 *
 */
class DirectoryScannerTest extends AbstractNikkiTest
{
    DirectoryScanner scanner = new DirectoryScanner(finder:new TimezoneFinder())
    
    public void testScan()
    {
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
        assertSame(day.waypoints[0], image.waypoint)
        assertSame(day.waypoints[1], tmpDir.waypointFiles[WAYPOINTS1].waypoints[0])
        assertSame(day.waypoints[2], tmpDir.waypointFiles[WAYPOINTS1].waypoints[1])
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

    public void testRescan()
    {
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        Image image = addImage(DAY1, IMAGE1)
        WaypointFile file = addWaypointFile(DAY1, WAYPOINTS1)
        Day day1 = tmpDir[0] 
        day1.waypoints = [file.waypoints[0],file.waypoints[1]]
        
        assertEquals(1, tmpDir.size())
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, day1.images.size())
        assertEquals(2, day1.waypoints.size())

        copyFile(IMAGE2)
        copyFile(WAYPOINTS2)

        assertEquals(ScanResult.TIMEZONE_MISSING, scanner.scan(tmpDir, null))
        assertEquals(1, tmpDir.size())
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, day1.images.size())
        assertEquals(2, day1.waypoints.size())

        scanner.zone = ZONE
        assertEquals(ScanResult.COMPLETE, scanner.scan(tmpDir, null))
        assertEquals(2, tmpDir.size())
        assertEquals(2, tmpDir.images.size())
        assertEquals(2, tmpDir.waypointFiles.size())

        assertEquals(DAY1, day1.date)
        assertSame(day1.directory, tmpDir)
        assertEquals(2, day1.waypoints.size())
        assertSame(file.waypoints[0], day1.waypoints[0])
        assertSame(file.waypoints[1], day1.waypoints[1])

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
        assertEquals(TIME1, wp.timestamp, )
        assertEquals(-5d, wp.latitude.value)
        assertEquals(25d, wp.longitude.value)

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
        assertTrue(Math.abs(day2.waypoints[1].latitude.value+23) < 1.0)
        assertEquals(DAY2, day2.waypoints[1].timestamp.toLocalDate())
    }
}

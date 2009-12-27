package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import de.brazzy.nikki.util.RelativeDateFormat
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * TODO: rescan mit entfernten images und waypointFiles
 *
 * @author Brazil
 */
public class DirectoryTest extends AbstractNikkiTest {

    public void testDirectoryToString()
    {
        assertEquals(tmpDir.path.name+" (0, 0)", tmpDir.toString())
        tmpDir.images[IMAGE1] = new Image(fileName: IMAGE1);
        assertEquals(tmpDir.path.name+" (1, 0)", tmpDir.toString())
        tmpDir.images[IMAGE2] = new Image(fileName: IMAGE2);
        assertEquals(tmpDir.path.name+" (2, 0)", tmpDir.toString())
        tmpDir.waypointFiles[IMAGE1] = new WaypointFile(fileName: IMAGE1);
        assertEquals(tmpDir.path.name+" (2, 1)", tmpDir.toString())
        tmpDir.images.remove(IMAGE1)
        tmpDir.waypointFiles.remove(IMAGE1)
        assertEquals(tmpDir.path.name+" (1, 0)", tmpDir.toString())
    }

    public void testScan()
    {
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)

        tmpDir.scan(null)
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, tmpDir.size())

        Day day = tmpDir[0]
        assertSame(tmpDir, day.directory)
        assertEquals(2, day.waypoints.size())
        assertEquals(1, day.images.size())
        assertSame(day.images[0], tmpDir.images[IMAGE1])
        assertSame(day.waypoints[0], tmpDir.waypointFiles[WAYPOINTS1].waypoints[0])
        assertSame(day.waypoints[1], tmpDir.waypointFiles[WAYPOINTS1].waypoints[1])
        assertEquals(DAY1, day.date)
        assertEquals("$DATE1 (1, 2)", day.toString())

        Image image = day.images[0]
        assertEquals(IMAGE1, image.fileName)
        assertNull(image.title)
        assertNull(image.description)
        assertNull(image.waypoint)
        assertFalse(image.export)
        assertNotNull(image.thumbnail)
        assertSame(day, image.day)
        assertEquals(DAY1, FORMAT.stripTime(image.time))
    }

    public void testSave()
    {
        Image image = constructImage(DAY1, IMAGE1)
        tmpDir.images[IMAGE1] = image
        WaypointFile file = constructWaypointFile(DAY1, WAYPOINTS1)
        tmpDir.waypointFiles[WAYPOINTS1] = file

        assertEquals(0, tmpDir.path.list().length)
        assertFalse(tmpDir.hasPersistent())
        tmpDir.save()
        assertTrue(tmpDir.hasPersistent())
        assertEquals(1, tmpDir.path.list().length)
    }

    public void testRescan()
    {
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        Image image = constructImage(DAY1, IMAGE1)
        tmpDir.images[IMAGE1] = image
        WaypointFile file = constructWaypointFile(DAY1, WAYPOINTS1)
        tmpDir.waypointFiles[WAYPOINTS1] = file
        tmpDir.save()

        tmpDir = new Directory(path: tmpDir.path)
        copyFile(IMAGE2)
        copyFile(WAYPOINTS2)
        assertEquals(TimeZone.getDefault(), tmpDir.zone)

        tmpDir.scan(null)
        assertEquals(2, tmpDir.size())
        assertEquals(2, tmpDir.images.size())
        assertEquals(2, tmpDir.waypointFiles.size())
        assertEquals(ZONE, tmpDir.zone)

        Day day1 = tmpDir[0]
        Day day2 = tmpDir[1]

        assertEquals(2, day1.waypoints.size())
        assertEquals(DAY1, day1.date)
        assertSame(day1.directory, tmpDir)
        assertEquals(file.waypoints[0].latitude.value, day1.waypoints[0].latitude.value)
        assertEquals(file.waypoints[0].timestamp, day1.waypoints[0].timestamp)

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

        assertEquals(1, day2.images.size())
        Image image2 = day2.images[0]
        assertEquals(IMAGE2, image2.fileName)
        assertNull(image2.title)
        assertNull(image2.description)
        assertNull(image2.waypoint)
        assertFalse(image2.export)
        assertNotNull(image2.thumbnail)
        assertSame(day2, image2.day)
        assertEquals(DAY2, FORMAT.stripTime(image2.time))
        assertTrue(Math.abs(day2.waypoints[1].latitude.value+23) < 1.0)
        assertEquals(DAY2, FORMAT.stripTime(day2.waypoints[1].timestamp))
    }

}


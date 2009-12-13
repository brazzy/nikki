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
 * TODO: scan/rescan mit waypointFiles
 *
 * @author Brazil
 */
public class DirectoryTest extends GroovyTestCase {
    private static final TimeZone ZONE = TimeZone.getTimeZone("GMT+10")
    private static final RelativeDateFormat FORMAT = new RelativeDateFormat(ZONE)
    private static final String DATE1 = "2009-11-11";
    private static final String DATE2 = "2009-11-12";
    private static final String IMAGE1 = "IMG${DATE1}.JPG";
    private static final String IMAGE2 = "IMG${DATE2}.JPG";
    private static final Date DAY1 = FORMAT.stripTime(FORMAT.parse(DATE1));
    private static final Date DAY2 = FORMAT.stripTime(FORMAT.parse(DATE2));
    private static final Date TIME = new Date(DAY1.time+(60*60*5))
    private static final byte[] THUMB = [1 , 2 , 3] as byte[]

    Directory tmpDir;

    public void setUp()
    {
        File tmpFile = File.createTempFile("nikkitest",null)
        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()
        tmpDir = new Directory(path: tmpFile)
        tmpDir.zone = ZONE
    }

    private void copyFile(String name)
    {
        IOUtils.copy(DirectoryTest.class.getResourceAsStream(name),
            new FileOutputStream(new File(tmpDir.path, name)))
    }

    private Image constructImage()
    {
        Day day = new Day(date: DAY1, directory:tmpDir)
        tmpDir.add(day);
        Waypoint wp = new Waypoint(day: day, timestamp: TIME,
            latitude: new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: 1.5d),
            longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 10d))
        Image image = new Image(fileName: IMAGE1, title:"testTitle",
            description:"testDescription", day: day, thumbnail: THUMB,
            export: true, time: TIME, waypoint: wp)
        day.images.add(image)
        return image
    }

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

        tmpDir.scan(null)
        assertEquals(1, tmpDir.images.size())
        assertEquals(0, tmpDir.waypointFiles.size())
        assertEquals(1, tmpDir.size())

        Day day = tmpDir[0]
        assertSame(tmpDir, day.directory)
        assertEquals(0, day.waypoints.size())
        assertEquals(1, day.images.size())
        assertSame(day.images[0], tmpDir.images[IMAGE1])
        assertEquals(DAY1, day.date)
        assertEquals("$DATE1 (1, 0)", day.toString())

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
        Image image = constructImage()
        tmpDir.images[IMAGE1] = image

        assertEquals(0, tmpDir.path.list().length)
        assertFalse(tmpDir.hasPersistent())
        tmpDir.save()
        assertTrue(tmpDir.hasPersistent())
        assertEquals(1, tmpDir.path.list().length)
    }

    public void testRescan()
    {
        copyFile(IMAGE1)
        Image image = constructImage()
        tmpDir.images[IMAGE1] = image
        tmpDir.save()

        tmpDir = new Directory(path: tmpDir.path);
        copyFile(IMAGE2);
        assertEquals(TimeZone.getDefault(), tmpDir.zone)

        tmpDir.scan(null)
        assertEquals(2, tmpDir.size())
        assertEquals(2, tmpDir.images.size())
        assertEquals(0, tmpDir.waypointFiles.size())
        assertEquals(ZONE, tmpDir.zone)

        Day day1 = tmpDir[0]
        Day day2 = tmpDir[1]

        assertEquals(0, day1.waypoints.size())
        assertEquals(DAY1, day1.date)
        assertSame(day1.directory, tmpDir)

        assertEquals(1, day1.images.size())
        Image image1 = day1.images[0]
        assertSame(image1, tmpDir.images[IMAGE1])
        assertEquals("testTitle", image1.title)
        assertEquals("testDescription", image1.description)
        assertEquals(day1, image1.day)
        assertEquals(THUMB, image1.thumbnail)
        assertTrue(image1.export)
        assertEquals(TIME, image1.time)
        Waypoint wp = image1.waypoint
        assertNotNull(wp)
        assertEquals(day1, wp.day)
        assertEquals(TIME, wp.timestamp, )
        assertEquals(-1.5d, wp.latitude.value)
        assertEquals(10d, wp.longitude.value)

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
    }

}


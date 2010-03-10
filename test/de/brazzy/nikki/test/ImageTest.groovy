package de.brazzy.nikki.test
import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.model.Rotation
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Waypoint
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils;
import java.util.Arrays

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone

/**
 * @author Brazil
 */
class ImageTest extends AbstractNikkiTest{

    ImageReader reader

    public void setUp()
    {
        super.setUp()
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-11.JPG").getFile()),
            DateTimeZone.UTC)
    }

    public void testTimezone()
    {
        assertEquals(TZ_DARWIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 11, 19, 10, 27, 0, DateTimeZone.forID("Australia/Darwin")), 
                     reader.time)
        def image = reader.createImage()
        assertEquals(TZ_DARWIN, image.time.zone)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").getFile()),
                TZ_BERLIN)
        assertEquals(TZ_BERLIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 12, 10, 10, 10, 0, TZ_BERLIN), 
                     reader.time)
        image = reader.createImage()
        assertEquals(TZ_BERLIN, image.time.zone)

        def deflt = DateTimeZone.default
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").getFile()),
            null)
        assertEquals(deflt, reader.timeZone)
        image = reader.createImage()
        assertEquals(deflt, image.time.zone)
    }

    public void testThumbnail()
    {
        assertEquals(Rotation.LEFT, reader.rotation)
        def thumb = reader.createImage().thumbnail
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(120, thumb.width)
        assertEquals(160, thumb.height)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").getFile()),
            TZ_BERLIN)
        assertEquals(Rotation.NONE, reader.rotation)
        thumb = reader.createImage().thumbnail
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(180, thumb.width)
        assertEquals(135, thumb.height)

    }

    public void testReadExif()
    {
        assertTrue(reader.export)
        assertEquals("Australia/Darwin", reader.timeZone.ID)
        assertEquals("Überschrift", reader.title)
        assertEquals("Kommentar\näöüß", reader.description)
        assertNotNull(reader.waypoint)
        assertEquals(45.5f, reader.waypoint.latitude.value)
        assertEquals(-16.5f, reader.waypoint.longitude.value)
    }

    public void testOffsetFinder()
    {
        TimezoneFinder tzFinder = new TimezoneFinder();
        Image im = reader.createImage()
        im.title="testTitle"
        Waypoint wp1 = Waypoint.parse(null, '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D', tzFinder)
        Waypoint wp2 = Waypoint.parse(null, '$GPRMC,071245.000,A,4810.1900,N,01134.9770,E,000.00,0.0,270709,,,E*5D', tzFinder)
        im.time = wp1.timestamp
        Day d = new Day(waypoints: [ wp1, wp2])
        im.day = d
        def out = new ByteArrayOutputStream()
        im.offsetFinder(out)
        def str = new String(out.toByteArray())
        def finder = new XmlSlurper().parseText(new String(out.toByteArray()))
        def style = finder.Document.Style.IconStyle
        assertNotNull(style)
        def col = style.color
        assertEquals(Image.OFFSET_FINDER_COLOR, style.color.text())
        assertEquals(String.valueOf(Image.OFFSET_FINDER_SCALE), String.valueOf(style.scale.text()))

        def placemarks = finder.Document.Placemark
        assertEquals(3, placemarks.size())
        assertEquals("testTitle", placemarks[0].name.text())
        assertEquals("#image", placemarks[0].styleUrl.text())
        assertEquals("0", placemarks[1].name.text())
        assertNotNull(placemarks[1].Point)
        assertEquals("13", placemarks[2].name.text())
        assertNotNull(placemarks[2].Point)
    }

    public void testSaveImage()
    {
        copyFile(IMAGE2)
        long baseTime = System.currentTimeMillis()-10000000
        File file = new File(tmpDir.path, IMAGE2)
        assertTrue(file.setLastModified(baseTime))

        Image image = constructImage(DAY2, IMAGE2)
        tmpDir.images[IMAGE2] = image

        assertTrue(file.lastModified() == baseTime)
        image.save(tmpDir.path)
        assertFalse(file.lastModified() == baseTime)

        assertTrue(file.setLastModified(baseTime))
        image.save(tmpDir.path)
        assertTrue(file.lastModified() == baseTime)

        image.description = "Ü\nß"
        image.title = "ä#'\n\n<>"
        image.export = false
        byte[] th = image.thumbnail
        image.save(tmpDir.path)
        assertFalse(file.lastModified() == baseTime)

        ImageReader reader = new ImageReader(file, null)
        assertEquals(ZONE.ID, reader.timeZone.ID)
        assertEquals("Ü\nß", reader.description)
        assertEquals("ä#'\n\n<>", reader.title)
        assertEquals(-5.0f, reader.waypoint.latitude.value)
        assertEquals(25.0f, reader.waypoint.longitude.value)
        assertFalse(reader.export)
        def thumb = reader.thumbnail
        assertTrue(Arrays.equals(thumb, th))
    }

    private void checkPropertyModified(def image, def propName, def newValue)
    {
        assertFalse(image.modified)
        image[propName] = image[propName]
        assertFalse(image.modified)
        image[propName] = newValue
        assertTrue(image.modified)
        image.modified = false
    }

    public void testModified()
    {
        copyFile(IMAGE1)
        Image image = reader.createImage()
        checkPropertyModified(image, 'title', 'changed')
        checkPropertyModified(image, 'description', 'changed')
        def origName = image.fileName
        checkPropertyModified(image, 'fileName', 'changed')
        checkPropertyModified(image, 'time', new DateTime())
        checkPropertyModified(image, 'day', new Day())
        checkPropertyModified(image, 'thumbnail', null)
        checkPropertyModified(image, 'export', false)
        checkPropertyModified(image, 'waypoint', null)
        image.fileName = origName
        assertTrue(image.modified)
        image.save(tmpDir.path)
        assertFalse(image.modified)
    }
}


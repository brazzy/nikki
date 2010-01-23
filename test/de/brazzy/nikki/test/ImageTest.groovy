package de.brazzy.nikki.test
import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.model.Rotation
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Waypoint
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils;
import java.util.Arrays

/**
 * @author Brazil
 */
class ImageTest extends AbstractNikkiTest{

    ImageReader reader

    public void setUp()
    {
        super.setUp()
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-11.JPG").getFile()),
            TimeZone.getTimeZone("GMT"))
    }

    public void testTimezone()
    {
        def fmt = new SimpleDateFormat("z yyyy-MM-dd HH:mm:ss");
        assertEquals("Australia/North", reader.timeZone.ID)
        assertEquals(fmt.parse("GMT 2009-11-11 09:40:27"), reader.time)
        def image = reader.createImage()
        assertEquals("Australia/North", image.zone.ID)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").getFile()),
            TimeZone.getTimeZone("CET"))
        assertEquals("CET", reader.timeZone.ID)
        assertEquals(fmt.parse("GMT 2009-11-12 09:10:10"), reader.time)
        image = reader.createImage()
        assertEquals("CET", image.zone.ID)

        def deflt = TimeZone.getDefault()
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").getFile()),
            null)
        assertEquals(deflt.ID, reader.timeZone.ID)
        image = reader.createImage()
        assertEquals(deflt.ID, image.zone.ID)
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
            TimeZone.getTimeZone("GMT"))
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
        assertEquals("Australia/North", reader.timeZone.ID)
        assertEquals("Überschrift", reader.title)
        assertEquals("Kommentar\näöüß", reader.description)
        assertNotNull(reader.waypoint)
        assertEquals(45.5f, reader.waypoint.latitude.value)
        assertEquals(-16.5f, reader.waypoint.longitude.value)
    }

    public void testOffsetFinder()
    {
        Image im = reader.createImage()
        im.title="testTitle"
        Waypoint wp1 = Waypoint.parse(new Directory(), null, '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D')
        Waypoint wp2 = Waypoint.parse(new Directory(), null, '$GPRMC,071245.000,A,4810.1900,N,01134.9770,E,000.00,0.0,270709,,,E*5D')
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
        copyFile(IMAGE1)
        long baseTime = System.currentTimeMillis()-10000000
        File file = new File(tmpDir.path, IMAGE1)
        assertTrue(file.setLastModified(baseTime))

        Image image = constructImage(DAY1, IMAGE1)
        tmpDir.images[IMAGE1] = image

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
        checkPropertyModified(image, 'time', new Date())
        checkPropertyModified(image, 'zone', TimeZone.getTimeZone("GMT"))
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


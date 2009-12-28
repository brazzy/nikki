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

/**
 * @author Brazil
 */
class ImageTest extends GroovyTestCase{

    ImageReader reader

    public void setUp()
    {
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-11.JPG").getFile()),
            TimeZone.getTimeZone("GMT"))
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
        // TODO: Einlesen vorhandener Geodaten
        def fmt = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        assertEquals(fmt.parse("GMT 2009-11-11 19:10:27"), reader.getTime())
    }

//    public void testTitle()
//    {
//        // TODO: Einlesen der Nikki-Daten
//    }

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
}


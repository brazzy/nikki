package de.brazzy.nikki.test
import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.model.Rotation
import java.text.SimpleDateFormat
import javax.imageio.ImageIO

/**
 * @author Brazil
 */
class ImageTest extends GroovyTestCase{

    ImageReader reader

    public void setUp()
    {
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-11.JPG").toURI()),
            TimeZone.getTimeZone("GMT"))
    }

    public void testThumbnail()
    {
        // TODO
        assertEquals(Rotation.LEFT, reader.rotation)
        def thumb = reader.createImage().thumbnail
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(120, thumb.width)
        assertEquals(160, thumb.height)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").toURI()),
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
        // TODO
        // Einlesen vorhandener Geodaten
        def fmt = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        assertEquals(fmt.parse("GMT 2009-11-11 19:10:27"), reader.getTime())
    }

    public void testTitle()
    {
        // TODO
        // Einlesen der Nikki-Daten
    }

    public void offsetFinder()
    {
        // TODO
    }
}


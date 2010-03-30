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

import de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.util.ImageWriter;
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.model.Rotation
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Waypoint
import javax.imageio.ImageIO

import mediautil.image.jpeg.Entry;

import org.apache.commons.io.IOUtils;
import java.util.Arrays

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate;

/**
 * @author Michael Borgwardt
 */
class ImageTest extends AbstractNikkiTest{

    ImageReader reader

    public void setUp()
    {
        super.setUp()
        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-11.JPG").toURI()),
            DateTimeZone.UTC)
    }

    public void testTimezone()
    {
        assertNotNull(reader.nikkiIFD)
        assertEquals(TZ_DARWIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 11, 19, 10, 27, 0, DateTimeZone.forID("Australia/Darwin")), 
                     reader.time)
        def image = reader.createImage()
        assertEquals(TZ_DARWIN, image.time.zone)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").toURI()),
                TZ_BERLIN)
        assertEquals(TZ_BERLIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 12, 10, 10, 10, 0, TZ_BERLIN), 
                     reader.time)
        image = reader.createImage()
        assertEquals(TZ_BERLIN, image.time.zone)

        reader = new ImageReader(new File(getClass().getResource("IMG2009-11-12.JPG").toURI()),
            null)
        assertNull(reader.timeZone)
        image = reader.createImage()
        assertNull(image.time)
    }

    public void testThumbnail()
    {
        assertNotNull(reader.exifData)
        assertEquals(Rotation.LEFT, reader.rotation)
        assertNull(reader.isThumbnailNew())
        def thumb = reader.createImage().thumbnail
        assertSame(Boolean.FALSE, reader.isThumbnailNew())
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(120, thumb.width)
        assertEquals(160, thumb.height)

        reader = new ImageReader(new File(getClass().getResource(IMAGE2).toURI()),
            TZ_BERLIN)
        assertEquals(Rotation.NONE, reader.rotation)
        assertNull(reader.thumbnailNew)
        thumb = reader.createImage().thumbnail
        assertSame(Boolean.TRUE, reader.isThumbnailNew())
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(180, thumb.width)
        assertEquals(135, thumb.height)

        reader = new ImageReader(new File(getClass().getResource(NO_EXIF).toURI()),
                TZ_BERLIN)
        assertNull(reader.thumbnailNew)
        assertEquals(Rotation.NONE, reader.rotation)
        thumb = reader.createImage().thumbnail
        assertSame(Boolean.TRUE, reader.isThumbnailNew())
        assertNotNull(thumb)
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(180, thumb.width)
        assertEquals(180, thumb.height)
    }

    public void testReadExif()
    {
        assertNotNull(reader.nikkiIFD)
        assertNotNull(reader.gpsIFD)
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

        Image image = addImage(DAY2, IMAGE2)

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
        
        copyFile(NO_EXIF)
        reader = new ImageReader(new File(tmpDir.path, NO_EXIF),
                TZ_BERLIN)
        image = reader.createImage()
        image.time = TIME2
        assertTrue(reader.thumbnailNew)
        assertTrue(image.modified)
        th = image.thumbnail
        image.save(tmpDir.path)
        reader = new ImageReader(new File(tmpDir.path, NO_EXIF),
                null)
        assertNotNull(reader.timeZone)
        image = reader.createImage()
        thumb = image.thumbnail
        assertFalse(reader.thumbnailNew)
        assertFalse(image.modified)
        assertEquals(TIME2, image.time)
        assertTrue(Arrays.equals(thumb, th))
        thumb = ImageIO.read(new ByteArrayInputStream(thumb))
        assertEquals(180, thumb.width)
        assertEquals(180, thumb.height)
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
        assertFalse(image.modified)
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
        
        image.day = new Day(date:new LocalDate(2009,1,1))
        image.day.waypoints = [constructWaypoint(image.day, 1)]
        image.modified=false
        image.geotag()        
        assertTrue(image.modified)
        image.save(tmpDir.path)
        assertFalse(image.modified)
    }
    
    public void testCoordinatePrecision()
    {
        double start = 12.38599967956543;
        Entry e = ImageWriter.writeGpsMagnitude(start)
        print e
        double end = ImageReader.readGpsMagnitude(e)
        assert Math.abs(start-end) < 0.00001d
    }
    
    public void testCopyPasteTime()
    {
        Image imageWithDate = addImage(DAY1, IMAGE1)
        Image imageNoDate1 = addImage(null, NO_EXIF)
        Image imageNoDate2 = addImage(null, IMAGE2)
        imageNoDate1.modified = false
        imageNoDate2.modified = false
        
        assertEquals(2, tmpDir.getSize())
        assertEquals(null, tmpDir[0].date)
        assertEquals(2, tmpDir[0].images.size())
        assertSame(imageNoDate1, tmpDir[0].images[0])
        assertSame(imageNoDate2, tmpDir[0].images[1])
        assertEquals(DAY1, tmpDir[1].date)
        assertEquals(1, tmpDir[1].images.size())
        assertSame(imageWithDate, tmpDir[1].images[0])
        assertFalse(imageNoDate1.modified)
        
        imageNoDate1.pasteTime(TIME2)
        
        assertTrue(imageNoDate1.modified)
        assertEquals(3, tmpDir.getSize())
        assertEquals(null, tmpDir[0].date)
        assertEquals(1, tmpDir[0].images.size())
        assertSame(imageNoDate2, tmpDir[0].images[0])
        assertEquals(DAY1, tmpDir[1].date)
        assertEquals(1, tmpDir[1].images.size())
        assertSame(imageWithDate, tmpDir[1].images[0])
        assertEquals(DAY2, tmpDir[2].date)
        assertEquals(1, tmpDir[2].images.size())
        assertSame(imageNoDate1, tmpDir[2].images[0])
        assertFalse(imageNoDate2.modified)
        
        imageNoDate2.pasteTime(TIME1)
        
        assertTrue(imageNoDate2.modified)
        assertEquals(2, tmpDir.getSize())
        assertEquals(DAY1, tmpDir[0].date)
        assertEquals(2, tmpDir[0].images.size())
        assertSame(imageWithDate, tmpDir[0].images[0])
        assertSame(imageNoDate2, tmpDir[0].images[1])
        assertEquals(DAY2, tmpDir[1].date)
        assertEquals(1, tmpDir[1].images.size())
        assertSame(imageNoDate1, tmpDir[1].images[0])
        
        imageWithDate.pasteTime(TIME1.plusMinutes(10))
        assertSame(imageWithDate, tmpDir[0].images[0])
        assertSame(imageNoDate2, tmpDir[0].images[1])
        assertEquals(TIME1.plusMinutes(10), imageWithDate.time)
    }
}


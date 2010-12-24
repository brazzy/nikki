package de.brazzy.nikki.test
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

import javax.imageio.ImageIO;

import mediautil.image.jpeg.Entry;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Day;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Rotation;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.util.ImageReader;
import de.brazzy.nikki.util.ImageWriter;
import de.brazzy.nikki.util.TimezoneFinder;

/**
 * @author Michael Borgwardt
 */
class ImageTest extends AbstractNikkiTest{
    
    ImageReader reader
    
    public void setUp() {
        super.setUp()
        reader = new ImageReader(new File(getClass().getResource(IMAGE1).toURI()),
                DateTimeZone.UTC)
    }
    
    public void testTimezone() {
        assertNotNull(reader.nikkiIFD)
        assertEquals(TZ_DARWIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 11, 19, 10, 27, 0, TZ_DARWIN), 
                reader.time)
        def image = reader.createImage()
        assertEquals(TZ_DARWIN, image.time.zone)
        
        reader = new ImageReader(new File(getClass().getResource(IMAGE2).toURI()),
                TZ_BERLIN)
        assertEquals(TZ_BERLIN, reader.timeZone)
        assertEquals(new DateTime(2009, 11, 12, 10, 10, 10, 0, TZ_BERLIN), 
                reader.time)
        image = reader.createImage()
        assertEquals(TZ_BERLIN, image.time.zone)
        
        reader = new ImageReader(new File(getClass().getResource(IMAGE2).toURI()),
                null)
        assertNull(reader.timeZone)
        image = reader.createImage()
        assertNull(image.time)
    }
    
    public void testThumbnail() {
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
    
    public void testReadExif() {
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
    
    public void testOffsetFinder() {
        def DIFF_SECONDS = 13
        TimezoneFinder tzFinder = new TimezoneFinder();
        Image im = reader.createImage()
        im.title="testTitle"
        Day d = new Day(date: DAY1)
        Waypoint wp1 = constructWaypoint(d, 1);
        Waypoint wp2 = constructWaypoint(d, 2);
        wp2.timestamp = wp1.timestamp.plusSeconds(DIFF_SECONDS)
        im.time = wp1.timestamp
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
        assertEquals(DIFF_SECONDS as String, placemarks[2].name.text())
        assertNotNull(placemarks[2].Point)
    }
    
    public void testSaveImage() {
        def IMAGE_INDEX = 2;
        copyFile(IMAGE2)
        long baseTime = System.currentTimeMillis()-10000000
        File file = new File(tmpDir.path, IMAGE2)
        assertTrue(file.setLastModified(baseTime))
        addWaypointFile(DAY2, "dummy");
        
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
        assertEquals(-(double)IMAGE_INDEX, reader.waypoint.latitude.value)
        assertEquals((double)IMAGE_INDEX+20, reader.waypoint.longitude.value)
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
    
    private void checkPropertyModified(def image, def propName, def newValue) {
        assertFalse(image.modified)
        image[propName] = image[propName]
        assertFalse(image.modified)
        image[propName] = newValue
        assertTrue(image.modified)
        image.modified = false
    }
    
    public void testModified() {
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
        constructWaypoint(image.day, 1)
        image.modified=false
        image.geotag(image.day.waypoints)        
        assertTrue(image.modified)
        image.save(tmpDir.path)
        assertFalse(image.modified)
    }
    
    public void testCoordinatePrecision() {
        double start = 12.38599967956543
        Entry e = ImageWriter.writeGpsMagnitude(start)
        double end = ImageReader.readGpsMagnitude(e)
        assert Math.abs(start-end) < 0.00001d
    }
    
    public void testCopyPasteTime() {
        addWaypointFile(DAY1, "dummy")
        addWaypointFile(DAY2, "dummy")
        Image imageWithDate = addImage(DAY1, IMAGE1)
        Image imageNoDate1 = addImage(null, NO_EXIF)
        Image imageNoDate2 = addImage(null, IMAGE2)
        imageNoDate1.modified = false
        imageNoDate2.modified = false
        
        assertEquals(3, tmpDir.getSize())
        assertEquals(null, tmpDir[0].date)
        assertEquals(2, tmpDir[0].images.size())
        assertSame(imageNoDate1, tmpDir[0].images[1])
        assertSame(imageNoDate2, tmpDir[0].images[0])
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
    
    public void testReadError() {
        reader = new ImageReader(new File(getClass().getResource(WAYPOINTS1).toURI()),
                DateTimeZone.UTC)
        assertFalse(logContains(WAYPOINTS1));
        Image image = reader.createImage()
        assertTrue(image.description.startsWith(Texts.ERROR_PREFIX))
        assertTrue(Arrays.equals(image.thumbnail, ImageReader.errorIcon))
        assertTrue(logContains(WAYPOINTS1));
    }
    
    public void testGeotag() {
        def dat = new LocalDate(2009, 1, 1)
        
        def day = new Day(date:dat)
        def wp12 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(12, 0)))
        def wp14 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(14, 0)))
        def wp16 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(16, 0)))
        def waypoints = new TreeSet([wp12, wp14, wp16])
        
        def im08 = new Image(time: dat.toDateTime(new LocalTime(8, 0)), day:day)
        def im11 = new Image(time: dat.toDateTime(new LocalTime(11, 0)), day:day)
        def im13l = new Image(time: dat.toDateTime(new LocalTime(12, 45)), day:day)
        def im15h = new Image(time: dat.toDateTime(new LocalTime(15, 10)), day:day)
        def im17 = new Image(time: dat.toDateTime(new LocalTime(17, 0)), day:day)
        def im12 = new Image(time: dat.toDateTime(new LocalTime(12, 0)), day:day)
        def im14 = new Image(time: dat.toDateTime(new LocalTime(14, 0)), day:day)
        def im16 = new Image(time: dat.toDateTime(new LocalTime(16, 0)), day:day)
        
        def images = [
            im08,
            im13l,
            im11,
            im14,
            im16,
            im12,
            im17,
            im15h
        ]
        
        images*.geotag(waypoints)
        
        assertSame(wp12, im12.waypoint)
        assertSame(wp14, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im08.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp12, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
        
        images.each{ it.waypoint = null }
        images*.geotag(Minutes.minutes(90), waypoints)
        
        assertSame(wp14, im12.waypoint)
        assertSame(wp16, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im08.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp14, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
    }
}


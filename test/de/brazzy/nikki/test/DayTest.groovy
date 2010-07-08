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

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.ImageSortField
import java.util.zip.ZipOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

/**
 * @author Michael Borgwardt
 */
class DayTest extends AbstractNikkiTest{
    
    public void testDayToString() {
        Day d = new Day(directory: tmpDir, date: DAY1)
        Image i1 = new Image(time: TIME1, fileName:"a")
        Image i2 = new Image(time: TIME1, fileName:"b")
        assertEquals(DATE1+" (0, 0)", d.toString())
        d.images.add(i1)
        assertEquals(DATE1+" (1, 0)", d.toString())
        d.images.add(i2)
        assertEquals(DATE1+" (2, 0)", d.toString())
        d.waypoints.add(new Waypoint(timestamp:new DateTime()))
        assertEquals(DATE1+" (2, 1)", d.toString())
        d.images.remove(i2)
        d.waypoints.remove(d.waypoints.first())
        assertEquals(DATE1+" (1, 0)", d.toString())
        
        d = new Day(directory: tmpDir)
        assertEquals(Texts.Main.UNKNOWN_DAY+" (0, 0)", d.toString())
        d.images.add(i1)
        d.waypoints.add(new Waypoint(timestamp:new DateTime()))
        assertEquals(Texts.Main.UNKNOWN_DAY+" (1, 1)", d.toString())
        d.images.remove(i1)
        d.waypoints.remove(d.waypoints.first())
        assertEquals(Texts.Main.UNKNOWN_DAY+" (0, 0)", d.toString())
    }
    
    public void testGeotag() {
        def dat = new LocalDate(2009, 1, 1)
        
        def day = new Day(date:dat)
        def wp12 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(12, 0)))
        def wp14 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(14, 0)))
        def wp16 = new Waypoint(timestamp: dat.toDateTime(new LocalTime(16, 0)))
        
        def im08 = new Image(time: dat.toDateTime(new LocalTime(8, 0)), day:day)
        def im11 = new Image(time: dat.toDateTime(new LocalTime(11, 0)), day:day)
        def im13l = new Image(time: dat.toDateTime(new LocalTime(12, 45)), day:day)
        def im15h = new Image(time: dat.toDateTime(new LocalTime(15, 10)), day:day)
        def im17 = new Image(time: dat.toDateTime(new LocalTime(17, 0)), day:day)
        def im12 = new Image(time: dat.toDateTime(new LocalTime(12, 0)), day:day)
        def im14 = new Image(time: dat.toDateTime(new LocalTime(14, 0)), day:day)
        def im16 = new Image(time: dat.toDateTime(new LocalTime(16, 0)), day:day)
        
        [im08, im13l, im11, im14, im16, im12, im17, im15h].each{ 
            day.images.add(it)
        } 
        day.waypoints.addAll([wp14, wp16, wp12])
        
        day.geotag()
        
        assertSame(wp12, im12.waypoint)
        assertSame(wp14, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im08.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp12, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
        
        day.images.each{ it.waypoint = null }
        day.geotag(Minutes.minutes(90))
        
        assertSame(wp14, im12.waypoint)
        assertSame(wp16, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im08.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp14, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
    }
    
    private Image createImage(Day day, String fileName, int hour){
        Image image = new Image(day: day, fileName: fileName, 
        time: (hour > 0 ? day.date.toDateTime(new LocalTime(hour, 0, 0), ZONE) : null))
        day.images.add(image)
        return image;
    }
    
    public void testImageSort() {
        Day d = new Day(directory: tmpDir, date: DAY1)
        Image image_c6 = createImage(d, "c", 6)
        assertEquals([image_c6], d.images.asList())
        Image image_a8 = createImage(d, "a", 8)
        assertEquals([image_c6, image_a8], d.images.asList())
        Image image_b7 = createImage(d, "b", 7)
        assertEquals([image_c6, image_b7, image_a8], d.images.asList())
        Image image_d9 = createImage(d, "d", 9)
        assertEquals([image_c6, image_b7, image_a8, image_d9], d.images.asList())
        
        d.setImageSortOrder(ImageSortField.FILENAME)        
        assertEquals([image_a8, image_b7, image_c6, image_d9], d.images.asList())
        d.images.remove(image_b7)
        assertEquals([image_a8, image_c6, image_d9], d.images.asList())
        
        d.setImageSortOrder(ImageSortField.TIME)        
        assertEquals([image_c6, image_a8, image_d9], d.images.asList())
        d.images.remove(image_d9)
        assertEquals([image_c6, image_a8], d.images.asList())
        
        d.setImageSortOrder(ImageSortField.FILENAME)        
        assertEquals([image_a8, image_c6], d.images.asList())
        d.images.add(image_b7)
        assertEquals([image_a8, image_b7, image_c6], d.images.asList())
        d.images.remove(image_a8)
        assertEquals([image_b7, image_c6], d.images.asList())
        d.images.remove(image_c6)
        assertEquals([image_b7], d.images.asList())
        d.images.remove(image_b7)
        assertEquals([], d.images.asList())
        
        d.setImageSortOrder(ImageSortField.TIME)        
        assertEquals([], d.images.asList())
    }
    
    public void testImageSortUnknown() {
        Day d = new Day(directory: tmpDir)
        Image image_c = createImage(d, "c", -1)
        assertEquals([image_c], d.images.asList())
        Image image_a = createImage(d, "a", -1)
        assertEquals([image_a, image_c], d.images.asList())
        Image image_b = createImage(d, "b", -1)
        assertEquals([image_a, image_b, image_c], d.images.asList())
        
        d.images.remove(image_b)
        assertEquals([image_a, image_c], d.images.asList())        
        d.images.remove(image_a)
        assertEquals([image_c], d.images.asList())
        d.images.remove(image_c)
        assertEquals([], d.images.asList())
    }
    
    public void testImageSortUnknownError() {
        try {
            Day d = new Day(directory: tmpDir)
            d.setImageSortOrder(ImageSortField.TIME)            
            fail("could set sort order to time on unknown day")
        } catch(IllegalArgumentException){
        } // expected
    }
    
    public void testEqualsHashCode() {
        def day1 = new Day(directory: tmpDir, date: DAY1)
        def day1a = new Day(directory: tmpDir, date: DAY1)
        def day2 = new Day(directory: tmpDir)
        def day2a = new Day(directory: tmpDir)
        def day3 = new Day(directory: tmpDir, date: DAY2)
        def day3a = new Day(directory: tmpDir, date: DAY2)
        
        checkEqualsHashCode([day1, day2, day3], [day1a, day2a, day3a])
    }
    
    public void testExport() {
        copyFile(IMAGE1)
        Image image1 = addImage(DAY1, IMAGE1)
        copyFile(IMAGE2)
        Image image2 = addImage(DAY1, IMAGE2)
        image2.description = "otherDescription"
        image2.title = "otherTitle"
        File file = new File(tmpDir.path, "export"+DATE1+".kmz")
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))
        Day day = tmpDir[0]
        day.export(out, null)
        ZipInputStream input = new ZipInputStream(new FileInputStream(file))
        ZipEntry entry;
        
        entry = input.getNextEntry()
        assertEquals("images/", entry.getName());
        assertEquals(0, entry.getSize());
        
        entry = input.getNextEntry()
        assertEquals(entry.getName(), "thumbs/");
        assertEquals(0, entry.getSize());
        
        entry = input.getNextEntry()
        assertEquals("images/"+IMAGE1, entry.getName());
        assertTrue(entry.getSize() > 0);
        
        entry = input.getNextEntry()
        assertEquals("thumbs/"+IMAGE1, entry.getName());
        assertTrue(entry.getSize() > 0);
        
        entry = input.getNextEntry()
        assertEquals("images/"+IMAGE2, entry.getName());
        assertTrue(entry.getSize() > 0);
        
        entry = input.getNextEntry()
        assertEquals("thumbs/"+IMAGE2, entry.getName());
        assertTrue(entry.getSize() > 0);
        
        entry = input.getNextEntry()
        assertEquals("diary.kml", entry.getName());
        
        String kml = IOUtils.toString(input, "UTF-8")
        assertTrue(kml.length() > 0)
        def finder = new XmlSlurper().parseText(kml)
        def placemarks = finder.Document.Placemark
        assertEquals(3, placemarks.size())
        def pm = placemarks[0]
        assertEquals("000 testTitle", pm.name.text())
        assertEquals("thumbs/"+IMAGE1, pm.Style.IconStyle.Icon.href.text())
        assertTrue(kml.contains("&lt;p&gt;testDescription&lt;/p&gt;"))
        
        pm = placemarks[1]
        assertEquals("001 otherTitle", pm.name.text())
        assertEquals("thumbs/"+IMAGE2, pm.Style.IconStyle.Icon.href.text())
        assertTrue(kml.contains("&lt;p&gt;otherDescription&lt;/p&gt;"))
        
        assertNull(input.getNextEntry())
        input.close()
    }
    
}


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

import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Image
import java.util.zip.ZipOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import org.apache.commons.io.IOUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.junit.Test;

/**
 * @author Michael Borgwardt
 */
class DayTest extends AbstractNikkiTest{

    public void testDayToString()
    {
        Day d = new Day(directory: tmpDir, date: DAY1)
        assertEquals(DATE1+" (0, 0)", d.toString())
        d.images.add(new Image())
        assertEquals(DATE1+" (1, 0)", d.toString())
        d.images.add(new Image())
        assertEquals(DATE1+" (2, 0)", d.toString())
        d.waypoints.add(new Waypoint())
        assertEquals(DATE1+" (2, 1)", d.toString())
        d.images.remove(1)
        d.waypoints.remove(0)
        assertEquals(DATE1+" (1, 0)", d.toString())
        
        d = new Day(directory: tmpDir)
        assertEquals("unknown (0, 0)", d.toString())
        d.images.add(new Image())
        d.waypoints.add(new Waypoint())
        assertEquals("unknown (1, 1)", d.toString())
        d.images.remove(0)
        d.waypoints.remove(0)
        assertEquals("unknown (0, 0)", d.toString())
    }

    public void testGeotag()
    {
        def dat = new LocalDate(2009, 1, 1)

        def day = new Day()
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

        day.images = [im08, im13l, im11, im14, im16, im12, im17, im15h]
        day.waypoints = [wp14, wp16, wp12]

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

    public void testExport()
    {
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
        assertEquals(2, placemarks.size())
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
    
    public void testEqualsHashCode()
    {
        def day1 = new Day(directory: tmpDir, date: DAY1)
        def day1a = new Day(directory: tmpDir, date: DAY1)
        def day2 = new Day(directory: tmpDir)
        def day2a = new Day(directory: tmpDir)
        def day3 = new Day(directory: tmpDir, date: DAY2)
        def day3a = new Day(directory: tmpDir, date: DAY2)

        checkEqualsHashCode([day1, day2, day3], [day1a, day2a, day3a])
    }
}


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

import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.ImageSortField
import de.brazzy.nikki.util.Texts;

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
        assertEquals([
            image_c6,
            image_b7,
            image_a8,
            image_d9
        ], d.images.asList())

        d.setImageSortOrder(ImageSortField.FILENAME)
        assertEquals([
            image_a8,
            image_b7,
            image_c6,
            image_d9
        ], d.images.asList())
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
        addWaypointFile(DAY1, "dummy")
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
        assertEquals("images/"+IMAGE1.toLowerCase(), entry.getName());
        assertTrue(entry.getSize() > 0);

        entry = input.getNextEntry()
        assertEquals("thumbs/"+IMAGE1.toLowerCase(), entry.getName());
        assertTrue(entry.getSize() > 0);

        entry = input.getNextEntry()
        assertEquals("images/"+IMAGE2.toLowerCase(), entry.getName());
        assertTrue(entry.getSize() > 0);

        entry = input.getNextEntry()
        assertEquals("thumbs/"+IMAGE2.toLowerCase(), entry.getName());
        assertTrue(entry.getSize() > 0);

        entry = input.getNextEntry()
        assertEquals("diary.kml", entry.getName());

        String kml = IOUtils.toString(input, "UTF-8")
        assertTrue(kml.length() > 0)
        def finder = new XmlSlurper().parseText(kml)
        def placemarks = finder.Document.Placemark
        assertEquals(4, placemarks.size())
        def pm = placemarks[0]
        assertEquals("000 testTitle", pm.name.text())
        assertEquals("thumbs/"+IMAGE1.toLowerCase(), pm.Style.IconStyle.Icon.href.text())
        assertTrue(kml.contains("&lt;p&gt;testDescription&lt;/p&gt;"))

        pm = placemarks[1]
        assertEquals("001 otherTitle", pm.name.text())
        assertEquals("thumbs/"+IMAGE2.toLowerCase(), pm.Style.IconStyle.Icon.href.text())
        assertTrue(kml.contains("&lt;p&gt;otherDescription&lt;/p&gt;"))

        assertNull(input.getNextEntry())
        input.close()
    }

    public void testPrepareWaypoints(){
        def day = new Day(directory: tmpDir, date: DAY1)
        def wp1 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.000000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.000000d),
                timestamp: TIME1.plusMinutes(1)
                )
        // far, soon
        def wp2 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.010000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.010000d),
                timestamp: TIME1.plusMinutes(2)
                )
        // near, soon
        def wp3 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.010001d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.010001d),
                timestamp: TIME1.plusMinutes(3)
                )
        // near, soon
        def wp4 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.010001d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.010002d),
                timestamp: TIME1.plusMinutes(4)
                )
        // near, soon
        def wp5 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.010002d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.010002d),
                timestamp: TIME1.plusMinutes(5)
                )
        // far, later
        def wp6 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.020000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.020000d),
                timestamp: TIME1.plusMinutes(10)
                )
        // near, soon
        def wp7 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.020001d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.020001d),
                timestamp: TIME1.plusMinutes(11)
                )
        // far, later
        def wp8 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.040000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.040000d),
                timestamp: TIME1.plusMinutes(21)
                )
        // far, later
        def wp9 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.050000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.050000d),
                timestamp: TIME1.plusMinutes(31)
                )
        // near, soon
        def wp10 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.050001d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.050001d),
                timestamp: TIME1.plusMinutes(32)
                )
        // near, soon
        def wp11 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.050002d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.050002d),
                timestamp: TIME1.plusMinutes(33)
                )
        // far, soon
        def wp12 = new Waypoint(
                latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 48.060000d),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 11.060000d),
                timestamp: TIME1.plusMinutes(34)
                )
        day.waypoints.addAll([
            wp1,
            wp2,
            wp3,
            wp4,
            wp5,
            wp6,
            wp7,
            wp8,
            wp9,
            wp10,
            wp11,
            wp12
        ])
        def compressedList = new ArrayList(day.prepareWaypoints());
        assertEquals([
            wp1,
            wp2,
            wp6,
            wp8,
            wp9,
            wp12
        ], compressedList)
        assertTrue(compressedList[0].startNewLine)
        assertFalse(compressedList[1].startNewLine)
        assertTrue(compressedList[2].startNewLine)
        assertTrue(compressedList[3].startNewLine)
        assertTrue(compressedList[4].startNewLine)
        assertFalse(compressedList[5].startNewLine)

        day = new Day(directory: tmpDir, date: DAY1)
        day.waypoints.addAll([wp1])
        compressedList = day.prepareWaypoints();
        assertEquals(new TreeSet([wp1]), compressedList)
        assertTrue(compressedList.first().startNewLine)

        day = new Day(directory: tmpDir, date: DAY1)
        assertEquals(new TreeSet(), day.prepareWaypoints())
    }
}


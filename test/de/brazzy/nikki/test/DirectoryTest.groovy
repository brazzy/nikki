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

import java.awt.Rectangle;

import groovy.mock.interceptor.MockFor;


import javax.swing.event.ListDataListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.util.TimezoneFinder;


/**
 * @author Michael Borgwardt
 */
public class DirectoryTest extends AbstractNikkiTest {
    
    public void testDirectoryToString() {
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
    
    public void testEqualsHashCode() {
        def dir1 = new Directory(path:new File("C:\\tmp"))
        def dir2 = tmpDir
        def dir1a = new Directory(path:new File("C:\\tmp"))
        def dir2a = new Directory(path:tmpDir.path)
        
        checkEqualsHashCode([dir1, dir2], [dir1a, dir2a])
    }
    
    
    public void testGetDay() {
        Day day1 = new Day(date:DAY1)
        Day day2 = new Day(date:DAY2)
        Day dayX = new Day()
        tmpDir.add(day1)
        tmpDir.add(day2)
        tmpDir.add(dayX)
        assertSame(day1, tmpDir.getDay(DAY1))
        assertSame(day2, tmpDir.getDay(DAY2))
        assertSame(dayX, tmpDir.getDay(null))
    }
    
    public void testAdd() {        
        def mock = new MockFor(ListDataListener)
        mock.demand.intervalAdded {
            assert it.index0 == 0 && it.index1==0
        }
        mock.demand.intervalAdded {
            assert it.index0 == 0 && it.index1==0
        }
        mock.demand.intervalAdded {
            assert it.index0 == 1 && it.index1==1
        }
        
        def mockListener = mock.proxyDelegateInstance()
        tmpDir.addListDataListener(mockListener)
        
        Day day1 = new Day(date:DAY1)
        Day day2 = new Day(date:DAY2)
        Day dayX = new Day()
        assert 0 == tmpDir.size
        
        tmpDir.add(day2)
        assert 1 == tmpDir.size
        assertFalse(tmpDir.contains(day1))
        assertTrue(tmpDir.contains(day2))
        assertFalse(tmpDir.contains(dayX))
        assertSame(day2, tmpDir[0])
        
        tmpDir.add(dayX)
        assert 2 == tmpDir.size
        assertFalse(tmpDir.contains(day1))
        assertTrue(tmpDir.contains(day2))
        assertTrue(tmpDir.contains(dayX))
        assertSame(day2, tmpDir[1])
        assertSame(dayX, tmpDir[0])
        
        tmpDir.add(day1)
        assert 3 == tmpDir.size
        assertTrue(tmpDir.contains(day1))
        assertTrue(tmpDir.contains(day2))
        assertTrue(tmpDir.contains(dayX))
        assertSame(day1, tmpDir[1])
        assertSame(day2, tmpDir[2])
        assertSame(dayX, tmpDir[0])
        
        try {
            tmpDir.add(null)
            fail("added null")
        }
        catch(IllegalArgumentException e) {
            assert e.message.contains("must not be null")
        }
        
        try {
            tmpDir.add(day1)
            fail("added already present day")
        }
        catch(IllegalArgumentException e) {
            assert e.message.contains("Already")
        }
        
        mock.verify(mockListener)
    }
    
    public void testRemove() {
        
        Day day1 = new Day(date:DAY1)
        Day day2 = new Day(date:DAY2)
        Day dayX = new Day()
        tmpDir.add(dayX)
        tmpDir.add(day1)
        tmpDir.add(day2)
        
        def mock = new MockFor(ListDataListener)
        mock.demand.intervalRemoved {
            assert it.index0 == 0 && it.index1==0
        }
        mock.demand.intervalRemoved {
            assert it.index0 == 1 && it.index1==1
        }
        def mockListener = mock.proxyDelegateInstance()
        tmpDir.addListDataListener(mockListener)
        
        tmpDir.remove(dayX)
        assert 2 == tmpDir.size
        assertSame(day2, tmpDir[1])
        assertSame(day1, tmpDir[0])
        assertTrue(tmpDir.contains(day1))
        assertTrue(tmpDir.contains(day2))
        assertFalse(tmpDir.contains(dayX))
        
        tmpDir.remove(day2)
        assert 1 == tmpDir.size
        assertSame(day1, tmpDir[0])
        assertTrue(tmpDir.contains(day1))
        assertFalse(tmpDir.contains(day2))
        assertFalse(tmpDir.contains(dayX))
        
        mock.verify(mockListener)
    }
    
    public void testIsModified() {
        copyFile(IMAGE1)
        Image image = addImage(DAY1, IMAGE1)
        image.modified = false
        Day day1 = tmpDir[0] 
        assertFalse(tmpDir.modified)
        image.description="changed"
        assertTrue(tmpDir.modified)
        tmpDir.save(null)
        assertFalse(tmpDir.modified)
    }
    
    def TIME_UTC_20H = new DateTime(2010,1,10,20,0,0,0,DateTimeZone.UTC)
    def WP_AUSTRALIA = new Waypoint(timestamp: TIME_UTC_20H.withZone(TZ_BRISBANE),
    latitude: new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: 10),
    longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 10))
    def WP_EUROPE = new Waypoint(timestamp: TIME_UTC_20H.plusMinutes(2).withZone(TZ_BERLIN),
    latitude: new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 10),
    longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: 10))
    
    public void testAddImageUntagged(){
        def imageAustralia = new Image(fileName:IMAGE1, time: TIME_UTC_20H)
        def imageEurope = new Image(fileName:IMAGE2, time:TIME_UTC_20H.plusMinutes(2))
        
        tmpDir.addWaypoint(WP_AUSTRALIA)
        tmpDir.addWaypoint(WP_EUROPE)
        
        assertEquals(new LocalDate(2010,1,10), tmpDir.addImage(imageEurope).date)
        assertEquals(new LocalDate(2010,1,11), tmpDir.addImage(imageAustralia).date)
    }
    
    public void testAddImageDifferentDay(){
        def imageAustralia = new Image(fileName:IMAGE1, time: TIME_UTC_20H.minusDays(3))
        def imageEurope = new Image(fileName:IMAGE2, time:TIME_UTC_20H.plusMinutes(2).plusDays(1))
        
        tmpDir.addWaypoint(WP_AUSTRALIA)
        tmpDir.addWaypoint(WP_EUROPE)
        
        assertEquals(new LocalDate(2010,1,11), tmpDir.addImage(imageEurope).date)
        assertEquals(new LocalDate(2010,1,8), tmpDir.addImage(imageAustralia).date)
    }
    
    public void testAddImageTagged(){
        def imageAustralia = new Image(fileName:IMAGE1, time: TIME_UTC_20H)
        imageAustralia.waypoint = WP_AUSTRALIA
        
        tmpDir.addWaypoint(WP_EUROPE)
        
        tmpDir.addImage(imageAustralia)
        assertSame(WP_AUSTRALIA, imageAustralia.waypoint)
        assertEquals(new LocalDate(2010,1,11), imageAustralia.day.date)
    }
    
    public void testGeotag(){
        def imageAustralia = new Image(fileName:IMAGE1, time: TIME_UTC_20H)
        
        tmpDir.addWaypoint(WP_EUROPE)
        tmpDir.geotag()
        assertEquals(new LocalDate(2010,1,10), tmpDir.addImage(imageAustralia).date)
        assertSame(WP_EUROPE, imageAustralia.waypoint)
        
        tmpDir.addWaypoint(WP_AUSTRALIA)
        tmpDir.geotag()
        assertSame(WP_AUSTRALIA, imageAustralia.waypoint)
        assertEquals(new LocalDate(2010,1,11), imageAustralia.day.date)
    }
}


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

import groovy.mock.interceptor.MockFor;


import javax.swing.event.ListDataListener;

import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.WaypointFile


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
        mock.demand.intervalAdded { assert it.index0 == 0 && it.index1==0 }
        mock.demand.intervalAdded { assert it.index0 == 0 && it.index1==0 }
        mock.demand.intervalAdded { assert it.index0 == 1 && it.index1==1 }
        
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
        mock.demand.intervalRemoved { assert it.index0 == 0 && it.index1==0 }
        mock.demand.intervalRemoved { assert it.index0 == 1 && it.index1==1 }
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
}


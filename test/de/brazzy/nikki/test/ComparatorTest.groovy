package de.brazzy.nikki.test;

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

import static org.junit.Assert.*;
import org.joda.time.DateTime;
import org.junit.Test;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.PropertyComparator;

/**
 * @author Michael Borgwardt
 *
 */
class ComparatorTest {
    @Test
    public void propertyComparator() {
        Image i1 = new Image(fileName: "a", time: new DateTime(2000,01,02,0,0,0,0));
        Image i2 = new Image(fileName: "b", time: new DateTime(2000,01,01,0,0,0,0));
        PropertyComparator<Image> nameComp = new PropertyComparator(propertyName: "fileName")
        PropertyComparator<Image> timeComp = new PropertyComparator(propertyName: "time")
        
        assertEquals(nameComp.compare(i1, i1), 0)
        assertEquals(nameComp.compare(i2, i2), 0)
        assertTrue(nameComp.compare(i1, i2) < 0)
        assertTrue(nameComp.compare(i2, i1) > 0)
        
        assertEquals(timeComp.compare(i1, i1), 0)
        assertEquals(timeComp.compare(i2, i2), 0)
        assertTrue(timeComp.compare(i1, i2) > 0)
        assertTrue(timeComp.compare(i2, i1) < 0) 
    }
    
    @Test
    public void propertyComparatorSecondary() {
        Image i1 = new Image(fileName: "a", time: new DateTime(2000,01,02,0,0,0,0));
        Image i2 = new Image(fileName: "a", time: new DateTime(2000,01,01,0,0,0,0));
        PropertyComparator<Image> comp = new PropertyComparator(propertyName: "fileName", secondary: "time")
        
        assertEquals(comp.compare(i1, i1), 0)
        assertEquals(comp.compare(i2, i2), 0)
        assertTrue(comp.compare(i1, i2) > 0)
        assertTrue(comp.compare(i2, i1) < 0)
    }
}

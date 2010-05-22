package de.brazzy.nikki.test;

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

import static org.junit.Assert.*;
import org.joda.time.DateTime;
import org.junit.Test;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.PropertyComparator;

/**
 * @author Michael Borgwardt
 *
 */
class ComparatorTest
{
    @Test
    public void propertyComparator()
    {
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
    public void propertyComparatorSecondary()
    {
        Image i1 = new Image(fileName: "a", time: new DateTime(2000,01,02,0,0,0,0));
        Image i2 = new Image(fileName: "a", time: new DateTime(2000,01,01,0,0,0,0));
        PropertyComparator<Image> comp = new PropertyComparator(propertyName: "fileName", secondary: "time")

        assertEquals(comp.compare(i1, i1), 0)
        assertEquals(comp.compare(i2, i2), 0)
        assertTrue(comp.compare(i1, i2) > 0)
        assertTrue(comp.compare(i2, i1) < 0)
    }
}

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

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.ListDataModel;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.util.PropertyComparator;
import de.brazzy.nikki.util.log_parser.ParserException;


/**
 * Close gaps in test coverage
 * 
 * @author Michael Borgwardt
 */
class CoverageTest
{
    @Test
    public void texts(){
        def dummy 
        dummy = new Texts()
        dummy = new Texts.Dialogs() 
        dummy = new Texts.Dialogs.About() 
        dummy = new Texts.Dialogs.GeotagOptions() 
        dummy = new Texts.Dialogs.ScanOptions() 
        dummy = new Texts.Image()
        dummy = new Texts.Main()
    }
    
    @Test
    public void parserException(){
        def dummy 
        dummy = new ParserException(new Exception())
        dummy = new ParserException("test")
    }    
    
    @Test
    public void propertyComparator(){
        def dummy1a = new PropertyComparator(propertyName: "a")
        def dummy1b = new PropertyComparator(propertyName: "a")
        def dummy1c = new PropertyComparator(propertyName: "b")
        def dummy2a = new PropertyComparator(propertyName: "a", secondary: "b")
        def dummy2b = new PropertyComparator(propertyName: "a", secondary: "b")
        def dummy2c = new PropertyComparator(propertyName: "a", secondary: "c")
        assert dummy1a == dummy1b
        assert dummy1a != dummy1c
        assert dummy1a != dummy2a
        assert dummy2a == dummy2b
        assert dummy2a != dummy2c
        
        def set = new HashSet();
        set.add(dummy1a)
        assertEquals(1, set.size())
        set.add(dummy1b)
        assertEquals(1, set.size())
        set.add(dummy1c)
        assertEquals(2, set.size())
        
        set.add(dummy2a)
        assertEquals(3, set.size())
        set.add(dummy2b)
        assertEquals(3, set.size())
        set.add(dummy2c)
        assertEquals(4, set.size())
    }    
    
    @Test
    public void toStringMethods(){
        def dummy = new Waypoint(timestamp: new DateTime())
        dummy.toString()
        dummy = new Image(fileName: "test")
        dummy.toString()
    }    
    
    @Test
    public void listDataModel(){
        def dummy = new ListDataModel<String>()
        dummy.add("a")
        dummy.add("b")
        def itr = dummy.iterator()
        assertEquals("a", itr.next())
        assertEquals("b", itr.next())
        assertFalse(itr.hasNext())
    }    
}

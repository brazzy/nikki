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


import com.infomatiq.jsi.Rectangle

import de.brazzy.nikki.util.TimezoneFinder
import groovy.util.GroovyTestCase

/**
 * @author Michael Borgwardt
 */
class TimezoneFinderTest extends GroovyTestCase
{
    public void testEmpty()
    {
        def obj = new TimezoneFinder()
        assertNull(obj.find(0.0f, 0.0f))
    }
    
    public void testParse()
    {
        def baos = new ByteArrayOutputStream()
        def oos = new ObjectOutputStream(baos)
        oos.writeUTF("Europe/Berlin")
        oos.writeUTF("Australia/Brisbane")
        oos.writeUTF("")
        oos.writeFloat(-10.0f)
        oos.writeFloat(22.0f)
        oos.writeShort(1)
        oos.writeFloat(1.0f)
        oos.writeFloat(2.0f)
        oos.writeShort(0)
        oos.writeFloat(Float.NaN)
        oos.close()
        
        def obj = new TimezoneFinder(new ByteArrayInputStream(baos.toByteArray()))
        assertEquals(obj.zones, 
                     [ AbstractNikkiTest.TZ_BERLIN, 
                       AbstractNikkiTest.TZ_BRISBANE])
        assertEquals(2, obj.tree.size())
        assertEquals(new Rectangle(-10.0f, 22.0f, 1.0f, 2.0f), obj.tree.bounds)
    }
    
    public void testFind()
    {
        def obj = new TimezoneFinder()
        obj.zones = [AbstractNikkiTest.TZ_BERLIN, 
                     AbstractNikkiTest.TZ_BRISBANE]
        obj.tree.add(new Rectangle(-10.0f, 22.0f, -10.00001f, 22.0001f), 0)
        obj.tree.add(new Rectangle(100.0f, -2.0f, 100.00001f, -2.0001f), 1)

        assertEquals(obj.find(-20.0f, 10.0f), AbstractNikkiTest.TZ_BERLIN)
        assertEquals(obj.find(10.0f, 10.0f), AbstractNikkiTest.TZ_BERLIN)
        assertEquals(obj.find(70.0f, 0.0f), AbstractNikkiTest.TZ_BRISBANE)
        assertEquals(obj.find(1000.0f, 22.0f), AbstractNikkiTest.TZ_BRISBANE)
        
    }    
}

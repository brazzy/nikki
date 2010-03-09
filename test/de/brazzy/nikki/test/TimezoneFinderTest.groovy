package de.brazzy.nikki.test;

import org.joda.time.DateTimeZone;

import com.infomatiq.jsi.Rectangle;

import de.brazzy.nikki.util.TimezoneFinder;
import groovy.util.GroovyTestCase;

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
                     [DateTimeZone.forID("Europe/Berlin"), 
                      DateTimeZone.forID("Australia/Brisbane")])
        assertEquals(2, obj.tree.size())
        assertEquals(new Rectangle(-10.0f, 22.0f, 1.0f, 2.0f), obj.tree.bounds)
    }
    
    public void testFind()
    {
        def obj = new TimezoneFinder()
        obj.zones = [DateTimeZone.forID("Europe/Berlin"), 
                     DateTimeZone.forID("Australia/Brisbane")]
        obj.tree.add(new Rectangle(-10.0f, 22.0f, -10.00001f, 22.0001f), 0)
        obj.tree.add(new Rectangle(100.0f, -2.0f, 100.00001f, -2.0001f), 1)

        assertEquals(obj.find(-20.0f, 10.0f), DateTimeZone.forID("Europe/Berlin"))
        assertEquals(obj.find(10.0f, 10.0f), DateTimeZone.forID("Europe/Berlin"))
        assertEquals(obj.find(70.0f, 0.0f), DateTimeZone.forID("Australia/Brisbane"))
        assertEquals(obj.find(1000.0f, 22.0f), DateTimeZone.forID("Australia/Brisbane"))
        
    }
}

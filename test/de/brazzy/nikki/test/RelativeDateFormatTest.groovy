package de.brazzy.nikki.test

import de.brazzy.nikki.util.RelativeDateFormat
import java.util.TimeZone
import java.text.DateFormat
import java.text.SimpleDateFormat
import groovy.util.GroovyTestCase
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.util.ImageReader

public class RelativeDateFormatTest extends GroovyTestCase{
    DateFormat formatGMT
    DateFormat format12
    RelativeDateFormat holiday
    Date normal
    Date early
    Date noon
    Date late

    public void setUp()
    {
        formatGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format12 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatGMT.timeZone = TimeZone.getTimeZone(TimeZone.GMT_ID)
        format12.timeZone = TimeZone.getTimeZone("Etc/GMT+12")
        holiday = new RelativeDateFormat(format12.timeZone)
        
        normal = format12.parse("2009-10-01 00:00:00")
        early = format12.parse("2009-10-01 00:00:01")
        noon = format12.parse("2009-10-01 12:00:01")
        late = format12.parse("2009-10-01 23:59:59")
    }
    
    public void testTimeZone()
    {
        assertFalse(normal.equals(formatGMT.parse("2009-10-01 00:00:00")))
    }
        
    public void testStripTime()
    {
        assertEquals(normal, holiday.stripTime(normal))
        assertEquals(normal, holiday.stripTime(early))
        assertEquals(normal, holiday.stripTime(noon))
        assertEquals(normal, holiday.stripTime(late))
    }
    
    public void testSameDay()
    {
        assertTrue(holiday.sameDay(normal, normal))
        assertTrue(holiday.sameDay(early, early))
        assertTrue(holiday.sameDay(noon, noon))
        assertTrue(holiday.sameDay(late, late))
        
        assertTrue(holiday.sameDay(normal, early))
        assertTrue(holiday.sameDay(normal, noon))
        assertTrue(holiday.sameDay(normal, late))

        assertTrue(holiday.sameDay(late, early))
        assertTrue(holiday.sameDay(noon, early))
        
        assertTrue(holiday.sameDay(noon, late))
        
        assertFalse(holiday.sameDay(normal, format12.parse("2009-10-02 00:00:00")))
        assertFalse(holiday.sameDay(normal, format12.parse("2009-09-30 23:59:59")))
    }
    
    public void testFormat()
    {
        assertEquals("2009-10-01", holiday.format(normal))
        assertEquals("2009-10-01", holiday.format(early))
        assertEquals("2009-10-01", holiday.format(late))
        assertEquals("2009-10-01", holiday.format(noon))
    }
    
    public void testDayToString()
    {
        def dir = new Directory(zone: TimeZone.getTimeZone("Etc/GMT+11"))
        def day = new Day(date: normal, directory: dir)
        assertEquals("2009-10-01 (0, 0)", day.toString())
    }
}

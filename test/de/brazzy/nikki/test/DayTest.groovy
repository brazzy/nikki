package de.brazzy.nikki.test

import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.Day
import java.text.SimpleDateFormat
import de.brazzy.nikki.model.Image

/**
 * TODO:
 * - Geotagging (mit Offset)
 * - Export
 * - toString
 * @author Brazil
 */
class DayTest extends GroovyTestCase{


    public void testGeotag()
    {
        def fmt = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        def dat = "GMT 2009-01-01 12:"

        def day = new Day()
        def wp12 = new Waypoint(timestamp: fmt.parse(dat+"12:00"))
        def wp14 = new Waypoint(timestamp: fmt.parse(dat+"14:00"))
        def wp16 = new Waypoint(timestamp: fmt.parse(dat+"16:00"))

        def im11 = new Image(time: fmt.parse(dat+"11:00"))
        def im13l = new Image(time: fmt.parse(dat+"12:45"))
        def im15h = new Image(time: fmt.parse(dat+"15:10"))
        def im17 = new Image(time: fmt.parse(dat+"17:00"))
        def im12 = new Image(time: fmt.parse(dat+"12:00"))
        def im14 = new Image(time: fmt.parse(dat+"14:00"))
        def im16 = new Image(time: fmt.parse(dat+"16:00"))

        day.images = [im13l, im11, im14, im16, im12, im17, im15h]
        day.waypoints = [wp14, wp16, wp12]

        day.geotag()

        assertSame(wp12, im12.waypoint)
        assertSame(wp14, im14.waypoint)
        assertSame(wp16, im16.waypoint)
        assertSame(wp12, im11.waypoint)
        assertSame(wp12, im13l.waypoint)
        assertSame(wp16, im15h.waypoint)
        assertSame(wp16, im17.waypoint)
    }

}


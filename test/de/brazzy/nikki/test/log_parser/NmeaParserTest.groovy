package de.brazzy.nikki.test.log_parser;

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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.util.log_parser.NmeaParser;

import static org.junit.Assert.*;

/**
 * @author Michael Borgwardt
 *
 */
class NmeaParserTest extends AbstractParserTest {
    static final String ONE_WP = 
    '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D'
    static final String TWO_WPS = ONE_WP+ '\n$stuff\n'+
    '$GPRMC,071240.000,A,4910.0900,S,01234.9470,W,000.00,0.0,270709,,,E*5D'
    
    public NmeaParserTest() {
        super(new NmeaParser())
        unparseable = '$GPRMC,071232.000,x,x,x'.getBytes()
        empty = 'AB\n$CD'.getBytes()
        oneWaypoint = ONE_WP.getBytes()
        matchFilenames = ["a.nmea", "file.NMEA", "test.nme", "dings.NME"]
    }
    
    
    @Test
    public void parseCoord() {
        def coord = NmeaParser.parseCoordinate("4810.0900","S")
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.SOUTH)
        assertTrue(coord.value.toString(), -49 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -48)
        
        coord = NmeaParser.parseCoordinate("01134.9470","W")
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.WEST)
        assertTrue(coord.value.toString(), -12 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -11)
    }
    
    @Test
    void parse() {
        def iterator = parser.parse(new ByteArrayInputStream(TWO_WPS.getBytes()))
        assertTrue(iterator.hasNext())
        Waypoint wp = iterator.next();
        
        def dt = new DateTime(2009, 7, 27, 7, 12, 32, 0, DateTimeZone.UTC)
        assertEquals(dt, wp.timestamp)
        
        def coord = wp.latitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.NORTH)
        assertTrue(coord.value.toString(), 48 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 49)
        
        coord = wp.longitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.EAST)
        assertTrue(coord.value.toString(), 11 < coord.value )
        assertTrue(coord.value.toString(), coord.value < 12)
        
        assertTrue(iterator.hasNext())
        wp = iterator.next();
        
        dt = new DateTime(2009, 7, 27, 7, 12, 40, 0, DateTimeZone.UTC)
        assertEquals(dt, wp.timestamp)
        
        coord = wp.latitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.SOUTH)
        assertTrue(coord.value.toString(), -50 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -49)
        
        coord = wp.longitude
        assertTrue(coord.direction.toString(), coord.direction == Cardinal.WEST)
        assertTrue(coord.value.toString(), -13 < coord.value )
        assertTrue(coord.value.toString(), coord.value < -12)
        
        assertFalse(iterator.hasNext())
    }
}

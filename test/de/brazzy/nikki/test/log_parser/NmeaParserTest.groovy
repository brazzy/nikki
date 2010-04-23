package de.brazzy.nikki.test.log_parser;

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
class NmeaParserTest extends AbstractParserTest
{
    static final String ONE_WP = 
                        '$GPRMC,071232.000,A,4810.0900,N,01134.9470,E,000.00,0.0,270709,,,E*5D'
    static final String TWO_WPS = ONE_WP+ '\n$stuff\n'+
                        '$GPRMC,071240.000,A,4910.0900,S,01234.9470,W,000.00,0.0,270709,,,E*5D'
        
    public NmeaParserTest()
    {
        super(new NmeaParser())
        unparseable = '$GPRMC,071232.000,x,x,x'.getBytes()
        empty = 'AB\n$CD'.getBytes()
        oneWaypoint = ONE_WP.getBytes()
        matchFilenames = ["a.nmea", "file.NMEA", "test.nme", "dings.NME"]
    }

    @Test
    void testParse()
    {
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

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

import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal


/**
 * @author Michael Borgwardt
 */
public class GeoCoordinateTest extends GroovyTestCase{
    
    public void testCoordValue() {
        GeoCoordinate a;
        a = new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: 1.5d)
        assertEquals(-1.5d, a.value)
        a = new GeoCoordinate(direction: Cardinal.NORTH, magnitude: 1.0d)
        assertEquals(1.0d, a.value)
        a = new GeoCoordinate(direction: Cardinal.EAST, magnitude: 2.0d)
        assertEquals(2.0d, a.value)
        a = new GeoCoordinate(direction: Cardinal.WEST, magnitude: 3.0d)
        assertEquals(-3.0d, a.value)
    }
}


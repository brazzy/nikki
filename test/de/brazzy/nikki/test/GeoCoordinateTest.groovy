package de.brazzy.nikki.test
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

import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal


/**
 * @author Michael Borgwardt
 */
public class GeoCoordinateTest extends GroovyTestCase{

    public void testCoordValue()
    {
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


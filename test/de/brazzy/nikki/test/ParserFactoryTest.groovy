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

import java.io.File;
import org.junit.Test;

import de.brazzy.nikki.util.ParserFactory;
import slash.navigation.nmea.NmeaFormat;

/**
 * @author Michael Borgwardt
 *
 */
class ParserFactoryTest extends AbstractNikkiTest {
    
    ParserFactory factory = new ParserFactory()
    
    void testNmea() {
        copyFile(WAYPOINTS1)
        def f = new File(tmpDir.path, WAYPOINTS1)
        def parser = factory.findParser(f)
        assertNotNull(parser)
        assertTrue(parser instanceof NmeaFormat)
    }    
    
    //    void testNoGps() {
    //        copyFile(IMAGE1)
    //        def parser = factory.findParser(new File(tmpDir.path, IMAGE1))
    //        assertNull(parser)
    //    }        
    
}


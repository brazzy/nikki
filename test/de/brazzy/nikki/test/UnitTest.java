package de.brazzy.nikki.test;
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

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import de.brazzy.nikki.test.log_parser.NmeaParserTest;
import de.brazzy.nikki.test.log_parser.ParserFactoryTest;

/**
 * @author Michael Borgwardt
 */
public class UnitTest extends TestSuite{
    public static TestSuite suite()
    {
        TestSuite s=new UnitTest();

        s.addTest(new JUnit4TestAdapter(ComparatorTest.class));
        s.addTest(new JUnit4TestAdapter(ParserFactoryTest.class));
        s.addTest(new JUnit4TestAdapter(NmeaParserTest.class));
        s.addTest(new JUnit4TestAdapter(ExtensionFilterTest.class));
        s.addTest(new JUnit4TestAdapter(CoverageTest.class));
        s.addTestSuite(ImageTest.class);
        s.addTestSuite(GeoCoordinateTest.class);
        s.addTestSuite(DirectoryTest.class);
        s.addTestSuite(DayTest.class);
        s.addTestSuite(TimezoneFinderTest.class);
        return s;
    }
}

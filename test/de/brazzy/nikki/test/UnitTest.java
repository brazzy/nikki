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

package de.brazzy.nikki.test;

import junit.framework.TestSuite;

/**
 *
 * @author Brazil
 */
public class UnitTest extends TestSuite{
    public static TestSuite suite()
    {
        TestSuite s=new UnitTest();
       
        s.addTestSuite(ImageTest.class);
        s.addTestSuite(WaypointTest.class);
        s.addTestSuite(DirectoryTest.class);
        s.addTestSuite(DayTest.class);
        s.addTestSuite(TimezoneFinderTest.class);
        
        return s;
    }
}

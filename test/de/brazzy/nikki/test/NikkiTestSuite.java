package de.brazzy.nikki.test;

import junit.framework.TestSuite;

public class NikkiTestSuite extends TestSuite{

    public static TestSuite suite()
    {
        TestSuite s=new NikkiTestSuite();
        s.addTestSuite(RelativeDateFormatTest.class);
        s.addTestSuite(WaypointTest.class);
        s.addTestSuite(NikkiModelTest.class);
        return s;
    }
}

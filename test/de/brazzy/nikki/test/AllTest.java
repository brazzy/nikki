package de.brazzy.nikki.test;

import junit.framework.TestSuite;

public class AllTest extends TestSuite{

    public static TestSuite suite()
    {
        TestSuite s=new AllTest();
        s.addTestSuite(RelativeDateFormatTest.class);
        s.addTestSuite(WaypointTest.class);
        s.addTestSuite(NikkiModelTest.class);
        return s;
    }
}

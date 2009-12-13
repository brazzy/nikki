package de.brazzy.nikki.test;

import junit.framework.TestSuite;

public class AllTest extends TestSuite{

    public static TestSuite suite()
    {
        TestSuite s=new AllTest();
        s.addTestSuite(RelativeDateFormatTest.class);
        s.addTestSuite(WaypointTest.class);
        s.addTestSuite(GuiTest.class);
        s.addTestSuite(DirectoryTest.class);
        s.addTestSuite(DayTest.class);
        s.addTestSuite(ImageTest.class);
        return s;
    }
}

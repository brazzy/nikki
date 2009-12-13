package de.brazzy.nikki.test;

import junit.framework.TestSuite;

public class IntegrationTest extends TestSuite{

    public static TestSuite suite()
    {
        TestSuite s=new IntegrationTest();
        s.addTestSuite(GuiTest.class);
        return s;
    }
}

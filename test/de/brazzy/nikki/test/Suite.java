package de.brazzy.nikki.test;

import junit.framework.TestSuite;

public class Suite extends TestSuite
{
    public static TestSuite suite()
    {
        TestSuite s=new UnitTest();
       
        s.addTest(IntegrationTest.suite());
        s.addTest(UnitTest.suite());
        return s;
    }
}

package de.brazzy.nikki.test;

import junit.framework.TestSuite;

/**
 * TODO:
 * - Einlesen / Anfertigen von Thumbnails (mit/ohne Rotation)
 * - Einlesen der Zeit und vorhandener Geodaten
 * - Einlesen von WaypointFiles
 * - Daten vollständig nach Rescan (incl. Zeitzone)
 * - Geotagging (mit/ohne Offset)
 * - Export
 */
public class AllTest extends TestSuite{

    public static TestSuite suite()
    {
        TestSuite s=new AllTest();
        s.addTestSuite(RelativeDateFormatTest.class);
        s.addTestSuite(WaypointTest.class);
        s.addTestSuite(NikkiModelTest.class);
        s.addTestSuite(DirectoryTest.class);
        return s;
    }
}

package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.text.SimpleDateFormat

/**
 * @author Brazil
 */
class GuiTest extends AbstractNikkiTest {

    TestDialogs dialogs
    NikkiModel model
    NikkiFrame view

    public void setUp()
    {
        super.setUp()
        dialogs = new TestDialogs()
        def nikki = new Nikki()
        nikki.build(false, dialogs)
        model = nikki.model
        view = nikki.view
    }

    public void testAdd()
    {
        assertEquals(0, model.size())
        dialogs.add(new File("C:\\tmp"))
        view.addButton.actionListeners[0].actionPerformed()
        assertEquals(1, model.size())
        assertEquals("tmp (0, 0)", model[0].toString())
    }

    public void testRemove()
    {
        model.add(new Directory(path: new File("C:\\tmp1")))
        model.add(new Directory(path: new File("C:\\tmp2")))
        assertEquals(2, model.size())
        view.dirList.selectedIndex = 0
        view.deleteButton.actionListeners[0].actionPerformed()
        assertEquals(1, model.size())
        assertEquals("tmp2 (0, 0)", model[0].toString())
    }

    public void testScanSaveRescan()
    {
        model.add(tmpDir)
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        view.dirList.selectedIndex = 0
        copyFile(IMAGE1)
        copyFile("20091111.nmea")
        dialogs.add(ZONE)

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(tmpDir.path.name + " (1, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())

        assertEquals(2, tmpDir.path.list().length)
        view.saveButton.actionListeners[0].actionPerformed()
        Thread.sleep(500);
        assertEquals(3, tmpDir.path.list().length)

        model.remove(tmpDir)
        tmpDir = new Directory(path: tmpDir.path);
        assertEquals(TimeZone.getDefault(), tmpDir.zone)
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        copyFile(IMAGE2)
        copyFile("20091112.nmea")

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpDir.size())
        assertEquals(ZONE, tmpDir.zone)
        assertEquals(tmpDir.path.name + " (2, 2)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
    }

    public void testGeotagExport()
    {
        def fmt = new SimpleDateFormat("Z yyyy-MM-dd HH:mm:ss");
        copyFile(IMAGE1)
        copyFile("20091111.nmea")
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        dialogs.add(ZONE)
        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertTrue(dialogs.isQueueEmpty())
        assertNull(model[0].images[IMAGE1].waypoint)

        view.dayList.selectedIndex = 0
        dialogs.add(Integer.valueOf(-15 * 60 * 60))
        view.tagButton.actionListeners[0].actionPerformed()
        Thread.sleep(100);
        assertTrue(dialogs.isQueueEmpty())
        def wp = model[0].images[IMAGE1].waypoint
        assertNotNull(wp)
        assertEquals(fmt.parse("GMT 2009-11-11 05:09:04"), wp.timestamp)

        dialogs.add(new File(tmpDir.path, "export.kmz"))
        assertEquals(2, tmpDir.path.list().length)
        view.exportButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(3, tmpDir.path.list().length)
    }

    public void testOffsetFinder()
    {
        // TODO
    }

    public void testScanOptions()
    {
        String[] zones = TimeZone.getAvailableIDs();
        Arrays.sort(zones);

        ScanOptions op = new ScanOptions(TimeZone.getTimeZone("GMT"))
        assertEquals("GMT", op.getTimezone().getID())
        op.combobox.selectedIndex = 1;
        assertEquals(zones[1], op.getTimezone().getID())
    }

    public void testGeotagOptions()
    {
        GeotagOptions op = new GeotagOptions()
        assertEquals(0, op.getOffset())
        op.spinner.value = op.spinner.nextValue;
        assertEquals(10, op.getOffset())
        op.spinner.value = -100;
        assertEquals(-100, op.getOffset())
    }

}


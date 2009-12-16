package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.view.NikkiFrame
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
        // TODO: waypointFiles
        model.add(tmpDir)
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        view.dirList.selectedIndex = 0
        copyFile(IMAGE1)
        dialogs.add(ZONE)

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(tmpDir.path.name + " (1, 0)", model[0].toString())
        assertEquals(DATE1+" (1, 0)", tmpDir[0].toString())

        assertEquals(1, tmpDir.path.list().length)
        view.saveButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpDir.path.list().length)

        model.remove(tmpDir)
        tmpDir = new Directory(path: tmpDir.path);
        assertEquals(TimeZone.getDefault(), tmpDir.zone)
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        copyFile(IMAGE2)

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpDir.size())
        assertEquals(ZONE, tmpDir.zone)
        assertEquals(tmpDir.path.name + " (2, 0)", model[0].toString())
        assertEquals(DATE1+" (1, 0)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 0)", tmpDir[1].toString())
    }

    public void testExport()
    {
        // TODO
    }

    public void testGeotag()
    {
        // TODO
    }
}


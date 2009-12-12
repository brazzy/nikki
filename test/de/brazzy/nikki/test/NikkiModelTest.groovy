package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.view.NikkiFrame
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Brazil
 */
class NikkiModelTest extends GroovyTestCase {

    TestDialogs dialogs
    NikkiModel model
    NikkiFrame view

    public void setUp()
    {
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

    public void testScan()
    {
        File tmpdir = File.createTempFile("nikkitest",null)
        tmpdir.delete();
        tmpdir.mkdir();
        tmpdir.deleteOnExit()
        Directory dir = new Directory(path: tmpdir);
        model.add(dir)
        assertEquals(tmpdir.name + " (0, 0)", model[0].toString())
        view.dirList.selectedIndex = 0
        IOUtils.copy(NikkiModelTest.class.getResourceAsStream("IMG2009-11-11.JPG"),
            new FileOutputStream(new File(tmpdir, "IMG2009-11-11.JPG")));
        dialogs.add(TimeZone.getTimeZone("GMT+10"))

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(1, dir.size())
        assertEquals(tmpdir.name + " (1, 0)", model[0].toString())
        assertEquals("2009-11-11 (1, 0)", dir[0].toString())

        assertEquals(1, tmpdir.list().length)
        view.saveButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpdir.list().length)

        model.remove(dir)
        dir = new Directory(path: tmpdir);
        model.add(dir)
        view.dirList.selectedIndex = 0
        
        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(1, dir.size())
        assertEquals(tmpdir.name + " (1, 0)", model[0].toString())
        assertEquals("2009-11-11 (1, 0)", dir[0].toString())
    }
}


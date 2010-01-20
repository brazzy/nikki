package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.text.SimpleDateFormat
import java.text.DateFormat

/**
 * @author Brazil
 */
class GuiTest extends AbstractNikkiTest {

    TestDialogs dialogs
    NikkiModel model
    NikkiFrame view
    Nikki nikki

    public void setUp()
    {
        super.setUp()
        dialogs = new TestDialogs()
        nikki = new Nikki()
        nikki.build(GuiTest.class, dialogs)
        model = nikki.model
        view = nikki.view
    }

    public void testAdd()
    {
        assertEquals(0, model.size())
        dialogs.add(null)
        view.addButton.actionListeners[0].actionPerformed()
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

    public void testButtons()
    {
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)

        model.add(tmpDir)
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)

        view.dirList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)

        tmpDir.images.put(IMAGE1, constructImage(DAY1, IMAGE1))
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)

        view.dayList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertTrue(view.tagButton.enabled)
        assertTrue(view.exportButton.enabled)

        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertTrue(editor.geoLink.enabled)

        view.dayList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)

        view.dirList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
    }

    public void testScanSaveRescan()
    {
        model.add(tmpDir)
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        view.dirList.selectedIndex = 0
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)

        dialogs.add(null)
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(0, tmpDir.size())

        dialogs.add(ZONE)
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(tmpDir.path.name + " (1, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())

        assertEquals(2, tmpDir.path.list().length)
        view.saveButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertEquals(3, tmpDir.path.list().length)

        model.remove(tmpDir)
        tmpDir = new Directory(path: tmpDir.path);
        assertEquals(TimeZone.getDefault(), tmpDir.zone)
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        copyFile(IMAGE2)
        copyFile(WAYPOINTS2)

        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertEquals(2, tmpDir.size())
        assertEquals(ZONE, tmpDir.zone)
        assertEquals(tmpDir.path.name + " (2, 2)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
    }

    public void testGeotag()
    {
        def fmt = new SimpleDateFormat("z yyyy-MM-dd HH:mm:ss");
        Image image = constructImage(DAY1, IMAGE1)
        WaypointFile wpf = constructWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
        tmpDir.waypointFiles.put("dummy", wpf)
        tmpDir.images.put(IMAGE1, image)
        tmpDir.add(image.day)
        image.waypoint = null
        assertNull(model[0].images[IMAGE1].waypoint)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0

        dialogs.add(null)
        view.tagButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        assertNull(model[0].images[IMAGE1].waypoint)

        dialogs.add(Integer.valueOf(-5 * 60 * 60))
        view.tagButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        def wp = model[0].images[IMAGE1].waypoint
        assertNotNull(wp)
        assertEquals(fmt.parse("GMT 2009-11-10 15:00:00"), wp.timestamp)
    }

    public void testExport()
    {
        copyFile(IMAGE1)
        Image image = constructImage(DAY1, IMAGE1)
        WaypointFile wpf = constructWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
        tmpDir.waypointFiles.put("dummy", wpf)
        tmpDir.images.put(IMAGE1, image)
        tmpDir.add(image.day)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0


        assertEquals(1, tmpDir.path.list().length)
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.path.list().length)

        dialogs.add(new File(tmpDir.path, "export.kmz"))
        view.exportButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(2, tmpDir.path.list().length)
    }

    public void testOffsetFinder()
    {
        Image image = constructImage(DAY1, IMAGE1)
        WaypointFile wpf = constructWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
        tmpDir.waypointFiles.put("dummy", wpf)
        tmpDir.images.put(IMAGE1, image)
        tmpDir.add(image.day)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0

        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        editor.geoLink.actionListeners[0].actionPerformed()
        def file = dialogs.getOpened()
        assertNotNull(file)
        assertTrue(file.name.endsWith(".kml"))
    }

    public void testImageView()
    {
        DateFormat fmt = DateFormat.getDateTimeInstance();
        fmt.setTimeZone(ZONE)
        Image image1 = constructImage(DAY1, IMAGE1)
        Image image2 = constructImage(DAY1, IMAGE2)
        Image image3 = constructImage(DAY1, IMAGE2)
        Image image4 = constructImage(DAY1, IMAGE2)
        model.add(tmpDir)
        tmpDir.images.put(IMAGE1, image1)
        tmpDir.images.put(IMAGE2, image2)
        tmpDir.add(image1.day)

        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0

        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertEquals("testTitle", editor.title.text)
        assertEquals("testDescription", editor.textArea.text)
        assertEquals(IMAGE1, editor.filename.text)
        assertEquals(fmt.format(image1.time), editor.time.text)
        assertEquals("0", editor.timeDiff.text)
        assertEquals(image1.waypoint.latitude.toString(), editor.latitude.text)
        assertEquals(image1.waypoint.longitude.toString(), editor.longitude.text)
        assertTrue(editor.export.selected)

        editor.title.text = "changedTitle"
        editor.textArea.text = "changedDescription"
        editor.export.selected = false

        image2.title = "otherTitle"
        image2.description = "otherDescription"
        image2.time = null
        image2.waypoint = null

        view.imageTable.editCellAt(1,0)

        assertEquals("changedTitle", image1.title)
        assertEquals("changedDescription", image1.description)
        assertFalse(image1.export)

        editor = view.imageTable.editorComponent
        assertEquals("otherTitle", editor.title.text)
        assertEquals("otherDescription", editor.textArea.text)
        assertEquals(IMAGE2, editor.filename.text)
        assertEquals("", editor.time.text)
        assertEquals("", editor.timeDiff.text)
        assertEquals("?", editor.latitude.text)
        assertEquals("?", editor.longitude.text)
        assertTrue(editor.export.selected)

        image3.waypoint = null
        view.imageTable.editCellAt(2,0)
        editor = view.imageTable.editorComponent
        assertEquals(fmt.format(image3.time), editor.time.text)
        assertEquals("", editor.timeDiff.text)

        image4.waypoint.timestamp = new Date(image4.waypoint.timestamp.time+2000)
        view.imageTable.editCellAt(3,0)
        editor = view.imageTable.editorComponent
        assertEquals(fmt.format(image3.time), editor.time.text)
        assertEquals("-2", editor.timeDiff.text)
    }

    public void testAutoCommit()
    {
        DateFormat fmt = DateFormat.getDateTimeInstance();
        fmt.setTimeZone(ZONE)
        Image image = constructImage(DAY1, IMAGE1)
        model.add(tmpDir)
        tmpDir.images.put(IMAGE1, image)
        tmpDir.add(image.day)

        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0

        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        editor.title.text = "changedTitle"
        assertEquals("testTitle", image.title)
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        assertEquals("changedTitle", image.title)

        editor.title.text = "otherTitle"
        view.saveButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertEquals("otherTitle", image.title)
    }

    public void testScanOptions()
    {
        String[] zones = TimeZone.getAvailableIDs();
        Arrays.sort(zones);

        ScanOptions op = new ScanOptions(TimeZone.getTimeZone("GMT"))
        assertEquals("GMT", op.getTimezone().getID())
        op.combobox.selectedIndex = 1;
        assertEquals(zones[1], op.getTimezone().getID())

        op = new ScanOptions(null)
        assertNull(op.getTimezone())
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


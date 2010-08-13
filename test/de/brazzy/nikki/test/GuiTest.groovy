package de.brazzy.nikki.test
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Directory 
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.ImageSortField
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.util.ConfirmResult 
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.util.ParserFactory;
import de.brazzy.nikki.view.AboutBox 
import de.brazzy.nikki.view.GeotagOptions;
import de.brazzy.nikki.view.ImageView;
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.view.ScanOptions;


import java.awt.event.WindowEvent 
import java.security.Permission;
import org.apache.commons.io.FileUtils 
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone 
import org.joda.time.Seconds 

/**
 * @author Michael Borgwardt
 */
class GuiTest extends AbstractNikkiTest {
    
    TestDialogs dialogs
    NikkiModel model
    NikkiFrame view
    Nikki nikki
    Integer exitStatus = null
    final long baseTime = System.currentTimeMillis()-10000000    
    
    public void setUp() {
        super.setUp()
        dialogs = new TestDialogs()
        nikki = new Nikki()
        ParserFactory pf = new ParserFactory();
        nikki.build(GuiTest.class, dialogs, new TimezoneFinder(), pf)
        model = nikki.model
        view = nikki.view
        System.setSecurityManager(new NoExitSecurityManager(testCase:this))
    }
    
    public void tearDown() {
        assertTrue(dialogs.isQueueEmpty())
        System.setSecurityManager(null) // or save and restore original
        view.frame.dispose()
        super.tearDown()
    }
    
    public void testAdd() {
        assertEquals(0, model.size())
        dialogs.add(null)
        view.addButton.actionListeners[0].actionPerformed()
        assertEquals(0, model.size())
        dialogs.add(new File("C:\\tmp"))
        view.addButton.actionListeners[0].actionPerformed()
        assertEquals(1, model.size())
        assertEquals("tmp (0, 0)", model[0].toString())
    }
    
    public void testRemove() {
        model.add(new Directory(path: new File("C:\\tmp1")))
        model.add(new Directory(path: new File("C:\\tmp2")))
        assertEquals(2, model.size())
        view.dirList.selectedIndex = 0
        view.deleteButton.actionListeners[0].actionPerformed()
        assertEquals(1, model.size())
        assertEquals("tmp2 (0, 0)", model[0].toString())
    }
    
    public void testButtons() {
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        model.add(tmpDir)
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dirList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        addImage(DAY1, IMAGE1)
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dayList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertTrue(view.tagButton.enabled)
        assertTrue(view.exportButton.enabled)
        assertTrue(view.exportAllButton.enabled)
        assertTrue(view.exportNoneButton.enabled)
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertTrue(editor.offsetFinder.enabled)
        
        view.dayList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dirList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.tagButton.enabled)
        assertFalse(view.exportButton.enabled)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
    }
    
    public void testScanSaveRescan() {
        model.add(tmpDir)
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        view.dirList.selectedIndex = 0
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        def imgFile = new File(tmpDir.path, IMAGE1)
        def timestamp = imgFile.lastModified()
        
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(TZ_DARWIN, tmpDir.images[IMAGE1].time.zone)
        assertEquals(tmpDir.path.name + " (1, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 3)", tmpDir[0].toString())
        
        assertEquals(2, tmpDir.path.list().length)
        tmpDir.images[IMAGE1].title = "changedTitle"
        view.saveButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(imgFile.lastModified() > timestamp)
        assertEquals(2, tmpDir.path.list().length)
        
        model.remove(tmpDir)
        tmpDir = new Directory(path: tmpDir.path);
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        FileUtils.copyFile(imgFile, new File(tmpDir.path, "other.JPG"))
        copyFile(IMAGE2)
        copyFile(WAYPOINTS2)
        
        dialogs.add(null)
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.images.size())
        assertEquals(1, tmpDir.size())
        
        dialogs.add(TZ_BERLIN)
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertEquals(2, tmpDir.size())
        assertEquals(TZ_DARWIN, tmpDir.images[IMAGE1].time.zone)
        assertEquals(TZ_BERLIN, tmpDir.images[IMAGE2].time.zone)
        assertEquals(tmpDir.path.name + " (3, 2)", model[0].toString())
        assertEquals(DATE1+" (2, 3)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        
        assertTrue(imgFile.delete())
        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        
        assertEquals(tmpDir.path.name + " (2, 2)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
    }
    
    public void testGeotag() {
        Image image = addImage(DAY1, IMAGE1)
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
        image.waypoint = null
        assertNull(model[0].images[IMAGE1].waypoint)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        dialogs.add(null)
        view.tagButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        assertNull(model[0].images[IMAGE1].waypoint)
        
        dialogs.add(Seconds.seconds(-5 * 60 * 60))
        view.tagButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        def wp = model[0].images[IMAGE1].waypoint
        assertNotNull(wp)
        assertEquals(new DateTime(2009, 11, 11, 1, 0, 0, 0, TZ_DARWIN), wp.timestamp)
    }
    
    public void testExport() {
        copyFile(IMAGE1)
        Image image = addImage(DAY1, IMAGE1)
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
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
    
    public void testOffsetFinder() {
        Image image = addImage(DAY1, IMAGE1)
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        editor.offsetFinder.actionListeners[0].actionPerformed()
        def file = dialogs.getOpened()
        assertNotNull(file)
        assertTrue(file.name.endsWith(".kml"))
    }
    
    public void testImageView() {
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, "a"+IMAGE2)
        Image image3 = addImage(DAY1, "b"+IMAGE2)
        Image image4 = addImage(DAY1, "c"+IMAGE2)
        image1.modified = false
        model.add(tmpDir)
        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertEquals("testTitle", editor.title.text)
        assertEquals("testDescription", editor.textArea.text)
        assertEquals(IMAGE1, editor.filename.text)
        assertEquals(FORMAT_TIME.print(image1.time), editor.time.text)
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
        
        assertFalse(image1.modified)
        view.imageTable.editCellAt(1,0)
        assertTrue(image1.modified)
        
        assertEquals("changedTitle", image1.title)
        assertEquals("changedDescription", image1.description)
        assertFalse(image1.export)
        
        editor = view.imageTable.editorComponent
        assertEquals("otherTitle", editor.title.text)
        assertEquals("otherDescription", editor.textArea.text)
        assertEquals("a"+IMAGE2, editor.filename.text)
        assertEquals("", editor.time.text)
        assertEquals("", editor.timeDiff.text)
        assertEquals("?", editor.latitude.text)
        assertEquals("?", editor.longitude.text)
        assertTrue(editor.export.selected)
        
        image3.waypoint = null
        view.imageTable.editCellAt(2,0)
        editor = view.imageTable.editorComponent
        assertEquals(FORMAT_TIME.print(image3.time), editor.time.text)
        assertEquals("", editor.timeDiff.text)
        
        image4.waypoint.timestamp = new DateTime(image4.waypoint.timestamp.millis+2000)
        view.imageTable.editCellAt(3,0)
        editor = view.imageTable.editorComponent
        assertEquals(FORMAT_TIME.print(image3.time), editor.time.text)
        assertEquals("-2", editor.timeDiff.text)
    }
    
    public void testImageSort() {
        Image image1_c7 = addImage(DAY1, "c",7)
        Image image1_b9 = addImage(DAY1, "b",9)
        Image image1_a8 = addImage(DAY1, "a",8)
        
        Image image2_f4 = addImage(DAY2, "f",4)
        Image image2_e6 = addImage(DAY2, "e",6)
        Image image2_d5 = addImage(DAY2, "d",5)
        
        Image image3_x = addImage(null, "x")
        Image image3_y = addImage(null, "y")
        
        assertNull(view.imageSortOrder.selectedItem)
        assertFalse(view.imageSortOrder.enabled)
        
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        assertEquals(ImageSortField.FILENAME, view.imageSortOrder.selectedItem)
        assertFalse(view.imageSortOrder.enabled)
        assertImageName(0, "x");
        assertImageName(1, "y");
        
        view.dayList.selectedIndex = 1        
        assertEquals(ImageSortField.TIME, view.imageSortOrder.selectedItem)
        assertTrue(view.imageSortOrder.enabled)
        assertImageName(0, "c");
        assertImageName(1, "a");
        assertImageName(2, "b");
        view.imageSortOrder.selectedItem = ImageSortField.FILENAME
        assertImageName(0, "a");
        assertImageName(1, "b");
        assertImageName(2, "c");
        
        view.dayList.selectedIndex = 2
        assertEquals(ImageSortField.TIME, view.imageSortOrder.selectedItem)
        assertTrue(view.imageSortOrder.enabled)
        assertImageName(0, "f");
        assertImageName(1, "d");
        assertImageName(2, "e");
        view.imageSortOrder.selectedItem = ImageSortField.FILENAME
        assertImageName(0, "d");
        assertImageName(1, "e");
        assertImageName(2, "f");
        view.imageSortOrder.selectedItem = ImageSortField.TIME
        assertImageName(0, "f");
        assertImageName(1, "d");
        assertImageName(2, "e");
        
        view.dayList.selectedIndex = 1
        assertEquals(ImageSortField.FILENAME, view.imageSortOrder.selectedItem)
        assertTrue(view.imageSortOrder.enabled)
        assertImageName(0, "a");
        assertImageName(1, "b");
        assertImageName(2, "c");
        view.imageSortOrder.selectedItem = ImageSortField.TIME
        assertImageName(0, "c");
        assertImageName(1, "a");
        assertImageName(2, "b");
        
        view.dayList.selectedIndex = 0
        assertEquals(ImageSortField.FILENAME, view.imageSortOrder.selectedItem)
        assertFalse(view.imageSortOrder.enabled)
        assertImageName(0, "x");
        assertImageName(1, "y");
        
        view.dayList.clearSelection()
        assertNull(view.imageSortOrder.selectedItem)
        assertFalse(view.imageSortOrder.enabled)
    }
    
    private assertImageName(int index, String name){
        view.imageTable.editCellAt(index,0)
        def editor = view.imageTable.editorComponent
        assertEquals(name, editor.filename.text)        
    }
    
    public void testAutoCommit() {
        copyFile(IMAGE1)
        Image image = addImage(DAY1, IMAGE1)
        addWaypointFile(DAY1, WAYPOINTS1)
        model.add(tmpDir)
        
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
    
    public void testCopyPaste() {
        Image imageWithDate = addImage(DAY1, IMAGE1)
        Image imageNoDate = addImage(null, NO_EXIF)
        model.add(tmpDir)
        assertEquals(2, tmpDir.getSize())
        assertEquals(1, tmpDir[0].images.size())
        assertEquals(1, tmpDir[1].images.size())
        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertFalse(editor.copy.enabled)
        assertFalse(editor.paste.enabled)
        
        view.dayList.selectedIndex = 1
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent
        assertTrue(editor.copy.enabled)
        assertFalse(editor.paste.enabled)
        
        editor.copy.actionListeners[0].actionPerformed()
        assertTrue(editor.copy.enabled)
        assertTrue(editor.paste.enabled)
        
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent
        assertFalse(editor.copy.enabled)
        assertTrue(editor.paste.enabled)
        assertNull(imageNoDate.time)
        assertNull(imageNoDate.waypoint)
        
        editor.paste.actionListeners[0].actionPerformed()
        assertEquals(-1, view.dayList.selectedIndex)
        
        assertEquals(imageNoDate.time, imageWithDate.time)
        assertSame(imageNoDate.waypoint, imageWithDate.waypoint)
        assertEquals(1, tmpDir.getSize())
        assertEquals(2, tmpDir[0].images.size())
        
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        assertTrue(editor.copy.enabled)
        assertTrue(editor.paste.enabled)
    }
    
    public void testHelp() {
        dialogs.add(null)
        view.helpButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
    }
    
    public void testAboutBox() {
        AboutBox box = new AboutBox();
        assertTrue(box.content.text.contains("Nikki GPS"))
        assertTrue(box.content.text.contains("Michael Borgwardt"))
    }
    
    public void testScanOptions() {
        String[] zones = DateTimeZone.getAvailableIDs().toArray()
        Arrays.sort(zones);
        
        ScanOptions op = new ScanOptions(DateTimeZone.UTC)
        assertEquals("UTC", op.getTimezone().getID())
        op.combobox.selectedIndex = 1;
        assertEquals(zones[1], op.getTimezone().getID())
        
        op = new ScanOptions(null)
        assertNull(op.getTimezone())
    }
    
    public void testGeotagOptions() {
        GeotagOptions op = new GeotagOptions()
        assertEquals(0, op.getOffset())
        op.spinner.value = op.spinner.nextValue;
        assertEquals(10, op.getOffset())
        op.spinner.value = -100;
        assertEquals(-100, op.getOffset())
    }
    
    /**
     * Using the same instance as table editor and renderer causes
     * UI defects when a table cell is removed.
     */
    public void testSeparateRendererAndEditor() {
        def editor = view.imageTable.getDefaultEditor(Object.class)
        def renderer = view.imageTable.getDefaultRenderer(Object.class)
        assertSame(editor.getClass(), renderer.getClass())
        assertNotSame(editor, renderer)
    }
    
    private File prepareTestModified() {
        model.add(tmpDir)
        copyFile(IMAGE1)
        addImage(DAY1, IMAGE1)
        def file = new File(tmpDir.path, IMAGE1)
        assertTrue(file.setLastModified(baseTime))
        return file
    }
    
    public void testNotModified() {
        def file = prepareTestModified()
        tmpDir.images[IMAGE1].modified = false
        try {
            view.frame.dispatchEvent(new WindowEvent(view.frame, WindowEvent.WINDOW_CLOSING))
            fail("did not exit!");
        }
        catch(ExitException e) {
            assertEquals(Nikki.EXIT_CODE_NO_MODIFICATIONS, exitStatus)
            assertTrue(dialogs.isQueueEmpty())
            assertEquals(baseTime, file.lastModified())
        }
    }
    
    public void testModifiedSave() {
        def file = prepareTestModified()
        dialogs.add(ConfirmResult.YES)
        view.frame.dispatchEvent(new WindowEvent(view.frame, WindowEvent.WINDOW_CLOSING))
        dialogs.registerWorker(null)
        assertEquals(Nikki.EXIT_CODE_SAVED_MODIFICATIONS, exitStatus)
        assertTrue(dialogs.isQueueEmpty())
        assertFalse(baseTime == file.lastModified())
    }
    
    public void testModifiedExit() {
        def file = prepareTestModified()
        dialogs.add(ConfirmResult.NO)
        try {
            view.frame.dispatchEvent(new WindowEvent(view.frame, WindowEvent.WINDOW_CLOSING))
            fail("did not exit!");
        }
        catch(ExitException e) {
            assertEquals(Nikki.EXIT_CODE_UNSAVED_MODIFICATIONS, exitStatus)
            assertTrue(dialogs.isQueueEmpty())
            assertEquals(baseTime, file.lastModified())
        }
    }    
    
    public void testModifiedCancel() {
        def file = prepareTestModified()
        
        dialogs.add(ConfirmResult.CANCEL)
        view.frame.dispatchEvent(new WindowEvent(view.frame, WindowEvent.WINDOW_CLOSING))
        assertNull(exitStatus)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(baseTime, file.lastModified())
    }
    
    public void testExportNoWaypoints() {
        Image image = addImage(DAY1, IMAGE1)
        assertTrue(image.export)
        model.add(tmpDir)
        tmpDir[0].waypoints.clear()
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        dialogs.add(ConfirmResult.CANCEL)
        assertNotNull(view.dayList.selectedValue)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
    }
    
    public void testExportNoImage() {
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, IMAGE2)
        image1.export = false
        image2.export = false
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        dialogs.add(ConfirmResult.CANCEL)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        
        dialogs.add(ConfirmResult.YES)
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        
        image2.export = true
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
    }
    
    public void testExportLock() {
        Image image1 = addImage(DAY1, IMAGE2)
        Image image2 = addImage(DAY1, IMAGE1)
        Image image3 = addImage(DAY1, "dummy")
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        image1.export = false
        image1.waypoint = null
        image2.export = false
        image2.waypoint = null
        image3.export = false
        assertNotNull(image3.waypoint)
        model.add(tmpDir)
        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(2,0)
        def editor = view.imageTable.editorComponent
        assertTrue(editor.export.enabled)
        assertEquals(editor.export.toolTipText, Texts.Image.EXPORT_TOOLTIP)
        editor.copy.actionListeners[0].actionPerformed()
        
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent        
        assertFalse(editor.export.enabled)
        assertEquals(editor.export.toolTipText, Texts.Image.EXPORT_LOCKED_TOOLTIP)
        editor.paste.actionListeners[0].actionPerformed()
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent        
        assertTrue(editor.export.enabled)
        assertEquals(editor.export.toolTipText, Texts.Image.EXPORT_TOOLTIP)
        
        view.imageTable.editCellAt(1,0)
        editor = view.imageTable.editorComponent
        assertFalse(editor.export.enabled)
        assertEquals(editor.export.toolTipText, Texts.Image.EXPORT_LOCKED_TOOLTIP)
        
        dialogs.add(null)
        view.tagButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent        
        
        assertTrue(editor.export.enabled)
        assertEquals(editor.export.toolTipText, Texts.Image.EXPORT_TOOLTIP)
    }
    
    public void testAutoSelectExport() {
        Image image1 = addImage(DAY1, IMAGE2)
        image1.export = false
        image1.title = null
        model.add(tmpDir)        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertFalse(editor.export.selected)
        assertFalse(image1.export)
        editor.title.text = "changedTitle"
        assertTrue(editor.export.selected)
        view.dayList.selectedIndex = -1
        assertTrue(image1.export)
    }
    
    public void testMassSelectExport() {
        Image image1 = addImage(DAY1, IMAGE2)
        Image image2 = addImage(DAY1, IMAGE1)
        WaypointFile wpf = addWaypointFile(DAY2, "dummy")
        image1.export = false
        image2.export = false
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertFalse(image1.export)
        assertFalse(image2.export)
        assertFalse(editor.export.selected)
        
        view.exportAllButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        
        assertTrue(editor.export.selected)
        assertTrue(image1.export)
        assertTrue(image2.export)
        
        view.imageTable.editCellAt(1,0)
        editor = view.imageTable.editorComponent
        assertTrue(editor.export.selected)
        
        view.exportNoneButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        
        assertFalse(editor.export.selected)
        assertFalse(image1.export)
        assertFalse(image2.export)
        
        view.dayList.selectedIndex = 2
        view.exportAllButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        view.exportNoneButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
    }
}

class ExitException extends SecurityException {
}

class NoExitSecurityManager extends SecurityManager {
    def GuiTest testCase
    
    // allow anything.
    @Override
    public void checkPermission(Permission perm) {
    }
    public void checkPermission(Permission perm, Object context) {
    }
    
    @Override
    public void checkExit(int status) {
        testCase.exitStatus = Integer.valueOf(status)
        throw new ExitException()
    }
}






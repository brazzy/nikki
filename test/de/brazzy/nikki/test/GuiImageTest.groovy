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

import org.joda.time.DateTime;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.ImageSortField;
import de.brazzy.nikki.model.WaypointFile;

/**
 * @author Michael Borgwardt
 *
 */
class GuiImageTest extends GuiTest {
    
    public void testButtons() {
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        model.add(tmpDir)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dirList.selectedIndex = 0
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        addImage(DAY1, IMAGE1)
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dayList.selectedIndex = 0
        assertTrue(view.exportAllButton.enabled)
        assertTrue(view.exportNoneButton.enabled)
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertTrue(editor.offsetFinder.enabled)
        
        view.dayList.clearSelection()
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
        
        view.dirList.clearSelection()
        assertFalse(view.exportAllButton.enabled)
        assertFalse(view.exportNoneButton.enabled)
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
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, IMAGE2)
        image1.export = false
        image1.title = null
        image1.description=null
        image2.export = false
        image2.title = null
        image2.description=null
        image2.waypoint=null
        model.add(tmpDir)        
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertFalse(editor.export.selected)
        assertFalse(image1.export)
        editor.title.text = "changedTitle"
        assertTrue(editor.export.selected)
        editor.export.selected = false
        editor.title.text = "changedTitle2"
        assertFalse(editor.export.selected)
        editor.title.text = ""
        editor.title.text = "changedTitle"
        assertFalse(editor.export.selected)
        editor.textArea.text = "changedDescription"
        assertFalse(editor.export.selected)
        
        view.imageTable.editCellAt(1,0)
        dialogs.registerWorker(null)
        assertFalse(image1.export)
        image1.title = null
        image1.description=null        
        editor = view.imageTable.editorComponent
        assertFalse(editor.export.selected)
        editor.title.text = "changedTitle"
        assertFalse(editor.export.selected)
        
        view.imageTable.editCellAt(0,0)
        dialogs.registerWorker(null)
        assertFalse(image2.export)
        editor = view.imageTable.editorComponent
        assertFalse(editor.export.selected)
        editor.textArea.text = "changedDescription"
        assertTrue(editor.export.selected)
        image2.title = null
        image2.waypoint = constructWaypoint(image2.day, 2)
        
        view.imageTable.editCellAt(1,0)
        dialogs.registerWorker(null)
        assertTrue(image1.export)
        editor = view.imageTable.editorComponent
        assertFalse(editor.export.selected)
        editor.title.text = "changedTitle"
        assertTrue(editor.export.selected)
    }
    
    public void testMassSelectExport() {
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, IMAGE2)
        WaypointFile wpf = addWaypointFile(DAY2, "dummy")
        image1.export = false
        image2.export = false
        image2.waypoint = null
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        view.dayList.selectedIndex = 0
        
        view.imageTable.editCellAt(0,0)
        def editor = view.imageTable.editorComponent
        assertFalse(image1.export)
        assertFalse(image2.export)
        assertFalse(editor.export.selected)
        editor.title.text = "changedTitle"
        
        view.exportAllButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        
        assertTrue(editor.export.selected)
        assertTrue(image1.export)
        assertFalse(image2.export)
        assertEquals(image1.title, "changedTitle")
        
        image2.waypoint = constructWaypoint(image2.day, 2)
        
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

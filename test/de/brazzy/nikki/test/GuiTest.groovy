package de.brazzy.nikki.test
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.view.AboutBox;
import de.brazzy.nikki.view.ImageView;
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormatter

/**
 * @author Michael Borgwardt
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
        nikki.build(GuiTest.class, dialogs, new TimezoneFinder())
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

        addImage(DAY1, IMAGE1)
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
        assertTrue(editor.offsetFinder.enabled)

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
        def imgFile = new File(tmpDir.path, IMAGE1)
        def timestamp = imgFile.lastModified()

        view.scanButton.actionListeners[0].actionPerformed()
        dialogs.registerWorker(null)
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(TZ_DARWIN, tmpDir.images[IMAGE1].time.zone)
        assertEquals(tmpDir.path.name + " (1, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())
        
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
        assertEquals(DATE1+" (2, 2)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
    }

    public void testGeotag()
    {
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
        assertEquals(new DateTime(2009, 11, 11, 1, 0, 0, 0, TZ_BERLIN), wp.timestamp)
    }

    public void testExport()
    {
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

    public void testOffsetFinder()
    {
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

    public void testImageView()
    {
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, IMAGE2)
        Image image3 = addImage(DAY1, IMAGE2)
        Image image4 = addImage(DAY1, IMAGE2)
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
        assertEquals(IMAGE2, editor.filename.text)
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

    public void testAutoCommit()
    {
        copyFile(IMAGE1)
        Image image = addImage(DAY1, IMAGE1)
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

    public void testCopyPaste()
    {
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
        def button = editor.copyPaste
        assertFalse(button.enabled)
        assertSame(ImageView.PASTE_ICON, button.icon)
        
        view.dayList.selectedIndex = 1
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent
        button = editor.copyPaste
        assertTrue(button.enabled)
        assertSame(ImageView.COPY_ICON, button.icon)
        
        button.actionListeners[0].actionPerformed()
        assertTrue(button.enabled)
        assertSame(ImageView.COPY_ICON, button.icon)
        
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        editor = view.imageTable.editorComponent
        button = editor.copyPaste
        assertTrue(button.enabled)
        assertSame(ImageView.PASTE_ICON, button.icon)
        
        button.actionListeners[0].actionPerformed()
        assertEquals(-1, view.dayList.selectedIndex)
        
        assertEquals(imageNoDate.time, imageWithDate.time)
        assertEquals(1, tmpDir.getSize())
        assertEquals(2, tmpDir[0].images.size())
        
        view.dayList.selectedIndex = 0
        view.imageTable.editCellAt(0,0)
        assertTrue(button.enabled)
        assertSame(ImageView.COPY_ICON, button.icon)        
    }
    
    public void testHelp()
    {
        dialogs.add(null)
        view.helpButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
    }

    public void testAboutBox()
    {
        AboutBox box = new AboutBox();
        assertTrue(box.content.text.contains("Nikki GPS"))
        assertTrue(box.content.text.contains("Michael Borgwardt"))
    }

    public void testScanOptions()
    {
        String[] zones = DateTimeZone.getAvailableIDs().toArray()
        Arrays.sort(zones);

        ScanOptions op = new ScanOptions(DateTimeZone.UTC)
        assertEquals("UTC", op.getTimezone().getID())
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
    
    public void testSeparateRendererAndEditor()
    {
        def editor = view.imageTable.getDefaultEditor(Object.class)
        def renderer = view.imageTable.getDefaultRenderer(Object.class)
        assertSame(editor.getClass(), renderer.getClass())
        assertNotSame(editor, renderer)
    }
}


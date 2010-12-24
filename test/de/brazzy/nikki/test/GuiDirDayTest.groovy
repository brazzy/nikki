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


import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.Seconds;

import slash.navigation.nmea.NmeaFormat;

import de.brazzy.nikki.model.Directory;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.WaypointFile;
import de.brazzy.nikki.util.ConfirmResult;
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.view.GeotagOptions;
import de.brazzy.nikki.view.ScanOptions;

/**
 * @author Michael Borgwardt
 *
 */
class GuiDirDayTest extends GuiTest {
    
    public void testAdd() {
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        model.add(new Directory(path: new File("C:\\aaa")))
        model.add(new Directory(path: new File("C:\\xxx")))
        assertEquals(2, model.size())
        dialogs.add(null)
        view.addButton.actionListeners[0].actionPerformed()
        assertEquals(2, model.size())
        dialogs.add(tmpDir.path)
        view.addButton.actionListeners[0].actionPerformed()
        assertEquals(3, model.size())
        assertEquals(1, view.dirList.selectedIndex)
        assertEquals(tmpDir.path.name+" (1, 1)", model[1].toString())
        assertEquals(0, view.dayList.selectedIndex)
    }
    
    public void testRemove() {
        ensureTmpDir()
        model.add(new Directory(path: new File("C:\\xxx")))
        model.add(tmpDir)
        assertEquals(2, model.size())
        view.dirList.selectedIndex = 0
        view.deleteButton.actionListeners[0].actionPerformed()
        assertEquals(1, model.size())
        assertEquals("xxx (0, 0)", model[0].toString())
        assertEquals(view.dirList.selectedIndex, -1)
        assertEquals(view.dayList.selectedIndex, -1)
    }
    
    public void testButtons() {
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
        
        model.add(tmpDir)
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
        
        ensureTmpDir()
        view.dirList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
        
        addImage(DAY1, IMAGE1)
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
        
        view.dayList.selectedIndex = 0
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertTrue(view.exportButton.enabled)
        
        view.dayList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertTrue(view.deleteButton.enabled)
        assertTrue(view.scanButton.enabled)
        assertTrue(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
        
        view.dirList.clearSelection()
        assertTrue(view.addButton.enabled)
        assertFalse(view.deleteButton.enabled)
        assertFalse(view.scanButton.enabled)
        assertFalse(view.saveButton.enabled)
        assertFalse(view.exportButton.enabled)
    }
    
    public void testScanSaveRescan() {
        def finder = new MockTimezoneFinder()
        finder.addCall(Float.NaN,Float.NaN,TZ_DARWIN)
        finder.addCall(Float.NaN,Float.NaN,TZ_DARWIN)
        finder.addCall(Float.NaN,Float.NaN,TZ_BERLIN)
        finder.addCall(Float.NaN,Float.NaN,TZ_BERLIN)
        nikki.timezoneFinder = finder
        
        
        model.add(tmpDir)
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        copyFile(IMAGE1)
        copyFile(WAYPOINTS1)
        def imgFile = new File(tmpDir.path, IMAGE1)
        def timestamp = imgFile.lastModified()
        
        view.dirList.selectedIndex = 0
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.size())
        assertEquals(TZ_DARWIN, tmpDir.images[IMAGE1].waypoint.timestamp.zone)
        assertEquals(tmpDir.path.name + " (1, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 2)", tmpDir[0].toString())
        assertEquals(0, view.dayList.selectedIndex)
        
        assertEquals(2, tmpDir.path.list().length)
        tmpDir.images[IMAGE1].title = "changedTitle"
        view.saveButton.actionListeners[0].actionPerformed()
        assertTrue(imgFile.lastModified() > timestamp)
        assertEquals(2, tmpDir.path.list().length)
        
        model.remove(tmpDir)
        FileUtils.deleteDirectory(tmpDir.path)
        tmpDir.path.mkdir()
        tmpDir = new Directory(path: tmpDir.path);
        model.add(tmpDir)
        copyFile(WAYPOINTS2)
        copyFile(IMAGE2)
        
        dialogs.add(null)
        view.dirList.selectedIndex = 0
        finder.finished()
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(0, tmpDir.images.size())
        assertEquals(1, tmpDir.waypointFiles.size())
        assertEquals(1, tmpDir.size())
        assertEquals(DATE2+" (0, 2)", tmpDir[0].toString())
        assertEquals(0, view.dayList.selectedIndex)
        
        copyFile(IMAGE1)
        def otherFile = new File(tmpDir.path, "other1.JPG")
        FileUtils.copyFile(imgFile, otherFile)
        FileUtils.moveFile(imgFile, new File(tmpDir.path, "other2.JPG"))
        dialogs.add(TZ_BERLIN)
        view.scanButton.actionListeners[0].actionPerformed()
        assertEquals(2, tmpDir.size())
        assertEquals(TZ_DARWIN, tmpDir.images[otherFile.name].waypoint.timestamp.zone)
        assertEquals(TZ_BERLIN, tmpDir.images[IMAGE2].waypoint.timestamp.zone)
        assertEquals(tmpDir.path.name + " (3, 1)", model[0].toString())
        assertEquals(DATE1+" (2, 0)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
        
        assertEquals(1, view.dayList.selectedIndex)
        view.imageTable.editCellAt(0,0)
        
        assertTrue(otherFile.delete())
        view.scanButton.actionListeners[0].actionPerformed()
        
        assertEquals(tmpDir.path.name + " (2, 1)", model[0].toString())
        assertEquals(DATE1+" (1, 0)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 2)", tmpDir[1].toString())
    }
    
    public void testGeotag() {
        nikki.timezoneFinder = new TimezoneFinder(TimezoneFinder.class
                .getResourceAsStream("timezones.dat"))
        copyFile("auto_geotag.jpg")
        copyFile("auto_geotag1.nmea")
        model.add(tmpDir)
        dialogs.add(TZ_STOCKHOLM)
        view.dirList.selectedIndex = 0
        
        def img = tmpDir.images["auto_geotag.jpg"]
        assertEquals(1, tmpDir.size)
        assertEquals(TZ_STOCKHOLM, img.waypoint.timestamp.zone)
        assertEquals(new LocalDate(2010,7,22), tmpDir[0].date)
        
        copyFile("auto_geotag2.nmea")
        view.scanButton.actionListeners[0].actionPerformed()
        
        assertEquals(2, tmpDir.size)
        assertEquals(TZ_DARWIN, img.waypoint.timestamp.zone)
        assertEquals(new LocalDate(2010,7,23), tmpDir[1].date)
        assertSame(tmpDir[1], img.day)
    }
    
    public void testExport() {
        ensureTmpDir()
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        
        copyFile(IMAGE1)
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        Image image = addImage(DAY1, IMAGE1)
        view.dayList.selectedIndex = 0
        
        assertEquals(1, tmpDir.path.list().length)
        dialogs.add(null)
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(1, tmpDir.path.list().length)
        
        dialogs.add(new File(tmpDir.path, "export.kmz"))
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        assertEquals(2, tmpDir.path.list().length)
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
    
    public void testExportNoWaypoints() {
        ensureTmpDir()
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        addWaypointFile(DAY1, "dummy")
        
        Image image = addImage(DAY1, IMAGE1)
        assertTrue(image.export)
        tmpDir[0].waypoints.clear()
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
        ensureTmpDir()
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        
        Image image1 = addImage(DAY1, IMAGE1)
        Image image2 = addImage(DAY1, IMAGE2)
        image1.export = false
        image2.export = false
        
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
    
    public void testSaveError() {
        ensureTmpDir()
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        
        addImage(DAY1, IMAGE1)
        tmpDir.images[IMAGE1].title = "changedTitle"
        assertFalse(logContains(IMAGE1));
        dialogs.add("error");
        view.saveButton.actionListeners[0].actionPerformed()
        assertTrue(logContains(IMAGE1));
        assertTrue(dialogs.isQueueEmpty())
    }
    
    public void testScanError() {
        model.add(tmpDir)
        ensureTmpDir()
        new File(tmpDir.path, WAYPOINTS1).write("Erroneous data")
        assertFalse(logContains(WAYPOINTS1));
        dialogs.add("error");
        view.dirList.selectedIndex = 0
        assertTrue(logContains(WAYPOINTS1));
        assertTrue(dialogs.isQueueEmpty())
    }
    
    public void testExportError() {
        copyFile(IMAGE1)
        model.add(tmpDir)
        addWaypointFile(DAY1, "dummy")
        Image image = addImage(DAY1, IMAGE1)
        view.dirList.selectedIndex = 0
        
        WaypointFile wpf = addWaypointFile(DAY1, "dummy")
        view.dayList.selectedIndex = 0
        
        assertFalse(logContains("_doesnt_exist"));
        dialogs.add(new File(tmpDir.path.absolutePath+"_doesnt_exist", "export.kmz"))
        dialogs.add("error");
        view.exportButton.actionListeners[0].actionPerformed()
        assertTrue(dialogs.isQueueEmpty())
        assertTrue(logContains("_doesnt_exist"));
    }
}

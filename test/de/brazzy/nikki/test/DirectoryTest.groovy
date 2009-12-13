package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.util.RelativeDateFormat
import de.brazzy.nikki.view.NikkiFrame
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Brazil
 */
public class DirectoryTest extends AbstractNikkiTest {
    private static final TimeZone ZONE = TimeZone.getTimeZone("GMT+10")
    private static final RelativeDateFormat FORMAT = new RelativeDateFormat(ZONE)
    private static final String DATE1 = "2009-11-11";
    private static final String DATE2 = "2009-11-12";
    private static final Date DAY1 = FORMAT.stripTime(FORMAT.parse(DATE1));
    private static final Date DAY2 = FORMAT.stripTime(FORMAT.parse(DATE2));

    Directory tmpDir;

    public void setUp()
    {
        super.setUp()
        File tmpFile = File.createTempFile("nikkitest",null)
        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()
        tmpDir = new Directory(path: tmpFile)
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
    }

    private void copyFile(String name)
    {
        IOUtils.copy(NikkiModelTest.class.getResourceAsStream(name),
            new FileOutputStream(new File(tmpDir.path, name)))
    }

    public void testScan()
    {
        assertEquals(tmpDir.path.name + " (0, 0)", model[0].toString())
        copyFile("IMG${DATE1}.JPG")
        dialogs.add(ZONE)

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(1, tmpDir.images.size())
        assertEquals(0, tmpDir.waypointFiles.size())
        assertEquals(tmpDir.path.name + " (1, 0)", model[0].toString())
        assertEquals(1, tmpDir.size())

        Day day = tmpDir[0]
        assertSame(tmpDir, day.directory)
        assertEquals(0, day.waypoints.size)
        assertEquals(1, day.images.size)
        assertSame(day.images[0], tmpDir.images["IMG${DATE1}.JPG"])
        assertEquals(day.date, DAY1)
        assertEquals("$DATE1 (1, 0)", day.toString())
    }
    
    public void testSaveRescan()
    {
        testScan()

        assertEquals(1, tmpDir.path.list().length)
        view.saveButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpDir.path.list().length)

        model.remove(tmpDir)
        tmpDir = new Directory(path: tmpDir.path);
        model.add(tmpDir)
        view.dirList.selectedIndex = 0
        copyFile("IMG${DATE2}.JPG");

        view.scanButton.actionListeners[0].actionPerformed()
        Thread.sleep(1000);
        assertEquals(2, tmpDir.size())
        assertEquals(tmpDir.path.name + " (2, 0)", model[0].toString())
        assertEquals(ZONE, tmpDir.zone)
        assertEquals(DATE1+" (1, 0)", tmpDir[0].toString())
        assertEquals(DATE2+" (1, 0)", tmpDir[1].toString())
    }

}


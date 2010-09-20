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

import java.awt.event.WindowEvent;
import java.io.File;
import java.security.Permission;

import de.brazzy.nikki.Nikki;
import de.brazzy.nikki.util.ConfirmResult;

/**
 * @author Michael Borgwardt
 *
 */
class GuiModifiedTest extends GuiTest {
    
    Integer exitStatus = null
    
    public void setUp() {
        super.setUp()
        System.setSecurityManager(new NoExitSecurityManager(testCase:this))
    }
    
    public void tearDown() {
        System.setSecurityManager(null) // or save and restore original
        super.tearDown()
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

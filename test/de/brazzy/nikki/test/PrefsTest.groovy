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

import java.util.prefs.Preferences;

import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory

/**
 *
 * @author Michael Borgwardt
 */
class PrefsTest extends AbstractNikkiTest{
    
    NikkiModel model
    def dirA
    def dirB
    def dirC
    
    public void setUp() {
        model = new NikkiModel(PrefsTest.class)
        dirA = new Directory(path: new File("C:\\testA"))
        dirB = new Directory(path: new File("C:\\testB"))
        dirC = new Directory(path: new File("C:\\testC"))
    }
    
    public void tearDown() {
        Preferences.userNodeForPackage(PrefsTest.class)?.removeNode()
    }
    
    public void testAddDirectory() {
        assertEquals(0, model.size())
        try {
            model.add(null)            
            fail("add succeeded with null argument")
        }
        catch(IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("must not"))
        }
        
        model.add(dirA)
        assertEquals(dirA, model[0])
        setUp()
        assertEquals(1, model.size())
        model.add(dirB)
        model.add(dirC)
        setUp()
        assertEquals(3, model.size())
        assertEquals(dirA, model[0])
        assertEquals(dirB, model[1])
        assertEquals(dirC, model[2])
    }
    
    public void testDeleteDirectory() {
        model.add(dirA)
        model.add(dirB)
        setUp()
        assertEquals(2, model.size())
        assertFalse(model.remove(dirC))
        setUp()
        assertEquals(2, model.size())
        assertTrue(model.remove(dirA))
        setUp()
        assertEquals(1, model.size())
        assertEquals(dirB, model[0])
        assertTrue(model.remove(dirB))
        assertEquals(0, model.size())
        setUp()
        assertEquals(0, model.size())
    }
    
    public void testContains() {
        assertFalse(model.contains(dirA))
        model.add(dirA)
        assertTrue(model.contains(dirA))
        model.remove(dirA)
        assertFalse(model.contains(dirA))        
    }
    
    public void testSelectionDir() {
        model.selectionDir = new File("C:\\sel")
        setUp()
        assertEquals("C:\\sel", model.selectionDir.path)
    }
    
    public void testExportDir() {
        model.exportDir = new File("C:\\exp")
        setUp()
        assertEquals("C:\\exp", model.exportDir.path)
    }
    
}


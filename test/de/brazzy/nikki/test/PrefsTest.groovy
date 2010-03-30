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

    public void setUp()
    {
        model = new NikkiModel(PrefsTest.class)
        dirA = new Directory(path: new File("C:\\testA"))
        dirB = new Directory(path: new File("C:\\testB"))
        dirC = new Directory(path: new File("C:\\testC"))
    }

    public void tearDown()
    {
        Preferences.userNodeForPackage(PrefsTest.class)?.removeNode()
    }
    
    public void testAddDirectory()
    {
        assertEquals(0, model.size())
        try
        {
            model.add(null)            
            fail("add succeeded with null argument")
        }
        catch(IllegalArgumentException ex)
        {
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

    public void testDeleteDirectory()
    {
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
    
    public void testContains()
    {
        assertFalse(model.contains(dirA))
        model.add(dirA)
        assertTrue(model.contains(dirA))
        model.remove(dirA)
        assertFalse(model.contains(dirA))        
    }

    public void testSelectionDir()
    {
        model.selectionDir = new File("C:\\sel")
        setUp()
        assertEquals("C:\\sel", model.selectionDir.path)
    }

    public void testExportDir()
    {
        model.exportDir = new File("C:\\exp")
        setUp()
        assertEquals("C:\\exp", model.exportDir.path)
    }

}


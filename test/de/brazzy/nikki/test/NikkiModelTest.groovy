package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.view.NikkiFrame

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
}


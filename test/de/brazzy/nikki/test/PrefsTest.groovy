package de.brazzy.nikki.test

import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory

/**
 *
 * @author Brazil
 */
class PrefsTest extends AbstractNikkiTest{

    NikkiModel model

    public void setUp()
    {
        model = new NikkiModel(PrefsTest.class)
    }

    public void testAddDirectory()
    {
        assertEquals(0, model.size())
        model.add(new Directory(path: new File("C:\\test1")))
        assertEquals("C:\\test1", model[0].path.path)
        setUp()
        assertEquals(1, model.size())
        model.add(new Directory(path: new File("C:\\test2")))
        model.add(new Directory(path: new File("C:\\test3")))
        setUp()
        assertEquals(3, model.size())
        assertEquals("C:\\test1", model[0].path.path)
        assertEquals("C:\\test2", model[1].path.path)
        assertEquals("C:\\test3", model[2].path.path)
    }

    public void testDeleteDirectory()
    {
        model.add(new Directory(path: new File("C:\\testA")))
        model.add(new Directory(path: new File("C:\\testB")))
        model.add(new Directory(path: new File("C:\\testC")))
        setUp()
        assertEquals(3, model.size())
        model.remove(model[1])
        setUp()
        assertEquals(2, model.size())
        assertEquals("C:\\testA", model[0].path.path)
        assertEquals("C:\\testC", model[1].path.path)
        model.remove(model[0])
        assertEquals(1, model.size())
        model.remove(model[0])
        assertEquals(0, model.size())
        setUp()
        assertEquals(0, model.size())
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


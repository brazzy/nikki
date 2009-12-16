package de.brazzy.nikki.test
import de.brazzy.nikki.util.Dialogs

/**
 *
 * @author Brazil
 */
class TestDialogs extends Dialogs{
    def queue = []

    @Override
    public File askDirectory(File startDir)
    {
        return queue.remove(0)
    }

    @Override
    public File askFile(File dir, String defaultFileName)
    {
        return queue.remove(0)
    }

    @Override
    public Integer askOffset()
    {
        return queue.remove(0)
    }

    @Override
    public TimeZone askTimeZone(TimeZone defaultZone)
    {
        return queue.remove(0)
    }

    @Override
    public void open(File f)
    {
        assert f.equals(queue.remove(0))
    }

    public boolean isQueueEmpty()
    {
        return queue.isEmpty()
    }

    public void add(stuff)
    {
        queue.add(stuff)
    }
}


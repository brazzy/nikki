package de.brazzy.nikki.test
import de.brazzy.nikki.util.Dialogs

/**
 *
 * @author Brazil
 */
class TestDialogs implements Dialogs{
    def queue = []

    public File askDirectory(File startDir)
    {
        return queue.remove(0)
    }

    public File askFile(File dir, String defaultFileName)
    {
        return queue.remove(0)
    }

    public Integer askOffset()
    {
        return queue.remove(0)
    }

    public TimeZone askTimeZone(TimeZone defaultZone)
    {
        return queue.remove(0)
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


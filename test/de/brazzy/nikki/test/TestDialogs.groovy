package de.brazzy.nikki.test
import de.brazzy.nikki.util.Dialogs
import javax.swing.SwingWorker

/**
 *
 * @author Brazil
 */
class TestDialogs extends Dialogs{
    def queue = []
    def worker
    def opened

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
        if(opened)
        {
            throw new IllegalStateException("Already present: "+opened)
        }
        opened = f
    }

    @Override
    public void registerWorker(SwingWorker worker)
    {
        if(this.worker)
        {
            this.worker.get()
        }
        this.worker = worker
    }

    public boolean isQueueEmpty()
    {
        return queue.isEmpty()
    }

    public void add(stuff)
    {
        queue.add(stuff)
    }

    public File getOpened()
    {
        def result = opened
        opened = null
        return result
    }
}


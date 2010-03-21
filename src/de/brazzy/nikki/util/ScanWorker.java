package de.brazzy.nikki.util;

import java.util.List;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Directory;
import org.joda.time.DateTimeZone;

public class ScanWorker extends SwingWorker<Void, Void>
{
    private Dialogs dialogs;
    private Directory dir;
    private TimezoneFinder finder;
    
    private DateTimeZone zone = null;
    private Object zoneLock = new Object();
    private Thread thread;
    
    public ScanWorker(Directory dir, Dialogs dialogs, TimezoneFinder finder)
    {
        super();
        this.dir = dir;
        this.dialogs = dialogs;
        this.finder = finder;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        thread = Thread.currentThread();
        ScanResult status = dir.scan(this, null, finder);
        if(status==ScanResult.TIMEZONE_MISSING)
        {            
            try
            {
                synchronized(zoneLock)
                {
                    publish();
                    while(zone==null)
                    {
                        zoneLock.wait();
                    }                
                }
                status = dir.scan(this, zone, finder);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return null;            
    }

    @Override
    protected void process(List<Void> chunks)
    {
        synchronized(zoneLock)
        {
            zone = dialogs.askTimeZone(DateTimeZone.getDefault());
            if(zone!=null)
            {
                zoneLock.notifyAll();
            }
            else
            {
                thread.interrupt();
            }
        }
    }   
}

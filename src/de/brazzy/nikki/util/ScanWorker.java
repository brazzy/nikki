package de.brazzy.nikki.util;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Directory;
import org.joda.time.DateTimeZone;

public class ScanWorker extends SwingWorker<Void, Void>
{
    private Directory dir;
    private DateTimeZone zone;
    private TimezoneFinder finder;
    
    public ScanWorker(Directory dir, DateTimeZone zone, TimezoneFinder finder)
    {
        super();
        this.dir = dir;
        this.zone = zone;
        this.finder = finder;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        dir.scan(this, zone, finder);
        return null;
    }   
}

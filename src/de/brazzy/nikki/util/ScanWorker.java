package de.brazzy.nikki.util;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Directory;
import org.joda.time.DateTimeZone;

public class ScanWorker extends SwingWorker<Void, Void>
{
    private Directory dir;
    private DateTimeZone zone;
    
    public ScanWorker(Directory dir, DateTimeZone zone)
    {
        super();
        this.dir = dir;
        this.zone = zone;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        dir.scan(this, zone);
        return null;
    }   
}

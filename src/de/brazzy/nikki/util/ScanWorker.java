package de.brazzy.nikki.util;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Directory;

public class ScanWorker extends SwingWorker<Void, Void>
{
    private Directory dir;
    
    public ScanWorker(Directory dir)
    {
        super();
        this.dir = dir;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        dir.scan(this);            
        return null;
    }   
}

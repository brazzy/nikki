package de.brazzy.nikki.util;

import java.util.zip.ZipOutputStream;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Day;

/**
 * Exports data to a KMZ file
 * 
 * @author Michael Borgwardt
 */
public class ExportWorker extends SwingWorker<Void, Void>
{
    private Day day;
    private ZipOutputStream out;
    
    public ExportWorker(Day day, ZipOutputStream out)
    {
        super();
        this.day = day;
        this.out = out;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        day.export(out, this);            
        return null;
    }   

}

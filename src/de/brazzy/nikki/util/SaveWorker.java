package de.brazzy.nikki.util;

import javax.swing.SwingWorker;
import de.brazzy.nikki.model.Directory;

/**
 *
 * @author Brazil
 */
public class SaveWorker extends SwingWorker<Void, Void>
{
    private Directory dir;

    public SaveWorker(Directory dir)
    {
        super();
        this.dir = dir;
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        try{
            dir.saveNew(this);
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

}
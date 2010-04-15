package de.brazzy.nikki.util;
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import javax.swing.SwingWorker;

import de.brazzy.nikki.Nikki;
import de.brazzy.nikki.model.Directory;

/**
 * Saves all image data to EXIF headers
 * before exiting the application
 *
 * @author Michael Borgwardt
 */
public class SaveExitWorker extends SwingWorker<Void, Void>
{
    private List<Directory> dirs;
    private UncaughtExceptionHandler handler;
    
    public SaveExitWorker(List<Directory> dirs, Dialogs dialogs)
    {
        super();
        this.dirs = dirs;
        this.handler = dialogs.getExceptionHandler();
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        Thread.currentThread().setUncaughtExceptionHandler(handler);
        for(Directory dir: dirs)
        {
            dir.save(this);            
        }
        try
        {
            System.exit(Nikki.EXIT_CODE_SAVED_MODIFICATIONS);
        }
        catch(SecurityException e)
        {
            // Happens only during tests
            e.printStackTrace();
        }
        return null;
    }

}

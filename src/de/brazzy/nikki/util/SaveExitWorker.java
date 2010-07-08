package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import javax.swing.SwingWorker;

import de.brazzy.nikki.Nikki;
import de.brazzy.nikki.model.Directory;

/**
 * Saves all image data to EXIF headers before exiting the application
 * 
 * @author Michael Borgwardt
 */
public class SaveExitWorker extends SwingWorker<Void, Void> {
    private List<Directory> dirs;
    private UncaughtExceptionHandler handler;

    public SaveExitWorker(List<Directory> dirs, Dialogs dialogs) {
        super();
        this.dirs = dirs;
        this.handler = dialogs.getExceptionHandler();
    }

    @Override
    protected Void doInBackground() throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(handler);
        for (Directory dir : dirs) {
            dir.save(this);
        }
        try {
            System.exit(Nikki.EXIT_CODE_SAVED_MODIFICATIONS);
        } catch (SecurityException e) {
            // Happens only during tests
            e.printStackTrace();
        }
        return null;
    }

}

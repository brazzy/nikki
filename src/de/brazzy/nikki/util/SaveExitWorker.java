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

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import de.brazzy.nikki.Nikki;
import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Directory;

/**
 * Saves all image data to EXIF headers before exiting the application
 * 
 * @author Michael Borgwardt
 */
public class SaveExitWorker extends NikkiWorker {
    private List<Directory> dirs;
    private Dialogs dialogs;

    public SaveExitWorker(List<Directory> dirs, Dialogs dialogs) {
        super(Texts.Dialogs.Save.PROGRESS_HEADER);
        this.dirs = dirs;
        this.dialogs = dialogs;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(
                dialogs.getExceptionHandler());
        boolean errorOccurred = false;
        for (Directory dir : dirs) {
            if (dir.save(this).size() > 0) {
                errorOccurred = true;
            }
        }
        if (errorOccurred) {
            ConfirmResult answer = dialogs.confirm(
                    Texts.Dialogs.Save.ERROR_SAVE_CLOSE,
                    JOptionPane.OK_CANCEL_OPTION);
            if (answer == ConfirmResult.CANCEL) {
                return null;
            }
        }

        try {
            System.exit(Nikki.EXIT_CODE_SAVED_MODIFICATIONS);
        } catch (SecurityException e) {
            // Happens only during tests
            Logger.getLogger(getClass()).error("Exit", e);
        }
        return null;
    }
}

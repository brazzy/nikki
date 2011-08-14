package de.brazzy.nikki;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import de.brazzy.nikki.model.Day;
import de.brazzy.nikki.util.NikkiWorker;
import de.brazzy.nikki.util.Texts;
import de.brazzy.nikki.view.Dialogs;

/**
 * Exports data to a KMZ file
 * 
 * @author Michael Borgwardt
 */
public class ExportWorker extends NikkiWorker {
    private Day day;
    private File file;
    private Dialogs dialogs;
    private Exception exception;

    public ExportWorker(Day day, File file, Dialogs dialogs) {
        super(Texts.Dialogs.Export.PROGRESS_HEADER);
        this.day = day;
        this.file = file;
        this.dialogs = dialogs;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            ZipOutputStream out = new ZipOutputStream(
                    new FileOutputStream(file));
            day.export(out, this);
        } catch (Exception e) {
            Logger.getLogger(getClass()).error(
                    "Error during export to " + file.getAbsolutePath(), e);
            this.exception = e;
        }
        return null;
    }

    @Override
    protected void done() {
        if (exception != null) {
            dialogs.error(Texts.Dialogs.Export.ERROR_PREFIX
                    + exception.getMessage());
        }
    }

}

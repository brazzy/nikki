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

import java.util.Map;

import de.brazzy.nikki.model.Directory;
import de.brazzy.nikki.util.NikkiWorker;
import de.brazzy.nikki.util.Texts;
import de.brazzy.nikki.view.Dialogs;

/**
 * Saves image data to EXIF headers
 * 
 * @author Michael Borgwardt
 */
public class SaveWorker extends NikkiWorker {
    private Directory dir;
    private Dialogs dialogs;
    private Map<String, Exception> exceptions;

    public SaveWorker(Directory dir, Dialogs dialogs) {
        super(Texts.Dialogs.Save.PROGRESS_HEADER);
        this.dir = dir;
        this.dialogs = dialogs;
    }

    @Override
    protected Void doInBackground() throws Exception {
        exceptions = dir.save(this);
        return null;
    }

    @Override
    protected void done() {
        for (Map.Entry<String, Exception> e : exceptions.entrySet()) {
            dialogs.error(Texts.Dialogs.Save.ERROR_PREFIX + e.getKey() + ": "
                    + e.getValue().getMessage());
        }
    }

}

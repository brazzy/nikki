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

import javax.swing.SwingWorker;
import de.brazzy.nikki.model.Directory;

/**
 * Saves image data to EXIF headers
 * 
 * @author Michael Borgwardt
 */
public class SaveWorker extends SwingWorker<Void, Void> {
    private Directory dir;

    public SaveWorker(Directory dir) {
        super();
        this.dir = dir;
    }

    @Override
    protected Void doInBackground() throws Exception {
        dir.save(this);
        return null;
    }

}

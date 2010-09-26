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

import java.util.zip.ZipOutputStream;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Day;

/**
 * Exports data to a KMZ file
 * 
 * @author Michael Borgwardt
 */
public class ExportWorker extends SwingWorker<Void, Void> {
    private Day day;
    private ZipOutputStream out;

    public ExportWorker(Day day, ZipOutputStream out) {
        super();
        this.day = day;
        this.out = out;
    }

    @Override
    protected Void doInBackground() throws Exception {
        day.export(out, this); // TODO: handle errors
        return null;
    }

}

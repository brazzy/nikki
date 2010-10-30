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

/**
 * Superclass for long-running background tasks
 * 
 * @author Michael Borgwardt
 */
public abstract class NikkiWorker extends SwingWorker<Void, Void> {
    public static final String LABEL = "label";

    private String header;

    /**
     * @param header
     *            Will be displayed in the title bar of the modal progress bar
     */
    protected NikkiWorker(String header) {
        this.header = header;
    }

    String getHeader() {
        return header;
    }

    /**
     * @param currentItem
     *            Will be displayed above the modal progress bar. Should
     *            represent the item currently being processed.
     */
    public void setLabelUpdate(String currentItem) {
        firePropertyChange(LABEL, null, currentItem);
    }
}

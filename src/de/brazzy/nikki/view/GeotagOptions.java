package de.brazzy.nikki.view;

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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.brazzy.nikki.util.Texts;

/**
 * UI for modal dialog that asks user for parameters used when geotagging
 * images.
 */
public class GeotagOptions extends JPanel {
    private static final int SECS_PER_DAY = 60 * 60 * 24;
    private JSpinner spinner = new JSpinner(new SpinnerNumberModel(0,
            -SECS_PER_DAY, SECS_PER_DAY, 10));

    public GeotagOptions() {
        add(new JLabel(Texts.Dialogs.GeotagOptions.OFFSET_LABEL));
        add(spinner);
    }

    /**
     * Offset (in seconds) to adjust camera time with
     */
    public int getOffset() {
        return ((Number) spinner.getValue()).intValue();
    }
}

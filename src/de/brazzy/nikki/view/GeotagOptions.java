package de.brazzy.nikki.view;
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.brazzy.nikki.Texts;

/**
 * UI for modal dialog that asks user for parameters used
 * when geotagging images.
 */
public class GeotagOptions extends JPanel
{
    private static final int SECS_PER_DAY = 60*60*24;
    private JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, -SECS_PER_DAY, SECS_PER_DAY, 10));

    public GeotagOptions()
    {
        add(new JLabel(Texts.Dialogs.GeotagOptions.OFFSET_LABEL));
        add(spinner);
    }

    /**
     * Offset (in seconds) to adjust camera time with
     */
    public int getOffset()
    {
        return ((Number)spinner.getValue()).intValue();
    }
}

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

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.joda.time.DateTimeZone;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Directory;

/**
 * UI for modal dialog that asks user for parameters used when looking for new
 * files in a {@link Directory}.
 */
public class ScanOptions extends JPanel {
    private JComboBox combobox = new JComboBox();

    /**
     * @param selected
     *            preselected time zone
     */
    public ScanOptions(DateTimeZone selected) {
        @SuppressWarnings("unchecked")
        String[] zones = (String[]) DateTimeZone.getAvailableIDs().toArray(
                new String[0]);
        Arrays.sort(zones);
        combobox.setModel(new DefaultComboBoxModel(zones));
        if (selected != null) {
            combobox.setSelectedItem(selected.getID());
        } else {
            combobox.setSelectedItem(null);
        }
        add(new JLabel(Texts.Dialogs.ScanOptions.TIMEZONE_LABEL));
        add(combobox);
    }

    /**
     * @return time zone selected by the user
     */
    public DateTimeZone getTimezone() {
        return combobox.getSelectedItem() == null ? null : DateTimeZone
                .forID((String) combobox.getSelectedItem());
    }

}

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

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;

/**
 * UI for modal dialog that asks user for parameters used
 * when looking for new files in a {@link Directory}.
 */
public class ScanOptions extends JPanel
{
    private JComboBox combobox = new JComboBox();

    /**
     * @param selected preselected time zone
     */
    public ScanOptions(DateTimeZone selected)
    {
        @SuppressWarnings("unchecked")
        String[] zones = (String[]) DateTimeZone.getAvailableIDs().toArray(new String[0]);
        Arrays.sort(zones);
        combobox.setModel(new DefaultComboBoxModel(zones));
        if(selected != null)
        {
            combobox.setSelectedItem(selected.getID());
        }
        else
        {
            combobox.setSelectedItem(null);
        }
        add(new JLabel("Camera time zone"));
        add(combobox);
    }

    /**
     * @return time zone selected by the user
     */
    public DateTimeZone getTimezone()
    {
        return combobox.getSelectedItem() == null ? null :
            DateTimeZone.forID((String) combobox.getSelectedItem());
    }

}

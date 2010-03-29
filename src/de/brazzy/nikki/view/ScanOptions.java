package de.brazzy.nikki.view;

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

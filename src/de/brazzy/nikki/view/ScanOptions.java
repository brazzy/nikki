package de.brazzy.nikki.view;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.joda.time.DateTimeZone;

public class ScanOptions extends JPanel
{
    private JComboBox combobox = new JComboBox();

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

    public DateTimeZone getTimezone()
    {
        return combobox.getSelectedItem() == null ? null :
            DateTimeZone.forID((String) combobox.getSelectedItem());
    }

}

package de.brazzy.nikki.view;

import java.util.Arrays;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ScanOptions extends JPanel
{
    private JComboBox combobox = new JComboBox();

    public ScanOptions(TimeZone selected)
    {
        String[] zones = TimeZone.getAvailableIDs();
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

    public TimeZone getTimezone()
    {
        return combobox.getSelectedItem() == null ? null :
            TimeZone.getTimeZone((String) combobox.getSelectedItem());
    }

}

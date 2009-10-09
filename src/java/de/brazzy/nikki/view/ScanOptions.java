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
    private JComboBox timezone = new JComboBox();

    public ScanOptions(TimeZone selected)
    {
        String[] zones = TimeZone.getAvailableIDs();
        Arrays.sort(zones);
        timezone.setModel(new DefaultComboBoxModel(zones));
        if(selected != null)
        {
            timezone.setSelectedItem(selected.getID());            
        }
        add(new JLabel("Camera time zone"));
        add(timezone);
    }
    
    public static void main(String[] args)
    {
        ScanOptions o = new ScanOptions(TimeZone.getDefault());
        int option = JOptionPane.showConfirmDialog(null, o, "Scan options", JOptionPane.OK_CANCEL_OPTION);
        System.out.println(o.timezone.getSelectedItem());
        System.out.println(option==JOptionPane.OK_OPTION ? "OK" : "Cancel");
    }

    public TimeZone getTimezone()
    {
        return TimeZone.getTimeZone((String) timezone.getSelectedItem());
    }

}

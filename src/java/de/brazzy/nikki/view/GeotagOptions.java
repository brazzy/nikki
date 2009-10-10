package de.brazzy.nikki.view;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class GeotagOptions extends JPanel
{
    private static final int SECS_PER_DAY = 60*60*24;
    private JSpinner offset = new JSpinner(new SpinnerNumberModel(0, -SECS_PER_DAY, SECS_PER_DAY, 10));

    public GeotagOptions()
    {
        add(new JLabel("Time offset (sec)"));
        add(offset);
    }
    
    public static void main(String[] args)
    {
        GeotagOptions o = new GeotagOptions();
        int option = JOptionPane.showConfirmDialog(null, o, "Geotagging options", JOptionPane.OK_CANCEL_OPTION);
        System.out.println(o.offset.getValue());
        System.out.println(option==JOptionPane.OK_OPTION ? "OK" : "Cancel");
    }
    
    public int getOffset()
    {
        return ((Number)offset.getValue()).intValue();
    }
}

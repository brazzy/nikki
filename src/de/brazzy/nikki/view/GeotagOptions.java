package de.brazzy.nikki.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class GeotagOptions extends JPanel
{
    private static final int SECS_PER_DAY = 60*60*24;
    private JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, -SECS_PER_DAY, SECS_PER_DAY, 10));

    public GeotagOptions()
    {
        add(new JLabel("Time offset (sec)"));
        add(spinner);
    }

    public int getOffset()
    {
        return ((Number)spinner.getValue()).intValue();
    }
}

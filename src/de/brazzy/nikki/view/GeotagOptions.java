package de.brazzy.nikki.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.brazzy.nikki.model.Directory;

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
        add(new JLabel("Time offset (sec)"));
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

package de.brazzy.nikki;

import de.brazzy.nikki.model.NikkiModel;
import de.brazzy.nikki.util.Dialogs;
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.view.NikkiFrame;

/**
 * Main class for starting the app.
 * 
 * @author Michael Borgwardt
 */
public class Launcher
{
    public static void main(String[] args)
    {
        TimezoneFinder finder = new TimezoneFinder(TimezoneFinder.class.getResourceAsStream("timezones.dat"));
        Dialogs d = new Dialogs();
        Nikki n = new Nikki();
        n.build(NikkiModel.class, d, finder);
        d.setParentComponent(((NikkiFrame)n.getView()).getFrame());
        n.start();
    }
}

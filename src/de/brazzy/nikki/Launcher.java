package de.brazzy.nikki;

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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import de.brazzy.nikki.model.NikkiModel;
import de.brazzy.nikki.util.ParserFactory;
import de.brazzy.nikki.util.TimezoneFinder;
import de.brazzy.nikki.view.Dialogs;
import de.brazzy.nikki.view.NikkiFrame;

/**
 * Main class for starting the app.
 * 
 * @author Michael Borgwardt
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        Logger.getLogger("").setLevel(Level.WARNING);
        TimezoneFinder finder = new TimezoneFinder(
                TimezoneFinder.class.getResourceAsStream("timezones.dat"));
        Dialogs d = new Dialogs();
        Nikki n = new Nikki();
        ParserFactory pf = new ParserFactory();
        n.build(NikkiModel.class, d, finder, pf);
        d.setView((NikkiFrame) n.getView());
        n.start();
    }
}

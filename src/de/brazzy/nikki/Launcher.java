package de.brazzy.nikki;

import de.brazzy.nikki.util.UserDialogs;
import de.brazzy.nikki.view.NikkiFrame;

/**
 * For starting the app and creating a runnable JAR in eclipse
 * 
 * @author Michael Borgwardt
 */
public class Launcher
{
    public static void main(String[] args)
    {
        UserDialogs d= new UserDialogs();
        Nikki n = new Nikki();
        n.build(true, d);
        d.setParentComponent(((NikkiFrame)n.getView()).getFrame());
        n.start();
    }
}

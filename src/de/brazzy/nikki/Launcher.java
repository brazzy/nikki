package de.brazzy.nikki;


/**
 * For starting the app and creating a runnable JAR in eclipse
 * 
 * @author Michael Borgwardt
 */
public class Launcher
{
    public static void main(String[] args)
    {
        Nikki n = new Nikki();
        n.build(true);
        n.start();
    }
}

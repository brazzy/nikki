package de.brazzy.nikki.util

import java.awt.Desktop
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import javax.swing.SwingWorker

import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;

/**
 * Encapsulates user interaction via modal dialogs for testability
 */
class Dialogs
{
    def parentComponent

    /**
     * Opens a JFileChooser to return a directory
     * 
     * @param startDir default starting dir
     */
    public File askDirectory(File startDir)
    {
        def fc = new JFileChooser(startDir)
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        return fc.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION ?
            fc.getSelectedFile() :
            null;
    }

    /**
     * Opens a JFileChooser to return a file
     * 
     * @param startDir default starting dir
     * @param defaultFileName
     */
    public File askFile(File dir, String defaultFileName)
    {
        def fc = new JFileChooser(dir);
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.selectedFile = new File(dir, defaultFileName);
        return (fc.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) ?
            fc.getSelectedFile() :
            null;
    }

    /**
     * Asks user for an offset to adjust for wrong camera time before geotagging
     */
    public ReadablePeriod askOffset()
    {
        def opt = new GeotagOptions()
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, "Geotagging options", JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? Seconds.seconds(opt.offset) : null
    }

    /**
     * Asks user for time zone used to set camera clock
     * 
     * @param defaultZone
     */
    public DateTimeZone askTimeZone(DateTimeZone defaultZone)
    {
        ScanOptions opt = new ScanOptions(defaultZone)
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, "Scan options", JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? opt.timezone : null
    }

    /**
     * Opens fial via OS
     */
    public void open(File f)
    {
        Desktop.getDesktop().open(f);
    }

    /**
     * Allows waiting for background actions to be completed during tests
     */
    public void registerWorker(SwingWorker worker)
    {

    }
}


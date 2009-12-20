package de.brazzy.nikki.util

import java.awt.Desktop
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import javax.swing.SwingWorker

class Dialogs
{
    def parentComponent

    public File askDirectory(File startDir)
    {
        def fc = new JFileChooser(startDir)
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        return fc.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION ?
            fc.getSelectedFile() :
            null;
    }

    public File askFile(File dir, String defaultFileName)
    {
        def fc = new JFileChooser(dir);
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.selectedFile = new File(dir, defaultFileName);
        return (fc.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) ?
            fc.getSelectedFile() :
            null;
    }

    public Integer askOffset()
    {
        def opt = new GeotagOptions()
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, "Geotagging options", JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? opt.offset : null
    }

    public TimeZone askTimeZone(TimeZone defaultZone)
    {
        ScanOptions opt = new ScanOptions(defaultZone)
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, "Scan options", JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? opt.timezone : null
    }

    public void open(File f)
    {
        Desktop.getDesktop().open(f);
    }

    public void registerWorker(SwingWorker worker)
    {

    }
}


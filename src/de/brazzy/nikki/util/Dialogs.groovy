package de.brazzy.nikki.util
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.awt.Desktop
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFileChooser
import javax.swing.JOptionPane

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.view.AboutBox;
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import javax.swing.SwingWorker

import org.joda.time.DateTimeZone
import org.joda.time.ReadablePeriod
import org.joda.time.Seconds

/**
 * Encapsulates user interaction via modal dialogs for testability
 */
class Dialogs
{
    def parentComponent

    public void showAboutBox()
    {
        def box = new AboutBox()
        JOptionPane.showOptionDialog(parentComponent, box, Texts.Dialogs.About.TITLE, 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null)
    }
    
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
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, 
                Texts.Dialogs.GeotagOptions.TITLE, JOptionPane.OK_CANCEL_OPTION)
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
        int pressed = JOptionPane.showConfirmDialog(parentComponent, opt, 
                Texts.Dialogs.ScanOptions.TITLE, JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? opt.timezone : null
    }
    
    /**
     * Ask user for confirmation for an action
     * 
     * @param message Text shown to user
     * @param optionType which buttons to show (see constants in JOptionPane)
     */
    public ConfirmResult confirm(String message, int optionType)
    {
        int pressed = JOptionPane.showConfirmDialog(parentComponent, message, 
                Texts.Dialogs.CONFIRM_TITLE, optionType)
        return (pressed == JOptionPane.YES_OPTION ? ConfirmResult.YES :
                pressed == JOptionPane.NO_OPTION ? ConfirmResult.NO :
                pressed == JOptionPane.CANCEL_OPTION ? ConfirmResult.CANCEL : null);
    }

    /**
     * Show error message to user
     * 
     * @param message Text shown to user
     */
    public void error(String message)
    {
        JOptionPane.showMessageDialog(parentComponent, message, 
                Texts.Dialogs.ERROR_TITLE, JOptionPane.ERROR_MESSAGE)
    }

    /**
     * Opens file via OS
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
    
    /**
     * used when testing for system exit
     */
    public UncaughtExceptionHandler getExceptionHandler()
    {
        return null;
    }
}


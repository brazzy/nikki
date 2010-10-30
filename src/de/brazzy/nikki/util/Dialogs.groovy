package de.brazzy.nikki.util
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

import groovy.swing.SwingBuilder;

import java.awt.BorderLayout;
import java.awt.Desktop
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.ProgressMonitor;
import javax.swing.border.EmptyBorder;
import javax.swing.WindowConstants;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.view.AboutBox;
import de.brazzy.nikki.view.NikkiFrame;
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import javax.swing.SwingWorker

import org.joda.time.DateTimeZone
import org.joda.time.ReadablePeriod
import org.joda.time.Seconds

/**
 * Encapsulates user interaction via modal dialogs for testability
 */
class Dialogs {
    NikkiFrame view
    
    public void showAboutBox() {
        def box = new AboutBox()
        JOptionPane.showOptionDialog(view.frame, box, Texts.Dialogs.About.TITLE, 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null)
    }
    
    /**
     * Opens a JFileChooser to return a directory
     * 
     * @param startDir default starting dir
     */
    public File askDirectory(File startDir) {
        def fc = new JFileChooser(startDir)
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        return fc.showOpenDialog(view.frame) == JFileChooser.APPROVE_OPTION ?
        fc.getSelectedFile() :
        null;
    }
    
    /**
     * Opens a JFileChooser to return a file
     * 
     * @param startDir default starting dir
     * @param defaultFileName
     */
    public File askFile(File dir, String defaultFileName) {
        def fc = new JFileChooser(dir);
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.selectedFile = new File(dir, defaultFileName);
        return (fc.showSaveDialog(view.frame) == JFileChooser.APPROVE_OPTION) ?
        fc.getSelectedFile() :
        null;
    }
    
    /**
     * Asks user for an offset to adjust for wrong camera time before geotagging
     */
    public ReadablePeriod askOffset() {
        def opt = new GeotagOptions()
        int pressed = JOptionPane.showConfirmDialog(view.frame, opt, 
                Texts.Dialogs.GeotagOptions.TITLE, JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? Seconds.seconds(opt.offset) : null
    }
    
    /**
     * Asks user for time zone used to set camera clock
     * 
     * @param defaultZone
     */
    public DateTimeZone askTimeZone(DateTimeZone defaultZone) {
        ScanOptions opt = new ScanOptions(defaultZone)
        int pressed = JOptionPane.showConfirmDialog(view.frame, opt, 
                Texts.Dialogs.ScanOptions.TITLE, JOptionPane.OK_CANCEL_OPTION)
        return pressed == JOptionPane.OK_OPTION ? opt.timezone : null
    }
    
    /**
     * Ask user for confirmation for an action
     * 
     * @param message Text shown to user
     * @param optionType which buttons to show (see constants in JOptionPane)
     */
    public ConfirmResult confirm(String message, int optionType) {
        int pressed = JOptionPane.showConfirmDialog(view.frame, message, 
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
    public void error(String message) {
        JOptionPane.showMessageDialog(view.frame, message, 
        Texts.Dialogs.ERROR_TITLE, JOptionPane.ERROR_MESSAGE)
    }
    
    /**
     * Opens file via OS
     */
    public void open(File f) {
        Desktop.getDesktop().open(f);
    }
    
    /**
     * Allows displaying of progress bar and waiting for background 
     * actions to be completed during tests
     */
    public void registerWorker(SwingWorker worker) {
        def swing = new SwingBuilder()
        def progress
        def monitor
        
        swing.edt{
            monitor = dialog(owner: view.frame, title: "message", modal:true, 
            defaultCloseOperation:WindowConstants.DO_NOTHING_ON_CLOSE){
                panel(border: new EmptyBorder(5,5,5,5)){
                    borderLayout()
                    label(text: "text", constraints: BorderLayout.NORTH)
                    progress = progressBar(minimum:0, maximum: 100, constraints: BorderLayout.CENTER, preferredSize:new Dimension(200,20))                    
                }
            }
        }
        monitor.locationRelativeTo = view.frame
        
        def listener = { evt ->
            switch(evt.propertyName){
                case "progress":
                progress.value = evt.newValue.intValue()
                break
                case "state":
                if(evt.newValue == SwingWorker.StateValue.DONE){
                    monitor.visible = false
                    monitor.dispose()
                }
                break
            }
            
            view.dirList.repaint()
            view.dayList.repaint()
            if(view.dayList.selectedValue){
                view.dayList.selectedValue.fireTableDataChanged()           
            }
        } as PropertyChangeListener
        worker.addPropertyChangeListener(listener)
        worker.execute()
        
        monitor.pack()
        monitor.show()
    }
    
    /**
     * used when testing for system exit
     */
    public UncaughtExceptionHandler getExceptionHandler() {
        return null;
    }
}


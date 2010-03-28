package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.border.EmptyBorder
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import javax.swing.JFileChooser
import javax.swing.event.ListSelectionListener
import javax.swing.DefaultListModel
import javax.swing.table.DefaultTableModel
import java.beans.PropertyChangeListener
import de.brazzy.nikki.util.ScanWorker
import de.brazzy.nikki.model.Day
import javax.swing.UIManager
import java.util.zip.ZipOutputStream
import de.brazzy.nikki.util.ExportWorker
import javax.swing.JOptionPane

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import de.brazzy.nikki.util.Dialogs
import de.brazzy.nikki.util.SaveWorker
import de.brazzy.nikki.util.TimezoneFinder;

/**
 * Controller that builds the view and the model, connects them
 * and wires up the GUI logic (listeners, etc)
 * 
 * @author Michael Borgwardt
 */
public class Nikki{
    public static final String EXPORT_FILE_NAME="diary_"

    /** View instance */
    def view

    /** Model instance */
    def model
    
    /** Encapsulates user interaction for testing */
    def dialogs
    
    def timezoneFinder

    private progressListener = { evt ->
            if("progress".equals(evt.propertyName)) {
                view.progressBar.value = evt.newValue.intValue();
            }
            view.dirList.repaint()
            view.dayList.repaint()
        } as PropertyChangeListener
    
    private selectDirectoryAction = { it ->
            def sel = view.dirList.selectedValue
            if(sel)
            {
                view.dayList.model = sel                
            }
            else
            {
                view.dayList.model = new DefaultListModel()
            }
            view.deleteButton.enabled = (sel != null)
            view.scanButton.enabled = (sel != null)
            view.saveButton.enabled = (sel != null)
        } as ListSelectionListener

    private selectDayAction = { it ->
            def sel = view.dayList.selectedValue
            if(sel)
            {
                view.imageTable.editingStopped()
                view.imageTable.model = sel                
            }
            else
            {
                view.imageTable.model = new DefaultTableModel()
            }
            view.exportButton.enabled = (sel != null)
            view.tagButton.enabled = (sel != null)
        } as ListSelectionListener 
        
    private addAction = {
            def selectedFile = dialogs.askDirectory(model.selectionDir);
            if(selectedFile){
                model.selectionDir = selectedFile.getParentFile()
                model.add(new Directory(path:selectedFile))
            }
        }
    
    private deleteAction = {
            def dir = view.dirList.selectedValue
            model.remove(dir)
        }
    
    private scanAction = {
            ScanWorker worker = new ScanWorker(view.dirList.selectedValue, dialogs, timezoneFinder)
            worker.addPropertyChangeListener(progressListener)
            worker.execute()
            dialogs.registerWorker(worker)                
        }
    
    private saveAction = {
            view.imageTable.editorComponent?.getValue()
            SaveWorker worker = new SaveWorker(view.dirList.selectedValue)
            worker.addPropertyChangeListener(progressListener)
            worker.execute()
            dialogs.registerWorker(worker)
        }
    
    private geotagAction = {
            def offset = dialogs.askOffset();
            if(offset != null)
            {
                view.dayList.selectedValue.geotag(offset)
                view.imageTable.repaint()
            }
        }
    
    private exportAction = {
            view.imageTable.editorComponent?.getValue()
            def day = view.dayList.selectedValue
            def selectedFile = dialogs.askFile(model.exportDir, EXPORT_FILE_NAME + day.date +".kmz");

            if(selectedFile){
                model.exportDir = selectedFile.getParentFile()
                ExportWorker worker = new ExportWorker(
                    day, new ZipOutputStream(new FileOutputStream(selectedFile)))
                worker.addPropertyChangeListener(progressListener)
                worker.execute()
                dialogs.registerWorker(worker)
            }
        }
    
    /**
     * Builds the model and the view and connects everything
     * 
     * @param prefsClass used as key in the Preferences API
     */
    public void build(Class prefsClass, Dialogs dialogs, TimezoneFinder finder){
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        this.view = NikkiFrame.create(dialogs)
        this.dialogs = dialogs
        this.model = new NikkiModel(prefsClass)
        this.timezoneFinder = finder
        view.dirList.model = model        
        
        view.dirList.addListSelectionListener(selectDirectoryAction)        
        view.addButton.actionPerformed = addAction            
        view.deleteButton.actionPerformed = deleteAction
        view.scanButton.actionPerformed = scanAction
        view.saveButton.actionPerformed = saveAction
        view.dayList.addListSelectionListener(selectDayAction)
        view.tagButton.actionPerformed = geotagAction        
        view.exportButton.actionPerformed = exportAction
    }

    /**
     * Shows the GUI
     */
    public void start(){
        view.frame.pack()
        view.frame.show()
    }
}

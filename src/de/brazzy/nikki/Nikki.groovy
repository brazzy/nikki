package de.brazzy.nikki
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

import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import javax.swing.event.ListSelectionListener
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel
import de.brazzy.nikki.util.ConfirmResult
import javax.swing.JOptionPane
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyChangeListener
import de.brazzy.nikki.util.ScanWorker
import java.util.zip.ZipOutputStream
import de.brazzy.nikki.util.ExportWorker


import de.brazzy.nikki.util.Dialogs
import de.brazzy.nikki.util.SaveExitWorker;
import de.brazzy.nikki.util.SaveWorker
import de.brazzy.nikki.util.TimezoneFinder

/**
 * Controller that builds the view and the model, connects them
 * and wires up the GUI logic (listeners, etc)
 * 
 * @author Michael Borgwardt
 */
public class Nikki{
    public static final String EXPORT_FILE_NAME="diary_"
        public static final int EXIT_CODE_NO_MODIFICATIONS = 0
        public static final int EXIT_CODE_UNSAVED_MODIFICATIONS = 1
        public static final int EXIT_CODE_SAVED_MODIFICATIONS = 2

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
    
    private aboutAction = {
        dialogs.showAboutBox()
    }
    
    private closeListener = new WindowAdapter(){
        public void windowClosing(WindowEvent e) {
            view.imageTable.editorComponent?.getValue()
            def modifiedDirs = model.dataList.findAll{ it.modified }
            if(modifiedDirs)
            {
                switch(dialogs.confirm("There are unsaved changes. Save changed data before exiting?", JOptionPane.YES_NO_CANCEL_OPTION))
                {
                case ConfirmResult.YES:
                    SaveExitWorker worker = new SaveExitWorker(modifiedDirs, dialogs)
                    worker.addPropertyChangeListener(progressListener)
                    worker.execute()
                    dialogs.registerWorker(worker);
                    break;
                case ConfirmResult.NO:
                    System.exit(EXIT_CODE_UNSAVED_MODIFICATIONS)
                case ConfirmResult.CANCEL:
                default:
                    break;
                }
            }
            else
            {
                System.exit(EXIT_CODE_NO_MODIFICATIONS)
            }
        }
    }
    
    /**
     * Builds the model and the view and connects everything
     * 
     * @param prefsClass used as key in the Preferences API
     */
    public void build(Class prefsClass, Dialogs dialogs, TimezoneFinder finder){
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
        view.helpButton.actionPerformed = aboutAction
        view.frame.addWindowListener(closeListener)
    }

    /**
     * Shows the GUI
     */
    public void start(){
        view.frame.pack()
        view.frame.show()
    }
}

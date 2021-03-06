package de.brazzy.nikki
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

import de.brazzy.nikki.view.Dialogs;
import de.brazzy.nikki.view.ImageRenderer;
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import javax.swing.event.ListSelectionListener
import javax.swing.DefaultListModel
import javax.swing.table.DefaultTableModel
import de.brazzy.nikki.util.ConfirmResult
import javax.swing.JOptionPane

import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.beans.PropertyChangeListener
import java.util.zip.ZipOutputStream


import de.brazzy.nikki.util.Texts;
import de.brazzy.nikki.util.TimezoneFinder
import de.brazzy.nikki.util.ParserFactory;

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

    /** finds parsers for GPS log files */
    def parserFactory

    /** assigns timezones to waypoints*/
    def timezoneFinder

    private copyListener = {
        view.dayList.repaint()
    } as ActionListener

    private selectDirectoryAction = {
        it ->
        def sel = view.dirList.selectedValue
        if(sel) {
            view.dayList.model = sel
        }
        else {
            view.dayList.model = new DefaultListModel()
        }
        view.deleteButton.enabled = (sel != null)
        view.scanButton.enabled = (sel != null)
        view.saveButton.enabled = (sel != null)
        if(sel){
            if(sel.size > 0){
                view.dayList.selectedIndex = 0
            }
            scanAction()
        }
    } as ListSelectionListener

    private selectDayAction = {
        it ->
        def sel = view.dayList.selectedValue
        if(sel) {
            view.imageTable.editingStopped()
            view.imageTable.model = sel
        }
        else {
            view.imageTable.model = new DefaultTableModel()
        }
        view.imageSortOrder.selectedItem = sel?.imageSortOrder
        view.imageSortOrder.enabled = sel?.date != null
        view.exportButton.enabled = (sel != null)
        view.exportAllButton.enabled = (sel != null)
        view.exportNoneButton.enabled = (sel != null)
    } as ListSelectionListener

    private sortOrderAction = {
        it ->
        def sel = view.dayList.selectedValue
        if(sel){
            sel.imageSortOrder = it.source.selectedItem
        }
    } as ActionListener

    private addAction = {
        def selectedFile = dialogs.askDirectory(model.selectionDir);
        if(selectedFile){
            model.selectionDir = selectedFile.getParentFile()
            def dir = new Directory(path:selectedFile)
            model.add(dir)
            view.dirList.setSelectedValue(dir, true)
        }
    }

    private deleteAction = {
        def dir = view.dirList.selectedValue
        model.remove(dir)
    }

    private scanAction = {
        def scanner = new DirectoryScanner(finder:timezoneFinder, parserFactory:parserFactory)
        def prevSelected = view.dayList.selectedValue
        def callback = {
            if(prevSelected){
                view.dayList.setSelectedValue(prevSelected, true)
            } else if(view.dirList.selectedValue.size > 0){
                view.dayList.selectedIndex = 0
            }
        }

        ScanWorker worker = new ScanWorker(view.dirList.selectedValue, dialogs, scanner, callback)
        dialogs.registerWorker(worker)
    }

    private saveAction = {
        view.imageTable.editorComponent?.getValue()
        SaveWorker worker = new SaveWorker(view.dirList.selectedValue, dialogs)
        dialogs.registerWorker(worker)
    }

    private exportAction = {
        view.imageTable.editorComponent?.getValue()
        def day = view.dayList.selectedValue
        if(day.waypoints.empty) {
            dialogs.error(Texts.Dialogs.Export.NODATA_MESSAGE)
            return
        }
        def exportFlags = day.images.asList().export
        if(!exportFlags.contains(true)) {
            ConfirmResult c = dialogs.confirm(Texts.Dialogs.Export.NOIMAGE_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
            if(c == ConfirmResult.CANCEL) {
                return
            }
        }

        def selectedFile = dialogs.askFile(model.exportDir, EXPORT_FILE_NAME + day.date +".kmz");

        if(selectedFile){
            model.exportDir = selectedFile.getParentFile()
            ExportWorker worker = new ExportWorker(day, selectedFile, dialogs)
            dialogs.registerWorker(worker)
        }
    }

    private exportAllAction = {
        def current = view.imageTable.editorComponent?.getValue()
        def day = view.dayList.selectedValue
        for(Image img in day.images){
            if(img.waypoint){
                img.export = true
            }
        }
        view.imageTable.editorComponent?.setValue(current)
        view.imageTable.repaint()
    }

    private exportNoneAction = {
        def current = view.imageTable.editorComponent?.getValue()
        def day = view.dayList.selectedValue
        for(Image img in day.images){
            img.export = false
        }
        view.imageTable.editorComponent?.setValue(current)
        view.imageTable.repaint()
    }

    private aboutAction = { dialogs.showAboutBox() }

    private closeListener = [
        windowClosing: {
            view.imageTable.editorComponent?.getValue()
            def modifiedDirs = model.dataList.findAll{ it.modified }
            if(modifiedDirs) {
                switch(dialogs.confirm(Texts.Dialogs.UNSAVED_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION)) {
                    case ConfirmResult.YES:
                        SaveExitWorker worker = new SaveExitWorker(modifiedDirs, dialogs)
                        dialogs.registerWorker(worker);
                        break;
                    case ConfirmResult.NO:
                        System.exit(EXIT_CODE_UNSAVED_MODIFICATIONS)
                    case ConfirmResult.CANCEL:
                    default:
                        break;
                }
            }
            else {
                System.exit(EXIT_CODE_NO_MODIFICATIONS)
            }
        }
    ] as WindowAdapter

    /**
     * Builds the model and the view and connects everything
     * 
     * @param prefsClass used as key in the Preferences API
     */
    public void build(Class prefsClass, Dialogs dialogs,
    TimezoneFinder finder, ParserFactory parserFactory){
        this.view = NikkiFrame.create()
        this.dialogs = dialogs
        this.model = new NikkiModel(prefsClass)
        this.timezoneFinder = finder
        this.parserFactory = parserFactory
        view.dirList.model = model

        view.dirList.addListSelectionListener(selectDirectoryAction)
        view.addButton.actionPerformed = addAction
        view.deleteButton.actionPerformed = deleteAction
        view.scanButton.actionPerformed = scanAction
        view.saveButton.actionPerformed = saveAction
        view.dayList.addListSelectionListener(selectDayAction)
        view.imageSortOrder.addActionListener(sortOrderAction)
        view.exportButton.actionPerformed = exportAction
        view.exportAllButton.actionPerformed = exportAllAction
        view.exportNoneButton.actionPerformed = exportNoneAction
        view.helpButton.actionPerformed = aboutAction
        view.frame.addWindowListener(closeListener)

        def clipboard = new Image[1];
        view.imageTable.setDefaultRenderer(Object.class, new ImageRenderer(dialogs, clipboard, copyListener))
        view.imageTable.setDefaultEditor(Object.class, new ImageRenderer(dialogs, clipboard, copyListener))
    }

    /**
     * Shows the GUI
     */
    public void start(){
        view.frame.pack()
        view.frame.show()
    }
}

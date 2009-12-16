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
import java.text.SimpleDateFormat
import javax.swing.JOptionPane
import de.brazzy.nikki.util.RelativeDateFormat
import de.brazzy.nikki.view.ScanOptions
import de.brazzy.nikki.view.GeotagOptions
import de.brazzy.nikki.util.Dialogs

/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static final String EXPORT_FILE_NAME="diary_"

    def view;
    def model;
    def dialogs;

    public void build(boolean usePreferences, Dialogs dialogs){
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        view = NikkiFrame.create(dialogs)
        this.dialogs = dialogs
        model = new NikkiModel(usePreferences)
        view.dirList.model = model        
        def selListener = { it ->
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
        view.dirList.addListSelectionListener(selListener)
        
        view.addButton.actionPerformed={
            def selectedFile = dialogs.askDirectory(model.selectionDir);
            if(selectedFile){
                model.selectionDir = selectedFile.getParentFile()
                model.add(new Directory(path:selectedFile))
            }
        }
        view.deleteButton.actionPerformed={
            def dir = view.dirList.selectedValue
            model.remove(dir)
        }

        def progressListener = { evt ->
            if("progress".equals(evt.propertyName)) {
                view.progressBar.value = evt.newValue.intValue();
            }
        } as PropertyChangeListener
        
        view.scanButton.actionPerformed={
            if(!view.dirList.selectedValue.hasPersistent())
            {
                def zone = dialogs.askTimeZone(view.dirList.selectedValue.zone)
                if(zone)
                {
                    view.dirList.selectedValue.zone = zone
                }
                else
                {
                    return
                }
            }
            ScanWorker worker = new ScanWorker(view.dirList.selectedValue)
            worker.addPropertyChangeListener(progressListener)
            worker.execute()
        }
        view.saveButton.actionPerformed={            
            view.dirList.selectedValue.save()
        }
        
        selListener = { it ->
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
        view.dayList.addListSelectionListener(selListener)

        view.tagButton.actionPerformed={
            def offset = dialogs.askOffset();
            if(offset != null)
            {
                view.dayList.selectedValue.geotag(offset)
            }
        }
        view.exportButton.actionPerformed={
            def day = view.dayList.selectedValue
            def format = new RelativeDateFormat(day.directory.zone);
            def selectedFile = dialogs.askFile(model.exportDir, EXPORT_FILE_NAME + format.format(day.date) +".kmz");

            if(selectedFile){
                model.exportDir = selectedFile.getParentFile()
                ExportWorker worker = new ExportWorker(
                    day, new ZipOutputStream(new FileOutputStream(selectedFile)))
                worker.addPropertyChangeListener(progressListener)
                worker.execute()
            }
        }
    }

    public void start(){
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        view.frame.pack()
        view.frame.show()
    }


}

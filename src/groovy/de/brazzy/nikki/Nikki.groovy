package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIconimport javax.swing.border.EmptyBorder
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import javax.swing.JFileChooserimport javax.swing.event.ListSelectionListenerimport javax.swing.DefaultListModelimport javax.swing.table.DefaultTableModelimport java.beans.PropertyChangeListenerimport de.brazzy.nikki.util.ScanWorkerimport de.brazzy.nikki.model.Day
/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static final String EXPORT_FILE_NAME="diary_"
    
    public static void start(){
        def view = NikkiFrame.create()
        view.frame.pack()
        view.frame.show()
        
        def model = new NikkiModel()
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
        } as ListSelectionListener
        view.dirList.addListSelectionListener(selListener)
        
        view.addButton.actionPerformed={
            def fc = new JFileChooser(model.selectionDir)
            fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            if(fc.showOpenDialog(view.frame) == JFileChooser.APPROVE_OPTION){
                model.selectionDir = fc.getSelectedFile().getParentFile()
                model.add(new Directory(path:fc.getSelectedFile()))
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
            ScanWorker worker = new ScanWorker(view.dirList.selectedValue)
            worker.addPropertyChangeListener(progressListener)
            worker.execute()
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
            view.dayList.selectedValue.geotag()
        }
        view.exportButton.actionPerformed={
            def day = view.dayList.selectedValue
            def fc = new JFileChooser(model.exportDir);
            fc.fileSelectionMode = JFileChooser.FILES_ONLY
            fc.selectedFile = new File(model.exportDir, EXPORT_FILE_NAME + day.date.dateString +".kmz");
            if(fc.showSaveDialog(view.frame) == JFileChooser.APPROVE_OPTION){
                model.exportDir = fc.getSelectedFile().getParentFile()
                day.export(new FileOutputStream(fc.getSelectedFile()))
            }
        }
    }    
}

package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIconimport javax.swing.border.EmptyBorder
import de.brazzy.nikki.view.NikkiFrame
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.model.Directory
import javax.swing.JFileChooserimport javax.swing.event.ListSelectionListenerimport javax.swing.DefaultListModel
/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static void main(def args){
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
            view.scanButton.enabled = (sel != null)
        } as ListSelectionListener
        view.dirList.addListSelectionListener(selListener)
        
        view.addButton.actionPerformed={
            def fc = new JFileChooser()
            fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            if(fc.showOpenDialog(view.frame) == JFileChooser.APPROVE_OPTION){
                model.add(new Directory(path:fc.getSelectedFile()))
            }            
        }
        
        view.scanButton.actionPerformed={
            view.dirList.selectedValue.scan()
        }
    }    
}

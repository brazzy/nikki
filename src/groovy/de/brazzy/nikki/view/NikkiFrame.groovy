package de.brazzy.nikki.view

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIconimport javax.swing.border.EmptyBorderimport groovy.model.DefaultTableModelimport javax.swing.table.DefaultTableModelimport javax.swing.JFrame
import de.brazzy.nikki.model.Image

/**
 * @author Michael Borgwardt
 */
public class NikkiFrame{
    def frame
    def dirList
    def addButton
    def scanButton
    def saveButton
    def exportButton
    def dayList
    def imageTable
    def progressBar
    
    public static NikkiFrame create(){
        def swing = new SwingBuilder()
        def result = new NikkiFrame()
        
        result.frame = swing.frame(title:'Nikki') {
            borderLayout()
            splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BL.CENTER){
                splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                    panel(){
                        borderLayout()
                        scrollPane(constraints: BL.CENTER){
                            result.dirList = list()
                        }
                        panel(constraints: BL.SOUTH){
                            result.addButton = button(text:'Add')
                            result.scanButton = button(text:'Scan', enabled:false)                      
                        }
                    }
                    panel(){
                        borderLayout()
                        scrollPane(constraints: BL.CENTER){
                            result.dayList = list()
                        }
                        panel(constraints: BL.SOUTH){
                            result.saveButton = button(text:'Save', enabled:false)
                            result.exportButton = button(text:'Export', enabled:false)
                        }
                    }
                }
                scrollPane(){
                      result.imageTable = table(tableHeader:null, rowHeight: 150)
                }
            }
            result.progressBar = progressBar(constraints: BL.SOUTH, minimum:0, maximum:100)
        }
        
        result.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE;
        result.imageTable.setDefaultRenderer(Object.class, new ImageRenderer())
        result.imageTable.setDefaultEditor(Object.class, new ImageRenderer())
        
        return result;
    }    
}

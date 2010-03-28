package de.brazzy.nikki.view

import java.awt.BorderLayout;

import groovy.swing.SwingBuilder;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import de.brazzy.nikki.util.Dialogs;

/**
 * @author Michael Borgwardt
 */
public class NikkiFrame{
    def frame
    def dirList
    def addButton
    def deleteButton
    def scanButton
    def tagButton
    def saveButton
    def exportButton
    def dayList
    def imageTable
    def progressBar
    
    public static NikkiFrame create(Dialogs dialogs){
        def swing = new SwingBuilder()
        def result = new NikkiFrame()
        
        result.frame = swing.frame(title:'Nikki') {
            borderLayout()
            splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BorderLayout.CENTER){
                splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                    panel(){
                        borderLayout()
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dirList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.addButton = button(text:'Add', 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/add.png")))
                            result.deleteButton = button(text:'Delete', enabled:false, 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/bin_closed.png")))                       
                            result.scanButton = button(text:'Scan', enabled:false, 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/find.png")))
                            result.saveButton = button(text:'Save', enabled:false, 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/disk.png")))
                        }
                    }
                    panel(){
                        borderLayout()
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dayList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.tagButton = button(text:'Geotag', enabled:false, 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_link.png")))
                            result.exportButton = button(text:'Export', enabled:false, 
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_go.png")))
                        }
                    }
                }
                scrollPane(){
                      result.imageTable = table(tableHeader:null, rowHeight: 180)
                }
            }
            result.progressBar = progressBar(constraints: BorderLayout.SOUTH, minimum:0, maximum:100)
        }
        
        def renderer = new ImageRenderer(dialogs);
        
        result.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE;
        result.imageTable.setDefaultRenderer(Object.class, renderer)
        result.imageTable.setDefaultEditor(Object.class, renderer)
        
        return result;
    }    
}

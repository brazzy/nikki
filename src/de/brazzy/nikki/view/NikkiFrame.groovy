package de.brazzy.nikki.view
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

import java.awt.BorderLayout

import groovy.swing.SwingBuilder

import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JSplitPane
import javax.swing.border.EmptyBorder;

import de.brazzy.nikki.util.Dialogs

/**
 * Main UI class for the application.
 * 
 * @see Nikki for controller logic
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
    def helpButton
    
    /**
     * @param dialogs For user interaction via modal dialogs
     */
    public static NikkiFrame create(Dialogs dialogs){
        def swing = new SwingBuilder()
        def result = new NikkiFrame()
        
        result.frame = swing.frame(title:'Nikki') {
            borderLayout()
            splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BorderLayout.CENTER){
                splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                    panel(){
                        borderLayout()
                        label(constraints: BorderLayout.NORTH, text:'Directories')
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dirList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.addButton = button(text:'Add', 
                                    toolTipText:'Add a directory containing images and GPS log files',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/add.png")))
                            result.deleteButton = button(text:'Delete', enabled:false, 
                                    toolTipText:'Remove directory from list',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/bin_closed.png")))                       
                            result.scanButton = button(text:'Scan', enabled:false, 
                                    toolTipText:'Find images and GPS log files in directory',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/find.png")))
                            result.saveButton = button(text:'Save', enabled:false, 
                                    toolTipText:'Save all changed data to EXIF headers in image files',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/disk.png")))
                        }
                    }
                    panel(){
                        borderLayout()
                        label(constraints: BorderLayout.NORTH, text:'Days')
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dayList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.tagButton = button(text:'Geotag', enabled:false, 
                                    toolTipText:'Assign GPS coordinates from log files to images based on time',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_link.png")))
                            result.exportButton = button(text:'Export', enabled:false, 
                                    toolTipText:'Export images and GPS paths to compact KMZ file for display in Google Earth',
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_go.png")))
                        }
                    }
                }
                panel(){
                    borderLayout()
                    panel(constraints: BorderLayout.NORTH){
                        borderLayout()
                        label(constraints: BorderLayout.WEST, text:'Images', border: new EmptyBorder(0,5,0,0))
                        result.helpButton = button(constraints: BorderLayout.EAST, enabled:true, border:null,
                                toolTipText:'Information about this program',
                                icon:new ImageIcon(NikkiFrame.class.getResource("/icons/help.png")))
                    }
                    scrollPane(constraints: BorderLayout.CENTER){
                          result.imageTable = table(tableHeader:null, rowHeight: 180)
                    }
                }
            }
            result.progressBar = progressBar(constraints: BorderLayout.SOUTH, minimum:0, maximum:100)
        }
        
        result.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE;
        result.imageTable.setDefaultRenderer(Object.class, new ImageRenderer(dialogs))
        result.imageTable.setDefaultEditor(Object.class, new ImageRenderer(dialogs))
        
        return result;
    }    
}

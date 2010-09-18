package de.brazzy.nikki.view
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

import java.awt.BorderLayout
import java.awt.FlowLayout;

import groovy.swing.SwingBuilder

import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JSplitPane
import javax.swing.border.EmptyBorder

import de.brazzy.nikki.Texts
import de.brazzy.nikki.util.Dialogs
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.ImageSortField

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
    def exportAllButton
    def exportNoneButton
    def dayList
    def imageSortOrder
    def imageTable
    def progressBar
    def helpButton
    
    public static NikkiFrame create(){
        def swing = new SwingBuilder()
        def result = new NikkiFrame()
        def icons = [
                new ImageIcon(NikkiFrame.class.getResource("/icons/logo_64.gif")).image,
                new ImageIcon(NikkiFrame.class.getResource("/icons/logo_32.gif")).image,
                new ImageIcon(NikkiFrame.class.getResource("/icons/logo_16.gif")).image
                ]
        
        result.frame = swing.frame(title:Texts.Main.TITLE, iconImages:icons) {
            borderLayout()
            splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BorderLayout.CENTER){
                splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                    panel(){
                        borderLayout()
                        label(constraints: BorderLayout.NORTH, text:Texts.Main.DIRECTORIES, border: new EmptyBorder(0,5,0,0))
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dirList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.addButton = button(text:Texts.Main.ADD_BUTTON, 
                            toolTipText:Texts.Main.ADD_TOOLTIP,
                            icon:new ImageIcon(NikkiFrame.class.getResource("/icons/add.png")))
                            result.deleteButton = button(text:Texts.Main.DELETE_BUTTON, enabled:false, 
                                    toolTipText:Texts.Main.DELETE_TOOLTIP,
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/bin_closed.png")))                       
                            result.scanButton = button(text:Texts.Main.SCAN_BUTTON, enabled:false, 
                                    toolTipText:Texts.Main.SCAN_TOOLTIP,
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/find.png")))
                            result.saveButton = button(text:Texts.Main.SAVE_BUTTON, enabled:false, 
                                    toolTipText:Texts.Main.SAVE_TOOLTIP,
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/disk.png")))
                        }
                    }
                    panel(){
                        borderLayout()
                        label(constraints: BorderLayout.NORTH, text:Texts.Main.DAYS, border: new EmptyBorder(0,5,0,0))
                        scrollPane(constraints: BorderLayout.CENTER){
                            result.dayList = list()
                        }
                        panel(constraints: BorderLayout.SOUTH){
                            result.tagButton = button(text:Texts.Main.GEOTAG_BUTTON, enabled:false, 
                            toolTipText:Texts.Main.GEOTAG_TOOLTIP,
                            icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_link.png")))
                            result.exportButton = button(text:Texts.Main.EXPORT_BUTTON, enabled:false, 
                                    toolTipText:Texts.Main.EXPORT_TOOLTIP,
                                    icon:new ImageIcon(NikkiFrame.class.getResource("/icons/world_go.png")))
                        }
                    }
                }
                panel(){
                    borderLayout()
                    panel(constraints: BorderLayout.NORTH){
                        borderLayout()
                        panel(constraints: BorderLayout.WEST){
                            flowLayout(alignment:FlowLayout.LEFT, vgap:0)
                            label(text:Texts.Main.IMAGES, border: new EmptyBorder(0,5,0,5))
                            result.imageSortOrder = comboBox(enabled: false,
                                    items: [ImageSortField.FILENAME, ImageSortField.TIME], selectedIndex:-1)
                        }
                        panel(constraints: BorderLayout.CENTER){
                            flowLayout(alignment:FlowLayout.RIGHT, vgap:0)
                            label(text:Texts.Main.EXPORT_TEXT)
                            result.exportAllButton = button(enabled:false, border:new EmptyBorder(2,5,2,5), text:Texts.Main.EXPORT_ALL_BUTTON)
                            result.exportNoneButton = button(enabled:false, border:new EmptyBorder(2,5,2,5), text:Texts.Main.EXPORT_NONE_BUTTON)
                        }
                        result.helpButton = button(constraints: BorderLayout.EAST, enabled:true, border:null,
                                toolTipText:Texts.Main.HELP_TOOLTIP,
                                icon:new ImageIcon(NikkiFrame.class.getResource("/icons/help.png")))
                    }
                    scrollPane(constraints: BorderLayout.CENTER){
                        result.imageTable = table(tableHeader:null, rowHeight: 180)
                    }
                }
            }
            result.progressBar = progressBar(constraints: BorderLayout.SOUTH, minimum:0, maximum:100)
        }
        
        result.frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE;
        
        return result;
    }    
}

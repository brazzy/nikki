package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIconimport javax.swing.border.EmptyBorder
/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static void main(def args){
        def swing = new SwingBuilder()

        def dirList;
        def addButton;
        def scanButton;
        def saveButton;
        def exportButton;
        def dayList;        
        
        def frame = swing.frame(title:'Nikki') {
          borderLayout()
          splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BL.CENTER){
              splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                  panel(){
                      borderLayout()
                      scrollPane(constraints: BL.CENTER){
                          dirList = list(listData: (1..5).collect{"Directory $it"})
                      }
                      panel(constraints: BL.SOUTH){
                          addButton = button(text:'Add')
                          scanButton = button(text:'Scan')                      
                      }
                  }
                  panel(){
                      borderLayout()
                      scrollPane(constraints: BL.CENTER){
                          dayList = list(listData: (1..30).collect{"Day $it"})
                      }
                      panel(constraints: BL.SOUTH){
                          saveButton = button(text:'Save')
                          exportButton = button(text:'Export')
                      }
                  }
                  
                  
              }
              scrollPane(){
                  panel()
                  {
                      boxLayout(axis:BoxLayout.Y_AXIS)
                      (1..25).each{ i ->                          
                          panel(border: new EmptyBorder(5,5,5,5))
                          {
                              borderLayout()
                              label(text:"Image $i", constraints: BL.NORTH)
                              label(icon: new ImageIcon(Nikki.class.getResource("image.jpg")), constraints: BL.WEST)
                              textArea(rows: 2, columns:40, constraints: BL.CENTER, border: new EmptyBorder(3,3,3,3))
                          }
                          separator()
                      }                      
                  }
              }
          }
        }
        frame.pack()
        frame.show()
    }    
}

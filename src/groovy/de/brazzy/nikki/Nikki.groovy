package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout

/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static void main(def args){
        def swing = new SwingBuilder()

        def dirListPanel
        def dayListPanel
        def dayPanel
        
        def frame = swing.frame(title:'Nikki') {
          borderLayout()
          splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, constraints: BL.CENTER){
              splitPane(orientation: JSplitPane.VERTICAL_SPLIT){
                  dirListPanel = panel(){
                      borderLayout()
                      scrollPane(constraints: BL.CENTER){
                          list(listData: (1..5).collect{"Directory $it"})
                      }
                      panel(constraints: BL.SOUTH){
                          button(text:'Add')
                          button(text:'Scan')                      
                      }
                  }
                  dayListPanel = panel(){
                      borderLayout()
                      scrollPane(constraints: BL.CENTER){
                          list(listData: (1..30).collect{"Day $it"})
                      }
                      panel(constraints: BL.SOUTH){
                          button(text:'Export')
                          button(text:'Save')
                      }
                  }
                  
                  
              }
              dayPanel = scrollPane(){
                  list(listData: (1..50).collect{"Image $it"})
              }
          }
        }
        frame.pack()
        frame.show()
    }    
}

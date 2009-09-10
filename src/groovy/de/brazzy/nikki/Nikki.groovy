package de.brazzy.nikki

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JSplitPane
import javax.swing.BoxLayout
import javax.swing.ImageIconimport javax.swing.border.EmptyBorder
import de.brazzy.nikki.view.NikkiFrame
/**
 * @author Michael Borgwardt
 */
public class Nikki{
    public static void main(def args){
        def frame = NikkiFrame.create()
        frame.frame.pack()
        frame.frame.show()
    }    
}

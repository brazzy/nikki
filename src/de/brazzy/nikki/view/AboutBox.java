package de.brazzy.nikki.view;
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


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Displays the about box
 * 
 * @author Michael Borgwardt
 */
public class AboutBox extends JPanel
{
    public AboutBox() throws Exception
    {
        setLayout(new BorderLayout());
        JLabel img = new JLabel(new ImageIcon(getClass().getResource("/icons/logo.png")));
        add(img, BorderLayout.NORTH);
        
        JEditorPane txt = new JEditorPane();
        txt.setContentType("text/html");
        txt.setEditable(false);
        txt.setBackground(getBackground());
        txt.read(new InputStreamReader(getClass().getResourceAsStream("about.html"), "UTF-8"), null);
        txt.addHyperlinkListener(new HyperlinkListener(){
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
                {
                    try
                    {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }                    
                }
            }});
        add(txt, BorderLayout.CENTER);
    }
    
    public static void main(String... args) throws Exception
    {
        JOptionPane.showOptionDialog(null, new AboutBox(), "About Nikki", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
    }
}

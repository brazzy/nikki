package de.brazzy.nikki.view;

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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.brazzy.nikki.Texts;

/**
 * Displays the about box
 * 
 * @author Michael Borgwardt
 */
public class AboutBox extends JPanel {
    private JEditorPane content;

    public AboutBox() throws Exception {
        setLayout(new BorderLayout());
        JLabel img = new JLabel(new ImageIcon(getClass().getResource(
                "/icons/logo_splash.png")));
        add(img, BorderLayout.NORTH);

        content = new JEditorPane();
        content.setContentType("text/html");
        content.setEditable(false);
        content.setBackground(getBackground());
        content.read(new InputStreamReader(getClass().getResourceAsStream(
                Texts.Dialogs.About.FILENAME), "UTF-8"), null);
        content.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        add(content, BorderLayout.CENTER);
    }
    //    
    // public static void main(String... args) throws Exception
    // {
    // JOptionPane.showOptionDialog(null, new AboutBox(), "About Nikki",
    // JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
    // }
}

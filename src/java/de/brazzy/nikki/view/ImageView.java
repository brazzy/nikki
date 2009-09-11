package de.brazzy.nikki.view;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ImageView extends JPanel
{
    private JTextArea textArea;

    public ImageView(int i)
    {
        super(new BorderLayout());
        setBorder(new EmptyBorder(5,5,5,5));
        add(new JLabel("Image "+i), BorderLayout.NORTH);
        add(new JLabel(new ImageIcon(ImageView.class.getResource("image.jpg"))), BorderLayout.WEST);
        
        textArea = new JTextArea(2, 40);
        textArea.setBorder(new EmptyBorder(3,3,3,3));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(new JSeparator(), BorderLayout.SOUTH);
    }    

}

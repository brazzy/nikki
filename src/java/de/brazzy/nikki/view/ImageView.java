package de.brazzy.nikki.view;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.brazzy.nikki.model.Image;


public class ImageView extends JPanel
{
    private JTextArea textArea;
    private JLabel icon = new JLabel();
    private JLabel title = new JLabel();
    
    private Image value;

    public ImageView()
    {
        super(new BorderLayout());
        setBorder(new EmptyBorder(5,5,5,5));
        add(title, BorderLayout.NORTH);
        add(icon, BorderLayout.WEST);
        
        textArea = new JTextArea(2, 40);
        textArea.setBorder(new EmptyBorder(3,3,3,3));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(new JSeparator(), BorderLayout.SOUTH);
    }

    public void setValue(Image value)
    {
        this.value = value;
        title.setText(value.getTitle());
        icon.setIcon(new ImageIcon(value.getThumbnail()));
        textArea.setText(value.getDescription());
    }

    public Image getValue()
    {
        value.setDescription(textArea.getText());
        return value;
    }    

    
}

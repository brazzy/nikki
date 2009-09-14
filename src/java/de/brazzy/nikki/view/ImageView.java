package de.brazzy.nikki.view;

import java.awt.BorderLayout;
import java.text.DateFormat;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.brazzy.nikki.model.Image;


public class ImageView extends JPanel
{
    private JTextArea textArea;
    private JLabel icon = new JLabel();
    private JTextField title = new JTextField();
    private JTextField filename = new JTextField();
    private JTextField time = new JTextField();
    private JTextField latitude = new JTextField();
    private JTextField longitude = new JTextField();

    
    private Image value;

    public ImageView()
    {
        super(new BorderLayout());
        setBorder(new EmptyBorder(5,5,5,5));
        add(icon, BorderLayout.WEST);        
        JPanel grid = new JPanel();
        add(grid, BorderLayout.CENTER);
        
        JLabel filenameLabel = new JLabel("File:");
        JLabel timeLabel = new JLabel("Time:");
        JLabel latitudeLabel = new JLabel("Latitude:");
        JLabel longitudeLabel = new JLabel("Longitude:");
        textArea = new JTextArea(2, 40);
        textArea.setBorder(new EmptyBorder(3,3,3,3));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        GroupLayout layout = new GroupLayout(grid);
        grid.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(title)
            .addGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(filenameLabel)
                        .addComponent(latitudeLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(filename)
                        .addComponent(latitude))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(timeLabel)
                        .addComponent(longitudeLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(time)
                        .addComponent(longitude)))
            .addComponent(scrollPane)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(title)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filenameLabel)
                    .addComponent(filename)
                    .addComponent(timeLabel)
                    .addComponent(time))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(latitudeLabel)
                    .addComponent(latitude)
                    .addComponent(longitudeLabel)
                    .addComponent(longitude))
            .addComponent(scrollPane)                    
        );
        filename.setEditable(false);
        time.setEditable(false);
        latitude.setEditable(false);
        longitude.setEditable(false);

    }

    public void setValue(Image value)
    {
        this.value = value;
        title.setText(value.getTitle());
        filename.setText(value.getFileName());
        time.setText(DateFormat.getDateTimeInstance().format(value.getTime()));
        latitude.setText("48 N");
        longitude.setText("11 E");
        icon.setIcon(new ImageIcon(value.getThumbnail()));
        textArea.setText(value.getDescription());
    }

    public Image getValue()
    {
        value.setDescription(textArea.getText());
        value.setTitle(title.getText());
        return value;
    }    

    
}

package de.brazzy.nikki.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.brazzy.nikki.model.Image;


public class ImageView extends JPanel
{
    public static final int DIFF_THRESHOLD = 30;
    
    private JTextArea textArea;
    private JLabel icon = new JLabel();
    private JTextField title = new JTextField();
    private JTextField filename = new JTextField();
    private JTextField time = new JTextField();
    private JTextField timeDiff = new JTextField();
    private JTextField latitude = new JTextField();
    private JTextField longitude = new JTextField();
    private JButton geoLink = new JButton(new ImageIcon(ImageView.class.getResource("globe.gif")));
    
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
        geoLink.setMargin(new Insets(0,0,0,0));
        geoLink.setDisabledIcon(new ImageIcon(ImageView.class.getResource("globe_grey.gif")));
        geoLink.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    File tmpFile = File.createTempFile("nikki", ".kml");
                    OutputStream tmpOut = new FileOutputStream(tmpFile);
                    value.offsetFinder(tmpOut);
                    Desktop.getDesktop().open(tmpFile);
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(ImageView.this, ex, "Error showing map", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }            
        });

//        geoLink.addActionListener(new ActionListener(){
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                try
//                {
//                    Waypoint wp = value.getWaypoint();
//                    Desktop.getDesktop().browse(new URI("http://maps.google.com/maps?ll="+wp.getLatitude().getValue()+","+wp.getLongitude().getValue()));
//                }
//                catch (Exception ex)
//                {
//                    JOptionPane.showMessageDialog(ImageView.this, ex, "Error showing map", JOptionPane.ERROR_MESSAGE);
//                }
//            }            
//        });

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
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(time)
                                .addComponent(timeDiff))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(longitude)
                                .addComponent(geoLink)
                        )))
                .addComponent(scrollPane)                    
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(title, (int)title.getPreferredSize().getHeight(), (int)title.getPreferredSize().getHeight(), (int)title.getPreferredSize().getHeight())
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filenameLabel)
                    .addComponent(filename)
                    .addComponent(timeLabel)
                    .addComponent(time)
                    .addComponent(timeDiff))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(latitudeLabel)
                    .addComponent(latitude)
                    .addComponent(longitudeLabel)
                    .addComponent(longitude)
                    .addComponent(geoLink))
            .addComponent(scrollPane)
        );
        filename.setEditable(false);
        time.setEditable(false);
        timeDiff.setEditable(false);
        timeDiff.setColumns(5);
        latitude.setEditable(false);
        longitude.setEditable(false);
        geoLink.setEnabled(false);
    }

    public void setValue(Image value)
    {
        this.value = value;
        timeDiff.setText(null);
        timeDiff.setToolTipText(null);
        title.setText(value.getTitle());
        filename.setText(value.getFileName());
        if(value.getTime() != null)
        {
            time.setText(DateFormat.getDateTimeInstance().format(value.getTime()));
            if(value.getWaypoint() != null)
            {
                long diff = (value.getTime().getTime() - value.getWaypoint().getTimestamp().getTime()) / 1000;                
                timeDiff.setText(String.valueOf(diff));
                timeDiff.setToolTipText("Difference between photo time and nearest waypoint time");
                if(Math.abs(diff) > DIFF_THRESHOLD)
                {
                    timeDiff.setForeground(Color.RED);
                }
                else
                {
                    timeDiff.setForeground(Color.BLACK);                    
                }
            }
        }
        if(value.getWaypoint() != null)
        {
            latitude.setText(value.getWaypoint().getLatitude().toString());
            longitude.setText(value.getWaypoint().getLongitude().toString());            
            geoLink.setEnabled(true);
        }
        else
        {
            latitude.setText("?");
            longitude.setText("?");            
            geoLink.setEnabled(false);
        }
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

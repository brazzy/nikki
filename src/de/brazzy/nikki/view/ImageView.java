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
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.brazzy.nikki.Texts;
import de.brazzy.nikki.model.Day;
import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.Dialogs;

/**
 * Used to render and edit {@link Image}s in a JTable
 * 
 * @author Michael Borgwardt
 */
public class ImageView extends JPanel
{
    /** 
     * Seconds beyond which a difference in image time
     * and waypoint time will be highlighted 
     */
    public static final int DIFF_THRESHOLD = 30;
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = ISODateTimeFormat.dateTimeNoMillis();
    public static final ImageIcon COPY_ICON = new ImageIcon(ImageView.class.getResource("/icons/page_copy.png"));
    public static final ImageIcon PASTE_ICON = new ImageIcon(ImageView.class.getResource("/icons/paste_plain.png"));
    
    private JTextArea textArea = new JTextArea(2, 40);
    private JLabel thumbnail = new JLabel();
    private JTextField title = new JTextField();
    private JTextField filename = new JTextField();
    private JTextField time = new JTextField();
    private JTextField timeDiff = new JTextField();
    private JTextField latitude = new JTextField();
    private JTextField longitude = new JTextField();
    private JButton offsetFinder = new JButton(
            new ImageIcon(ImageView.class.getResource("/icons/find.png")));
    private JButton copyPaste = new JButton(COPY_ICON);
    private JCheckBox export = new JCheckBox("export");
    
    private Image clipboard;
    private Image value;
    private Dialogs dialogs;

    /**
     * @param dialogs used for offset finder button
     */
    public ImageView(final Dialogs dialogs)
    {
        super(new BorderLayout());
        this.dialogs = dialogs;
        setBorder(new EmptyBorder(5,5,5,5));
        add(thumbnail, BorderLayout.WEST);        
        JPanel grid = new JPanel();
        add(grid, BorderLayout.CENTER);
        
        JLabel filenameLabel = new JLabel(Texts.Image.FILE);
        JLabel timeLabel = new JLabel(Texts.Image.TIME);
        JLabel latitudeLabel = new JLabel(Texts.Image.LATITUDE);
        JLabel longitudeLabel = new JLabel(Texts.Image.LONGITUDE);
        textArea.setBorder(new EmptyBorder(3,3,3,3));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        offsetFinder.setMargin(new Insets(0,0,0,0));
        offsetFinder.addActionListener(offsetFinderAction);
        copyPaste.setMargin(new Insets(0,0,0,0));
        copyPaste.addActionListener(copyPasteAction);

        GroupLayout layout = new GroupLayout(grid);
        grid.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(title)
                .addComponent(export)
            )
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
                                .addComponent(timeDiff)
                                .addComponent(copyPaste))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(longitude)
                                .addComponent(offsetFinder)
                        )))
                .addComponent(scrollPane)                    
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                    .addComponent(title, (int)title.getPreferredSize().getHeight(), (int)title.getPreferredSize().getHeight(), (int)title.getPreferredSize().getHeight())
                    .addComponent(export)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filenameLabel)
                    .addComponent(filename)
                    .addComponent(timeLabel)
                    .addComponent(time)
                    .addComponent(timeDiff)
                    .addComponent(copyPaste))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(latitudeLabel)
                    .addComponent(latitude)
                    .addComponent(longitudeLabel)
                    .addComponent(longitude)
                    .addComponent(offsetFinder))
            .addComponent(scrollPane)
        );
        filename.setEditable(false);
        time.setEditable(false);
        timeDiff.setEditable(false);
        timeDiff.setColumns(5);
        latitude.setEditable(false);
        longitude.setEditable(false);
        offsetFinder.setEnabled(true);
    }

    private ActionListener offsetFinderAction = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e)
        {
            OutputStream tmpOut = null;
            try
            {
                File tmpFile = File.createTempFile("nikki", ".kml");
                tmpOut = new FileOutputStream(tmpFile);
                value.offsetFinder(tmpOut);
                dialogs.open(tmpFile);
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(ImageView.this, ex, Texts.Image.OFFSET_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    tmpOut.close();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }            
    };
    
    private ActionListener copyPasteAction = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(value.getTime() != null)
            {
                clipboard = value;
            }
            else if(clipboard != null)
            {
                Day d = value.getDay();
                value.setWaypoint(clipboard.getWaypoint());
                value.pasteTime(clipboard.getTime());
                d.fireTableStructureChanged();
                d.fireTableDataChanged();
            }
            else
            {
                throw new IllegalStateException("button should not be "+
                        "enabled when both time and clipboard are empty");
            }
        }
    };
    
    public void setValue(Image value)
    {
        this.value = value;
        timeDiff.setText(null);
        timeDiff.setToolTipText(null);
        title.setText(value.getTitle());
        filename.setText(value.getFileName());
        if(value.getTime() != null)
        {
            copyPaste.setIcon(COPY_ICON);
            copyPaste.setEnabled(true);
            time.setText(TIMESTAMP_FORMAT.print(value.getTime()));
            setTimeDiff(value);
        }
        else
        {
            copyPaste.setIcon(PASTE_ICON);
            copyPaste.setEnabled(clipboard!=null);
            time.setText(null);
        }
        setGpsData(value);
        thumbnail.setIcon(new ImageIcon(value.getThumbnail()));
        textArea.setText(value.getDescription());
        export.setSelected(value.getExport());
    }

    private void setGpsData(Image value)
    {
        if(value.getWaypoint() != null)
        {
            if(value.getWaypoint().getLatitude()!=null)
            {
                latitude.setText(value.getWaypoint().getLatitude().toString());                
            }
            if(value.getWaypoint().getLongitude()!=null)
            {            
                longitude.setText(value.getWaypoint().getLongitude().toString());            
            }
        }
        else
        {
            latitude.setText("?");
            longitude.setText("?");            
        }
    }

    private void setTimeDiff(Image value)
    {
        if(value.getWaypoint() != null && 
           value.getWaypoint().getTimestamp() != null)
        {
            long diff = (value.getTime().getMillis() 
                         - value.getWaypoint().getTimestamp().getMillis()) 
                       / 1000;
            timeDiff.setText(String.valueOf(diff));
            timeDiff.setToolTipText(Texts.Image.DIFF_TOOLTIP);
            if(Math.abs(diff) > DIFF_THRESHOLD)
            {
                timeDiff.setForeground(Color.RED);
            }
            else
            {
                timeDiff.setForeground(Color.BLACK);                    
            }
        }
        else
        {
            timeDiff.setText(null);
        }
    }

    public Image getValue()
    {
        // necessary to trigger setProperty()
        value.setProperty("description", textArea.getText());
        value.setProperty("title", title.getText());
        value.setProperty("export", export.isSelected());
//        value.setDescription(textArea.getText());
//        value.setTitle(title.getText());
//        value.setExport(export.isSelected());

        return value;
    }    

    
}

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
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
public class ImageView extends JPanel {
    /**
     * Seconds beyond which a difference in image time and waypoint time will be
     * highlighted
     */
    public static final int DIFF_THRESHOLD = 30;

    private static final DateTimeFormatter TIMESTAMP_FORMAT = ISODateTimeFormat
            .dateTimeNoMillis();
    private JTextArea textArea = new JTextArea(2, 40);
    private JLabel thumbnail = new JLabel();
    private JTextField title = new JTextField();
    private JTextField filename = new JTextField();
    private JTextField time = new JTextField();
    private JTextField timeDiff = new JTextField();
    private JTextField latitude = new JTextField();
    private JTextField longitude = new JTextField();
    private JButton offsetFinder = new JButton(new ImageIcon(
            ImageView.class.getResource("/icons/find.png")));
    private JButton copy = new JButton(new ImageIcon(
            ImageView.class.getResource("/icons/page_copy.png")));
    private JButton paste = new JButton(new ImageIcon(
            ImageView.class.getResource("/icons/paste_plain.png")));
    private JCheckBox export = new JCheckBox(Texts.Image.EXPORT);

    private Image[] clipboard;
    private Image value;
    private Dialogs dialogs;
    private ActionListener copyListener;
    private boolean empty = true;

    /**
     * @param dialogs
     *            used for offset finder button
     */
    public ImageView(final Dialogs dialogs, Image[] clipboard,
            ActionListener copyListener) {
        super(new BorderLayout());
        this.clipboard = clipboard;
        this.dialogs = dialogs;
        this.copyListener = copyListener;
        setBorder(new EmptyBorder(5, 5, 5, 5));
        add(thumbnail, BorderLayout.WEST);
        JPanel grid = new JPanel();
        add(grid, BorderLayout.CENTER);

        JLabel filenameLabel = new JLabel(Texts.Image.FILE);
        JLabel timeLabel = new JLabel(Texts.Image.TIME);
        JLabel latitudeLabel = new JLabel(Texts.Image.LATITUDE);
        JLabel longitudeLabel = new JLabel(Texts.Image.LONGITUDE);
        textArea.setBorder(new EmptyBorder(3, 3, 3, 3));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        offsetFinder.setMargin(new Insets(0, 0, 0, 0));
        offsetFinder.addActionListener(offsetFinderAction);
        offsetFinder.setToolTipText(Texts.Image.OFFSETFINDER_TOOLTIP);
        copy.setMargin(new Insets(0, 0, 0, 0));
        copy.addActionListener(copyAction);
        copy.setToolTipText(Texts.Image.COPY_TOOLTIP);
        paste.setMargin(new Insets(0, 0, 0, 0));
        paste.addActionListener(pasteAction);
        paste.setToolTipText(Texts.Image.PASTE_TOOLTIP);

        GroupLayout layout = new GroupLayout(grid);
        grid.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup().addComponent(title)
                                .addComponent(export))
                .addGroup(
                        layout.createSequentialGroup()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(filenameLabel)
                                                .addComponent(latitudeLabel))
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(filename)
                                                .addComponent(latitude))
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(timeLabel)
                                                .addComponent(longitudeLabel))
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        time)
                                                                .addComponent(
                                                                        timeDiff)
                                                                .addComponent(
                                                                        copy)
                                                                .addComponent(
                                                                        paste))
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        longitude)
                                                                .addComponent(
                                                                        offsetFinder))))
                .addComponent(scrollPane));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup()
                                .addComponent(
                                        title,
                                        (int) title.getPreferredSize()
                                                .getHeight(),
                                        (int) title.getPreferredSize()
                                                .getHeight(),
                                        (int) title.getPreferredSize()
                                                .getHeight())
                                .addComponent(export))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(filenameLabel)
                                .addComponent(filename).addComponent(timeLabel)
                                .addComponent(time).addComponent(timeDiff)
                                .addComponent(copy).addComponent(paste))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(latitudeLabel)
                                .addComponent(latitude)
                                .addComponent(longitudeLabel)
                                .addComponent(longitude)
                                .addComponent(offsetFinder))
                .addComponent(scrollPane));
        filename.setEditable(false);
        time.setEditable(false);
        timeDiff.setEditable(false);
        timeDiff.setColumns(5);
        timeDiff.setToolTipText(Texts.Image.DIFF_TOOLTIP);
        latitude.setEditable(false);
        longitude.setEditable(false);
        offsetFinder.setEnabled(true);
        title.getDocument().addDocumentListener(autoExportListener);
        textArea.getDocument().addDocumentListener(autoExportListener);
    }

    private transient ActionListener offsetFinderAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            OutputStream tmpOut = null;
            try {
                File tmpFile = File.createTempFile("nikki", ".kml");
                tmpOut = new FileOutputStream(tmpFile);
                value.offsetFinder(tmpOut);
                dialogs.open(tmpFile);
            } catch (Exception ex) {
                Logger.getLogger(ImageView.class).error(
                        "Failed to create offset finder", ex);
                dialogs.error(ex.getMessage());
            } finally {
                try {
                    tmpOut.close();
                } catch (Exception e1) {
                    Logger.getLogger(ImageView.class).error(
                            "Error closing tmp file", e1);
                }
            }
        }
    };

    private transient DocumentListener autoExportListener = new DocumentListener() {
        private void update(DocumentEvent e) {
            if (empty && value.getWaypoint() != null && e.getLength() > 0) {
                export.setSelected(true);
                empty = false;
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update(e);
        }
    };

    private transient ActionListener copyAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (value.getTime() != null) {
                clipboard[0] = value;
                paste.setEnabled(true);
            } else {
                throw new IllegalStateException("button should not be "
                        + "enabled when time is empty");
            }
        }
    };

    private transient ActionListener pasteAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboard[0] != null) {
                Day d = value.getDay();
                value.setWaypoint(clipboard[0].getWaypoint());
                value.pasteTime(clipboard[0].getTime());
                d.fireTableStructureChanged();
                d.fireTableDataChanged();
                copyListener.actionPerformed(new ActionEvent(this, 0, null));
            } else {
                throw new IllegalStateException("button should not be "
                        + "enabled when clipboard is empty");
            }
        }
    };

    public void setValue(Image value) {
        this.value = value;
        timeDiff.setText(null);
        timeDiff.setToolTipText(null);
        title.setText(value.getTitle());
        filename.setText(value.getFileName());
        if (value.getTime() != null) {
            copy.setEnabled(true);
            time.setText(TIMESTAMP_FORMAT.print(value.getTime()));
            setTimeDiff(value);
        } else {
            copy.setEnabled(false);
            time.setText(null);
        }
        paste.setEnabled(clipboard[0] != null);
        setGpsData(value);
        thumbnail.setIcon(new ImageIcon(value.getThumbnail()));
        textArea.setText(value.getDescription());
        export.setSelected(value.getExport());
        if (value.getWaypoint() == null) {
            export.setEnabled(false);
            export.setToolTipText(Texts.Image.EXPORT_LOCKED_TOOLTIP);
        } else {
            export.setEnabled(true);
            export.setToolTipText(Texts.Image.EXPORT_TOOLTIP);
        }
        empty = ((value.getTitle() == null || value.getTitle().length() == 0) && (value
                .getDescription() == null || value.getDescription().length() == 0));
    }

    private void setGpsData(Image value) {
        if (value.getWaypoint() != null) {
            if (value.getWaypoint().getLatitude() != null) {
                latitude.setText(value.getWaypoint().getLatitude().toString());
            }
            if (value.getWaypoint().getLongitude() != null) {
                longitude
                        .setText(value.getWaypoint().getLongitude().toString());
            }
        } else {
            latitude.setText("?");
            longitude.setText("?");
        }
    }

    private void setTimeDiff(Image value) {
        if (value.getWaypoint() != null
                && value.getWaypoint().getTimestamp() != null) {
            long diff = (value.getTime().getMillis() - value.getWaypoint()
                    .getTimestamp().getMillis()) / 1000;
            timeDiff.setText(String.valueOf(diff));
            if (Math.abs(diff) > DIFF_THRESHOLD) {
                timeDiff.setForeground(Color.RED);
            } else {
                timeDiff.setForeground(Color.BLACK);
            }
        } else {
            timeDiff.setText(null);
        }
    }

    public Image getValue() {
        // necessary to trigger setProperty()
        value.setProperty("description", textArea.getText());
        value.setProperty("title", title.getText());
        value.setProperty("export", export.isSelected());
        // value.setDescription(textArea.getText());
        // value.setTitle(title.getText());
        // value.setExport(export.isSelected());

        return value;
    }
}

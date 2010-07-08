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

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.Dialogs;

/**
 * For rendering/editing {@link Image}s in a JTable. Uses {@link ImageView} to
 * do the actual work.
 * 
 * @author Michael Borgwardt
 */
public class ImageRenderer extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor {
    ImageView view;

    public ImageRenderer(Dialogs dialogs, Image[] clipboard,
            ActionListener copyListener) {
        view = new ImageView(dialogs, clipboard, copyListener);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        view.setValue((Image) value);
        return view;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        view.setValue((Image) value);
        return view;
    }

    @Override
    public Object getCellEditorValue() {
        return view.getValue();
    }

}

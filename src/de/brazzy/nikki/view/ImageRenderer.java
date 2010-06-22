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

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.Dialogs;

/**
 * For rendering/editing {@link Image}s in a JTable.
 * Uses {@link ImageView} to do the actual work.
 *
 * @author Michael Borgwardt
 */
public class ImageRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
{
    ImageView view;

    public ImageRenderer(Dialogs dialogs, Image[] clipboard)
    {
        view = new ImageView(dialogs, clipboard);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column)
    {
        view.setValue((Image)value);
        return view;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        view.setValue((Image)value);
        return view;
    }

    @Override
    public Object getCellEditorValue()
    {
        return view.getValue();
    }

}

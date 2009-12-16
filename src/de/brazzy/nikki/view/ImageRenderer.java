package de.brazzy.nikki.view;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.util.Dialogs;

public class ImageRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
{
    ImageView view;

    public ImageRenderer(Dialogs dialogs)
    {
        view = new ImageView(dialogs);
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

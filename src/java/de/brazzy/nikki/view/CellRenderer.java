package de.brazzy.nikki.view;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class CellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column)
    {
        return new ImageView(row);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) 
    {
        return new ImageView(row);        
    }
    
    public Object getCellEditorValue()
    {
        return null;
    }

}

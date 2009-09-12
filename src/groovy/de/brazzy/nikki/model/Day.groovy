package de.brazzy.nikki.model;

import javax.swing.table.AbstractTableModel
class Day extends AbstractTableModel{

    List<Waypoint> waypoints = [];
    List<Image> images = [];
    
    Date date;
    Directory directory;
    
    public String toString()
    {        
        (date==null? "unknown" : date.getDateString())+" ("+images.size()+")"
    }

    public int getRowCount()
    {
        images.size()
    }
    
    public int getColumnCount()
    {
        1
    }
    
    public Object getValueAt(int row, int column)
    {
        images[row]
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) 
    {
        true
    }
}

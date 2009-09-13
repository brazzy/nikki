package de.brazzy.nikki.model;

import javax.swing.table.AbstractTableModel
class Day extends AbstractTableModel implements Externalizable
{
    public static final long serialVersionUID = 1;

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
    
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(waypoints)
        out.writeObject(images)
        out.writeObject(date)
    }
    
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException
    {
        waypoints = oi.readObject()
        images = oi.readObject()
        date = oi.readObject()
    }
}

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
        (date==null? "unknown" : date.getDateString())+" ("+images.size()+", "+waypoints.size()+")"
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
    
    public void geotag()
    {
        waypoints.sort()
        images.each{ image ->
            def index = Collections.binarySearch(waypoints, new Waypoint(timestamp:image.time))
            if(index>=0) // direct hit
            {
                image.waypoint = waypoints[index]
            }
            else if(-index==waypoints.size()+1) // after all WPs
            {
                image.waypoint = waypoints[waypoints.size()-1]
            }
            else if(index == -1) // before all WPs
            {
                image.waypoint = waypoints[0]
            }
            else
            {
                def before = waypoints[-(index+2)]
                def after = waypoints[-(index+1)]
                if(image.time.time - before.timestamp.time > 
                   after.timestamp.time - image.time.time)
                {
                    image.waypoint = after
                }
                else
                {
                    image.waypoint = before
                }
            }
        }
        
    }
}

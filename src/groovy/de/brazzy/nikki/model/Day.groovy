package de.brazzy.nikki.model;

import javax.swing.table.AbstractTableModel
import de.micromata.opengis.kml.v_2_2_0.Kmlimport de.micromata.opengis.kml.v_2_2_0.KmlFactoryimport de.micromata.opengis.kml.v_2_2_0.Coordinateimport de.micromata.opengis.kml.v_2_2_0.Documentimport de.micromata.opengis.kml.v_2_2_0.Placemarkimport de.micromata.opengis.kml.v_2_2_0.LineStringimport de.micromata.opengis.kml.v_2_2_0.AltitudeModeclass Day extends AbstractTableModel implements Externalizable
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
    
    public void export(OutputStream out)
    {
        Kml kml = KmlFactory.createKml()
        Document doc = kml.createAndSetDocument()
        
        images.each{ Image image ->
            doc.createAndAddPlacemark()
            .withName(image.title)
            .withDescription(image.longDescription)
            .withVisibility(true)
            .createAndSetPoint()
                .withCoordinates([new Coordinate(image.waypoint.longitude.value, image.waypoint.latitude.value)])
        }
        LineString ls = doc.createAndAddPlacemark()
                           .createAndSetLineString()
                           .withTessellate(Boolean.TRUE)
                           .withExtrude(Boolean.TRUE)
                           .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
        
        waypoints.each{ waypoint ->
            ls.addToCoordinates(waypoint.longitude.value, waypoint.latitude.value)
        }
        
        kml.marshal(out)
        out.close()
    }
}

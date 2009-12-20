package de.brazzy.nikki.model;

import de.brazzy.nikki.util.ImageReader
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.Document
import de.micromata.opengis.kml.v_2_2_0.Coordinate
import de.micromata.opengis.kml.v_2_2_0.KmlFactory
import java.text.NumberFormat
class Image implements Serializable{
    public static final long serialVersionUID = 1;
    public static final String OFFSET_FINDER_COLOR = "801977FF";
    public static final double OFFSET_FINDER_SCALE = 3.0
    String fileName
    String title
    String description
    Date time
    Day day
    Waypoint waypoint
    byte[] thumbnail
    boolean export
    
    public String getLongDescription()
    {
        def writer = new StringWriter()  
        def builder = new groovy.xml.MarkupBuilder(writer) 
        builder.html(){ 
          body(){ 
            img(src: "images/"+fileName)
            p(description) 
          } 
        } 
        return writer.toString()
    }
    
    public void offsetFinder(OutputStream out)
    {
        if(!waypoint)
        {
            day.waypoints.sort()
            geotag(0)
        }
        Kml kml = KmlFactory.createKml()
        Document doc = kml.createAndSetDocument()

        doc.createAndAddStyle().withId("image")
            .createAndSetIconStyle()
            .withScale(OFFSET_FINDER_SCALE)
            .withColor(OFFSET_FINDER_COLOR)

        doc.createAndAddPlacemark()
            .withName(title)
            .withStyleUrl("#image")
            .withVisibility(true)
            .createAndSetPoint()
                .withCoordinates([new Coordinate(waypoint.longitude.value, waypoint.latitude.value)])

        NumberFormat nf = NumberFormat.getIntegerInstance();
        day.waypoints.each{ Waypoint point ->
            doc.createAndAddPlacemark()
            .withName(nf.format((point.timestamp.time-time.time)/1000))
            .withVisibility(true)
            .createAndSetPoint()
                .withCoordinates([new Coordinate(point.longitude.value, point.latitude.value)])
        }
        
        kml.marshal(out)
        out.close()
    }

    private void geotag(long milliOffset)
    {
        def imagetime = new Date(time.time + milliOffset)
        def index = Collections.binarySearch(day.waypoints, new Waypoint(timestamp:imagetime))
        if(index>=0) // direct hit
        {
            waypoint = day.waypoints[index]
        }
        else if(-index==day.waypoints.size()+1) // after all WPs
        {
            waypoint = day.waypoints[day.waypoints.size()-1]
        }
        else if(index == -1) // before all WPs
        {
            waypoint = day.waypoints[0]
        }
        else
        {
            def before = day.waypoints[-(index+2)]
            def after = day.waypoints[-(index+1)]
            if(imagetime.time - before.timestamp.time >
               after.timestamp.time - imagetime.time)
            {
                waypoint = after
            }
            else
            {
                waypoint = before
            }
        }
    }
}

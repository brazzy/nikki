package de.brazzy.nikki.model;

import de.brazzy.nikki.util.ImageReaderimport de.micromata.opengis.kml.v_2_2_0.Kmlimport de.micromata.opengis.kml.v_2_2_0.Documentimport de.micromata.opengis.kml.v_2_2_0.Coordinateimport de.micromata.opengis.kml.v_2_2_0.KmlFactoryimport java.text.NumberFormatclass Image implements Serializable{
    public static final long serialVersionUID = 1;
    
    String fileName
    String title
    String description
    Date time
    Day day
    Waypoint waypoint
    byte[] thumbnail
    
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
        Kml kml = KmlFactory.createKml()
        Document doc = kml.createAndSetDocument()

        doc.createAndAddStyle().withId("image")
            .createAndSetIconStyle()
            .withScale(3.0)
            .withColor("801977FF")

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


}

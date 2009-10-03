package de.brazzy.nikki.model;

class Image implements Serializable{
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
            img(src: new File(day.directory.path, fileName).toURL(), width:200, height:150)
            p(description) 
          } 
        } 
        return writer.toString()
    }
}

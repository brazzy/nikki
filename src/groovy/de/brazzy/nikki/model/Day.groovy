package de.brazzy.nikki.model;

class Day {

    List<Waypoint> waypoints = [];
    List<Image> images = [];
    
    Date date;
    Directory directory;
    
    public String toString()
    {        
        (date==null? "unknown" : date.getDateString())+" ("+images.size()+")"
    }

}

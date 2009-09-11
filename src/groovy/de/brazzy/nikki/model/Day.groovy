package de.brazzy.nikki.model;

class Day {

    List<Waypoint> waypoints = [];
    List<Image> images = [];
    
    Date date;
    Directory directory;
    
    public String toString()
    {
        date.getDateString()+" ("+images.size()+")"
    }

}

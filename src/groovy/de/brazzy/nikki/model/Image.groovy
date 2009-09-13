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
}

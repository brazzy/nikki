package de.brazzy.nikki.model;

class WaypointFile implements Serializable{
    public static final long serialVersionUID = 1;

    List<Waypoint> waypoints;
    Directory directory;
	String fileName;
}

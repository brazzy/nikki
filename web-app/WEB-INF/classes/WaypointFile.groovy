class WaypointFile {
    static mappedBy = [waypoints:'file']	
    static hasMany = [waypoints:Waypoint]

    static constraints = {
    }

    Directory directory;
	String fileName;
}

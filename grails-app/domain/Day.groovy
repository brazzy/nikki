class Day {

    static mappedBy = [images:'day', waypoints:'day']	
    static hasMany = [images:Image, waypoints:Waypoint]

    static constraints = {
    }
    
    Date date;
    Directory directory;
}

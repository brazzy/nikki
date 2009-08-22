class Directory {

    static mappedBy = [days:'directory', images:'directory', waypointFiles:'directory']	
    static hasMany = [days:Day, images:Image, waypointFiles:WaypointFile]

    static constraints = {
    }
    
    String path;
}

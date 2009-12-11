package de.brazzy.nikki.model;

class WaypointFile implements Serializable{
    public static final long serialVersionUID = 1;

    List<Waypoint> waypoints = [];
    Directory directory;
	String fileName;

	public static WaypointFile parse(Directory dir, File file)
	{
	    def result = new WaypointFile(directory: dir, fileName: file.name)
	    file.eachLine{ line ->
	        if(line.startsWith('$GPRMC'))
	        {
	            def wp = Waypoint.parse(dir, result, line)
	            result.waypoints.add(wp)
	        }
	    }
	    return result;
	}

}

package de.brazzy.nikki.util;

import java.io.Serializable;
import java.util.Comparator;

import de.brazzy.nikki.model.Waypoint;

/**
 * Compares waypoints based on their timestamps, used for geotagging.
 * 
 * @author Michael Borgwardt
 */
public class WaypointComparator implements Comparator<Waypoint>, Serializable
{
    public static final long serialVersionUID = 1L;

    @Override
    public int compare(Waypoint o1, Waypoint o2)
    {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}

package de.brazzy.nikki.util;

import java.util.Comparator;

import de.brazzy.nikki.model.Waypoint;

public class WaypointComparator implements Comparator<Waypoint>
{
    @Override
    public int compare(Waypoint o1, Waypoint o2)
    {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}

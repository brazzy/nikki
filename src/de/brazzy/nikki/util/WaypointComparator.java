package de.brazzy.nikki.util;
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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

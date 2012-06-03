package de.brazzy.nikki.model;
/*
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


/**
 * An entry in a GPS track, with timestamp and GPS coordinates
 */
class Waypoint implements Serializable, Comparable<Waypoint>{
    public static final long serialVersionUID = 1;

    private static final DateTimeFormatter PARSE_FORMAT = DateTimeFormat.forPattern('ddMMyyHHmmss.SSS').withZone(DateTimeZone.UTC)

    /** GPS log file in which the waypoint is recorded */
    WaypointFile file

    /** Day to which the waypoint is assigned (based on timezone of nearest town) */
    Day day

    /** Timestamp of the log entry (based on timezone of nearest town) */
    DateTime timestamp

    GeoCoordinate latitude

    GeoCoordinate longitude

    /**
     * Set on waypoints after a time gap, which should start a new
     * line segment in the exported file.
     **/
    boolean startNewLine

    public String toString() {
        return timestamp.toString()
    }

    public int compareTo(Waypoint other){
        return this.timestamp.compareTo(other.timestamp);
    }

    public float distanceInMeters(Waypoint other) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(latitude.value-other.latitude.value);
        double dLng = Math.toRadians(longitude.value-other.longitude.value);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(other.latitude.value)) * Math.cos(Math.toRadians(latitude.value)) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;
        return new Float(dist * meterConversion).floatValue();
    }
}

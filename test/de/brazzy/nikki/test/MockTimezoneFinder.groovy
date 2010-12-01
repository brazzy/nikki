package de.brazzy.nikki.test;

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

import de.brazzy.nikki.util.TimezoneFinder;
import org.joda.time.DateTimeZone;

class MockTimezoneFinder extends TimezoneFinder {
    def queue = []
    
    public addCall(float lat, float lng, DateTimeZone result) {
        queue.add([lat, lng, result])
    }
    
    public DateTimeZone find(float latitude, float longitude) {
        def entry = queue.remove(0)
        if(!Float.isNaN(entry[0])) {
            assert Math.abs(entry[0] - latitude) < 0.1, "error: $latitude"
        }
        if(!Float.isNaN(entry[1])) {
            assert Math.abs(entry[1] - longitude) < 0.1, "error: $longitude"
        }
        return entry[2]
    }
    
    public void finished() {
        assert queue.size() == 0
    }
}
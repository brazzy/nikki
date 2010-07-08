package de.brazzy.nikki.util;

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

import java.io.File;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;

public class ParseWaypointBenchmark {
    public static void main(String... args) throws Exception {
        long start = System.nanoTime();
        TimezoneFinder finder = new TimezoneFinder(TimezoneFinder.class
                .getResourceAsStream("timezones.dat"));
        System.out
                .println((System.nanoTime() - start) / (1000 * 1000 * 1000.0));
        Directory dir = new Directory();
        dir.setPath(new File(args[0]));
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setFinder(finder);
        scanner.setZone(DateTimeZone.UTC);
        scanner.scan(dir, null);
        System.out
                .println((System.nanoTime() - start) / (1000 * 1000 * 1000.0));
    }
}

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

import java.io.File;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;

public class ParseWaypointBenchmark
{
    public static void main(String... args) throws Exception
    {
        long start = System.nanoTime();
        TimezoneFinder finder = new TimezoneFinder(TimezoneFinder.class.getResourceAsStream("timezones.dat"));
        System.out.println((System.nanoTime()-start)/(1000*1000*1000.0));
        Directory dir = new Directory();
        dir.setPath(new File(args[0]));
        dir.scan(null, DateTimeZone.UTC, finder);
        System.out.println((System.nanoTime()-start)/(1000*1000*1000.0));
    }
}

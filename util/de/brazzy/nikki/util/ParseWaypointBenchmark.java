package de.brazzy.nikki.util;

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

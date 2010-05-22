package de.brazzy.nikki.test
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

import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.prefs.Preferences
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatter
import org.joda.time.DateTime;
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat

/**
 *
 * @author Michael Borgwardt
 */
class AbstractNikkiTest extends GroovyTestCase
{
    public static final DateTimeZone TZ_BERLIN = DateTimeZone.forID("Europe/Berlin")
    public static final DateTimeZone TZ_DARWIN = DateTimeZone.forID("Australia/Darwin")
    public static final DateTimeZone TZ_BRISBANE = DateTimeZone.forID("Australia/Brisbane")
    protected static final DateTimeZone ZONE = TZ_DARWIN
    protected static final String DATE1 = "2009-11-11";
    protected static final String DATE2 = "2009-11-12";
    protected static final String IMAGE1 = "IMG${DATE1}.JPG";
    protected static final String IMAGE2 = "IMG${DATE2}.JPG";
    protected static final String NO_EXIF = "no_exif.jpg";
    protected static final String WAYPOINTS1 = "20091111.nmea";
    protected static final String WAYPOINTS2 = "20091112.nmea";
    protected static final LocalDate DAY1 = new LocalDate(2009, 11, 11);
    protected static final LocalDate DAY2 = new LocalDate(2009, 11, 12);
    protected static final DateTime TIME1 = DAY1.toDateTime(new LocalTime(5, 0, 0), ZONE)
    protected static final DateTime TIME2 = DAY2.toDateTime(new LocalTime(5, 0, 0), ZONE)
    protected static final byte[] THUMB = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wAALCAABAAEBAREA/8QAFAABAAAAAAAAAAAAAAAAAAAAA//EABQQAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQEAAD8AN//Z".decodeBase64()
    protected static final DateTimeFormatter FORMAT = ISODateTimeFormat.date().withZone(ZONE)
    protected static final DateTimeFormatter FORMAT_TIME = ISODateTimeFormat.dateTimeNoMillis().withZone(ZONE)

    protected Directory tmpDir;

    protected void setUp()
    {
        File tmpFile = File.createTempFile("nikkitest",null)
        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()
        tmpDir = new Directory(path: tmpFile)
    }

    protected void tearDown()
    {
        Preferences p = Preferences.userNodeForPackage(getClass())
        p.removeNode()
        p.flush()
    }

    protected void copyFile(String name)
    {
        File f = new File(tmpDir.path, name)
        def stream = new FileOutputStream(f)
        IOUtils.copy(DirectoryTest.class.getResourceAsStream(name),
            stream)
        stream.close()
        f.deleteOnExit()
    }

    protected WaypointFile addWaypointFile(LocalDate date, String fileName)
    {
        Day day = tmpDir.find{ it.date.equals(date)}
        if(!day)
        {
            day = new Day(date: date, directory:tmpDir)
            tmpDir.add(day);
        }
        WaypointFile file = new WaypointFile(fileName: fileName, directory:tmpDir)
        file.waypoints.add(constructWaypoint(day, 1))
        file.waypoints.add(constructWaypoint(day, 2))
        tmpDir.waypointFiles.put(fileName, file)
        return file
    }

    protected static Waypoint constructWaypoint(Day day, int index)
    {
        Waypoint wp = new Waypoint(day: day, timestamp: day.date.toDateTime(new LocalTime(index, 0, 0), ZONE),
            latitude: new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: (double)index),
            longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: (double)index+20))
        day.waypoints << wp
        return wp
    }

    protected Image addImage(LocalDate date, String fileName)
    {
        Day day = tmpDir.find{ it.date.equals(date)}
        if(!day)
        {
            day = new Day(date: date, directory:tmpDir)
            tmpDir.add(day);
        }
        Waypoint wp = date == null ? null : constructWaypoint(day, 5)
        Image image = new Image(fileName: fileName, title:"testTitle",
            description:"testDescription", day: day, thumbnail: THUMB,
            export: true, time: wp?.timestamp, waypoint: wp, modified: true)
        day.images.add(image)
        tmpDir.images.put(fileName, image)
        return image
    }

    public void checkEqualsHashCode(List a, List b)
    {
        for(int i=0; i<a.size(); i++)
        {
            for(int j=0; j<b.size(); j++)
            {
                if(i==j)
                {
                    assert a[i].hashCode() == b[i].hashCode()
                    assert a[i].hashCode() == a[i].hashCode()
                    assert b[i].hashCode() == b[i].hashCode()
                    assert a[i] == a[i]
                    assert a[i] == b[i]
                    assert !a[i].is(b[i])
                }
                else
                {
                    assert !a[i].is(b[i]), "$i, $j"
                    assert a[i] != a[j]  , "$i, $j"                  
                    assert a[i] != b[j]  , "$i, $j"
                }                                 
            }
        }
    }
}


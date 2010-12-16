package de.brazzy.nikki.test
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

import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

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
class AbstractNikkiTest extends GroovyTestCase {
    public static final DateTimeZone TZ_BERLIN = DateTimeZone.forID("Europe/Berlin")
    public static final DateTimeZone TZ_STOCKHOLM = DateTimeZone.forID("Europe/Stockholm")
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
    
    protected void setUp() {
        tmpDir = new Directory(path: new File(
                System.getProperty("java.io.tmpdir")+"/nikkitest"+(int)(Math.random()*1e9)));
        Locale.setDefault(Locale.GERMAN);
        Logger.getRootLogger().getAppender("A1").rollOver()
    }
    
    protected void tearDown() {
        Preferences p = Preferences.userNodeForPackage(getClass())
        p.removeNode()
        p.flush()
    }
    
    protected void copyFile(String name) {
        ensureTmpDir()
        
        File f = new File(tmpDir.path, name)
        def stream = new FileOutputStream(f)
        IOUtils.copy(AbstractNikkiTest.class.getResourceAsStream(name),
                stream)
        stream.close()
        f.deleteOnExit()
    }
    
    protected void ensureTmpDir() {
        if(!tmpDir.path.exists()) {
            File tmpFile = File.createTempFile("nikkitest",null)
            tmpFile.delete()
            tmpFile.mkdir()
            tmpFile.deleteOnExit()
            tmpDir.path = tmpFile
        }
    }
    
    protected WaypointFile addWaypointFile(LocalDate date, String fileName) {
        Day day = tmpDir.find{ it.date.equals(date)}
        if(!day) {
            day = new Day(date: date, directory:tmpDir)
            tmpDir.add(day);
        }
        WaypointFile file = new WaypointFile(fileName: fileName, directory:tmpDir)
        file.waypoints.add(constructWaypoint(day, 1))
        file.waypoints.add(constructWaypoint(day, 2))
        tmpDir.addWaypointFile(file)
        return file
    }
    
    protected static Waypoint constructWaypoint(Day day, int index=5) {
        Waypoint wp = new Waypoint(day: day, timestamp: day.date.toDateTime(new LocalTime(index, 0, 0), ZONE),
                latitude: new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: (double)index),
                longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: (double)index+20))
        day.waypoints << wp
        return wp
    }
    
    protected static Image constructImage(LocalDate date, String fileName, int index=5) {
        return new Image(fileName: fileName, title:"testTitle",
        description:"testDescription", thumbnail: THUMB,
        export: true, time: date?.toDateTime(new LocalTime(index, 0, 0), ZONE), modified: true)
    }
    
    protected Image addImage(LocalDate date, String fileName, int index=5) {
        Image image = constructImage(date, fileName, index);
        tmpDir.addImage(image);
        return image
    }
    
    public void checkEqualsHashCode(List a, List b) {
        for(int i=0; i<a.size(); i++) {
            for(int j=0; j<b.size(); j++) {
                if(i==j) {
                    assert a[i].hashCode() == b[i].hashCode()
                    assert a[i].hashCode() == a[i].hashCode()
                    assert b[i].hashCode() == b[i].hashCode()
                    assert a[i] == a[i]
                    assert a[i] == b[i]
                    assert !a[i].is(b[i])
                }
                else {
                    assert !a[i].is(b[i]), "$i, $j"
                    assert a[i] != a[j]  , "$i, $j"                  
                    assert a[i] != b[j]  , "$i, $j"
                }
            }
        }
    }
    
    
    public static boolean logContains(String needle){
        File logFile = new File(System.getProperty("user.home")+"/nikki.log")
        def needleFound = false
        logFile.eachLine{ if(it.contains(needle)) needleFound = true }
        return needleFound
    }
}


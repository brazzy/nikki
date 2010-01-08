package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.Directory
import de.brazzy.nikki.model.Day
import de.brazzy.nikki.model.Image
import de.brazzy.nikki.model.Waypoint
import de.brazzy.nikki.model.WaypointFile
import de.brazzy.nikki.model.GeoCoordinate
import de.brazzy.nikki.model.Cardinal
import de.brazzy.nikki.util.RelativeDateFormat
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.util.prefs.Preferences

/**
 *
 * @author Brazil
 */
class AbstractNikkiTest extends GroovyTestCase
{
    protected static final TimeZone ZONE = TimeZone.getTimeZone("Etc/GMT-10")
    protected static final RelativeDateFormat FORMAT = new RelativeDateFormat(ZONE)
    protected static final String DATE1 = "2009-11-11";
    protected static final String DATE2 = "2009-11-12";
    protected static final String IMAGE1 = "IMG${DATE1}.JPG";
    protected static final String IMAGE2 = "IMG${DATE2}.JPG";
    protected static final String WAYPOINTS1 = "20091111.nmea";
    protected static final String WAYPOINTS2 = "20091112.nmea";
    protected static final Date DAY1 = FORMAT.stripTime(FORMAT.parse(DATE1));
    protected static final Date DAY2 = FORMAT.stripTime(FORMAT.parse(DATE2));
    protected static final Date TIME1 = new Date(DAY1.time+(60*60*5*1000))
    protected static final Date TIME2 = new Date(DAY2.time+(60*60*5*1000))
    protected static final byte[] THUMB = [1 , 2 , 3, 4, 5, 6, 7, 8] as byte[]

    protected Directory tmpDir;

    public void setUp()
    {
        File tmpFile = File.createTempFile("nikkitest",null)
        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()
        tmpDir = new Directory(path: tmpFile)
        tmpDir.zone = ZONE
    }

    public void tearDown()
    {
        Preferences p = Preferences.userNodeForPackage(getClass())
        p.removeNode()
        p.flush()
    }

    protected void copyFile(String name)
    {
        IOUtils.copy(DirectoryTest.class.getResourceAsStream(name),
            new FileOutputStream(new File(tmpDir.path, name)))
    }

    protected WaypointFile constructWaypointFile(Date date, String fileName)
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
        day.waypoints.addAll(file.waypoints)
        return file
    }

    protected Waypoint constructWaypoint(Day day, int index)
    {
        Waypoint wp = new Waypoint(day: day, timestamp: new Date(day.date.time + (60*60*1000*index)),
            latitude: new GeoCoordinate(direction: Cardinal.SOUTH, magnitude: (double)index),
            longitude: new GeoCoordinate(direction: Cardinal.EAST, magnitude: (double)index+20))
        return wp
    }

    protected Image constructImage(Date date, String fileName)
    {
        Day day = tmpDir.find{ it.date.equals(date)}
        if(!day)
        {
            day = new Day(date: date, directory:tmpDir)
            tmpDir.add(day);
        }
        Waypoint wp = constructWaypoint(day, 5)
        Image image = new Image(fileName: fileName, title:"testTitle",
            description:"testDescription", day: day, thumbnail: THUMB,
            export: true, time: wp.timestamp, waypoint: wp)
        day.images.add(image)
        return image
    }

	
}


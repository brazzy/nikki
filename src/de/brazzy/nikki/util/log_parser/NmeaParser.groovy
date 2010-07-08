package de.brazzy.nikki.util.log_parser;

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

import java.util.NoSuchElementException;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.brazzy.nikki.model.Cardinal;
import de.brazzy.nikki.model.GeoCoordinate;
import de.brazzy.nikki.model.Waypoint;

/**
 * Parses the NMEA GPS file format
 * 
 * @author Michael Borgwardt
 */
public class NmeaParser extends ExtensionFilter implements LogParser {
    public NmeaParser() {
        super(["NMEA", "NME"] as String[])
    }
    
    /* (non-Javadoc)
     * @see de.brazzy.nikki.util.log_parser.LogParser#parse(java.io.InputStream)
     */
    @Override
    public Iterator<Waypoint> parse(InputStream input) throws ParserException {
        if(!input) {
            throw new IllegalArgumentException("GPS input stream is null!");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "US-ASCII"))
        return new NmeaIterator(reader)
    }
    
    /**
     * @param mag magnitude String
     * @param dir direction String
     * @return parsed result
     */
    public static GeoCoordinate parseCoordinate(String mag, String dir) {
        if(dir.length()!=1) {
            throw new IllegalArgumentException(dir)
        }
        def spl = mag.tokenize(".")
        def degrees = Integer.parseInt(spl[0].substring(0, spl[0].length()-2))
        double minutes = Double.parseDouble(spl[0].substring(spl[0].length()-2, spl[0].length())+"."+spl[1])
        degrees += minutes / 60.0d
        def result = new GeoCoordinate(direction: Cardinal.parse(dir), magnitude: degrees)
        return result;
    }
}

class NmeaIterator implements Iterator<Waypoint> {
    private static final DateTimeFormatter PARSE_FORMAT = 
    DateTimeFormat.forPattern('ddMMyyHHmmss.SSS').withZone(DateTimeZone.UTC)
    
    private BufferedReader reader
    private String nextLine
    
    public NmeaIterator(BufferedReader reader){
        this.reader = reader
        read()
    }
    
    boolean hasNext(){
        return nextLine != null
    }
    
    Waypoint next(){
        if(!nextLine) {
            throw new NoSuchElementException();
        }
        Waypoint result = new Waypoint()
        try {
            def data = nextLine.trim().tokenize(',')        
            result.latitude = NmeaParser.parseCoordinate(data[3], data[4])
            result.longitude = NmeaParser.parseCoordinate(data[5], data[6])        
            result.timestamp = PARSE_FORMAT.parseDateTime(data[9]+data[1])             
        }
        catch(Exception ex) {
            throw new ParserException("line was: "+nextLine, ex)
        }
        read()
        return result
    }
    
    private void read() {
        nextLine = reader.readLine()
        while(nextLine != null && !nextLine.startsWith('$GPRMC')) {
            nextLine = reader.readLine()
        }
        if(nextLine == null){
            reader.close();
        }
    }
    
    void remove(){ throw new UnsupportedOperationException()
    }
}
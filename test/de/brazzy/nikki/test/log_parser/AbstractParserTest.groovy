package de.brazzy.nikki.test.log_parser;

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

import static org.junit.Assert.*;
import java.io.File;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import de.brazzy.nikki.util.log_parser.LogParser;
import de.brazzy.nikki.util.log_parser.ParserException;

/**
 * @author Michael Borgwardt
 *
 */
class AbstractParserTest {
    private static final File DIR = new File(".")
    
    protected LogParser parser
    protected byte[] unparseable
    protected byte[] empty
    protected byte[] oneWaypoint
    protected String[] matchFilenames
    private noMatchFilenames = [".hoodyhoo.", "xml", " "]
    
    public AbstractParserTest(LogParser parser) {
        this.parser = parser
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullStream() {
        parser.parse(null)
    }
    
    @Test(expected = ParserException.class)
    public void unparseableStream() {
        parser.parse(new ByteArrayInputStream(unparseable)).next()
    }
    
    @Test
    public void oneWaypoint() {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        assertTrue(it.hasNext())
        def wp = it.next()
        assertEquals(DateTimeZone.UTC, wp.timestamp.zone)
        assertNotNull(wp.timestamp)
        assertNotNull(wp.latitude)
        assertNotNull(wp.longitude)
        assertFalse(it.hasNext())
    }
    
    @Test(expected = NoSuchElementException.class)
    public void beyondEnd() {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        it.next()
        it.next()
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        it.next()
        it.remove()
    }
    
    @Test
    public void filter() {
        for(name in matchFilenames) {
            assertTrue(name, parser.accept(DIR, name))
        }
        for(name in noMatchFilenames) {
            assertFalse(name, parser.accept(DIR, name))
        }
    }
}

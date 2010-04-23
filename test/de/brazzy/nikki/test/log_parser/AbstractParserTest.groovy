package de.brazzy.nikki.test.log_parser;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import de.brazzy.nikki.util.log_parser.LogParser;
import de.brazzy.nikki.util.log_parser.ParserException;

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

import static org.junit.Assert.*;

/**
 * @author Michael Borgwardt
 *
 */
class AbstractParserTest
{
    protected LogParser parser
    protected byte[] unparseable
    protected byte[] empty
    protected byte[] oneWaypoint
    protected String[] matchFilenames
    private noMatchFilenames = [".hoodyhoo.", "xml", " "]
    
    public AbstractParserTest(LogParser parser)
    {
        this.parser = parser
    }
                                
    @Test(expected = IllegalArgumentException.class)
    public void nullStream()
    {
        parser.parse(null)
    }
    
    @Test(expected = ParserException.class)
    public void unparseableStream()
    {
        parser.parse(new ByteArrayInputStream(unparseable))
    }

    @Test
    public void oneWaypoint()
    {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        assertTrue(it.hasNext())
        def wp = it.next()
        assertEquals(DateTimeZone.UTC, wp.timestamp.zone)
        assertNotNull(wp.timestamp)
        assertNotNull(wp.latitude)
        assertNotNull(wp.longitude)
        assertFalse(it.next)
    }
    
    @Test(expected = NoSuchElementException.class)
    public void beyondEnd()
    {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        it.next()
        it.next()
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void remove()
    {
        def it = parser.parse(new ByteArrayInputStream(oneWaypoint))
        it.next()
        it.remove()
    }
    
    @Test
    public void filter()
    {
        def filter = parser.getParseableFileNameFilter()
        assertNotNull(filter)
        for(name in matchFilenames)
        {
            assertTrue(name, filter.accept(name))
        }
        for(name in noMatchFilenames)
        {
            assertFalse(name, filter.accept(name))
        }
    }
}

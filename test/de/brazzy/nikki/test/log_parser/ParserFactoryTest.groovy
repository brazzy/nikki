package de.brazzy.nikki.test.log_parser

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
import groovy.mock.interceptor.MockFor;

import java.io.File;

import org.junit.Test;

import de.brazzy.nikki.util.log_parser.LogParser;
import de.brazzy.nikki.util.log_parser.ParserFactory;

/**
 * @author Michael Borgwardt
 *
 */
class ParserFactoryTest {
    private static final File DIR = new File(".")
    
    ParserFactory factory = new ParserFactory()
    
    @Test
    void noParsers() {
        assertEquals([:], factory.findParsers(DIR, ["file.xml", "file"] as String[]))
    }
    
    @Test
    void noFiles() {
        def mock = new MockFor(LogParser)
        def mockParser1 = mock.proxyDelegateInstance()
        def mockParser2 = mock.proxyDelegateInstance()
        
        factory.parsers = [mockParser1, mockParser2]
        assertEquals([:], factory.findParsers(DIR, new String[0]))
        mock.verify(mockParser1)
        mock.verify(mockParser2)
    }
    
    @Test
    void multipleParsers() {
        def mock1 = new MockFor(LogParser)
        mock1.demand.accept(4..4){ dir, name -> print name; name.endsWith(".xml") }
        def mockParser1 = mock1.proxyDelegateInstance()
        
        def mock2 = new MockFor(LogParser)
        mock2.demand.accept(4..4){ dir, name -> print name; name.endsWith(".nmea") || name.endsWith(".xyz") }
        def mockParser2 = mock2.proxyDelegateInstance()
        
        factory.parsers = [mockParser1, mockParser2]
        assertEquals(["file.xml":mockParser1, "file.nmea":mockParser2, "a.xyz":mockParser2], 
                factory.findParsers(DIR, ["file.xml", "file", "file.nmea", "a.xyz"] as String[]))
        mock1.verify(mockParser1)
        mock2.verify(mockParser2)
    }    
}


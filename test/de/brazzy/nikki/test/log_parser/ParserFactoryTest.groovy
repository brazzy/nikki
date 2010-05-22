package de.brazzy.nikki.test.log_parser

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
import groovy.mock.interceptor.MockFor;

import java.io.File;

import org.junit.Test;

import de.brazzy.nikki.util.log_parser.LogParser;
import de.brazzy.nikki.util.log_parser.ParserFactory;

/**
 * @author Michael Borgwardt
 *
 */
class ParserFactoryTest
{
    private static final File DIR = new File(".")
    
    ParserFactory factory = new ParserFactory()

    @Test
    void noParsers()
    {
        assertEquals([:], factory.findParsers(DIR, ["file.xml", "file"] as String[]))
    }
    
    @Test
    void noFiles()
    {
        def mock = new MockFor(LogParser)
        def mockParser1 = mock.proxyDelegateInstance()
        def mockParser2 = mock.proxyDelegateInstance()
        
        factory.parsers = [mockParser1, mockParser2]
        assertEquals([:], factory.findParsers(DIR, new String[0]))
        mock.verify(mockParser1)
        mock.verify(mockParser2)
    }
    
    @Test
    void multipleParsers()
    {
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


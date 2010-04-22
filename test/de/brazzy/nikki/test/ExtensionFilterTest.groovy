package de.brazzy.nikki.test;

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

import de.brazzy.nikki.util.log_parser.ExtensionFilter 
import org.junit.Test;

/**
 * @author Michael Borgwardt
 *
 */
class ExtensionFilterTest
{
    private static final File FILE = new File(".")

    @Test
    public void padding() {
        assert ["x", "y"]*.padLeft(2, ".") == [".x", ".y"]
        assert [".x", ".y"]*.toUpperCase() == [".X", ".Y"]
        assert ["x", "y"]*.padLeft(2, ".")*.toUpperCase() == [".X", ".Y"]
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNoExtensions() {
        def filter = new ExtensionFilter(new String[0])
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNull() {
        def filter = new ExtensionFilter(null as String[])        
    }

    @Test(expected = IllegalArgumentException.class)
    public void extensionNull() {
        def filter = new ExtensionFilter([null] as String[])
    }

    @Test
    public void nullDirectory() {
        def filter = new ExtensionFilter("x")
        assertTrue(filter.accept(null, "file.x"))        
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullName() {
        def filter = new ExtensionFilter("x")
        filter.accept(FILE, null)        
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName() {
        def filter = new ExtensionFilter("x")
        filter.accept(FILE, "")
    }

    @Test
    public void strangeNames() {
        def filter = new ExtensionFilter("x")
        assertFalse(filter.accept(FILE, "x"))
        assertFalse(filter.accept(FILE, "X"))
        assertFalse(filter.accept(FILE, "x."))
        assertFalse(filter.accept(FILE, "."))
        assertFalse(filter.accept(FILE, "\n"))
        assertFalse(filter.accept(FILE, " "))
        assertFalse(filter.accept(FILE, ".."))
    }


    @Test
    public void multipleMatch() {
        def filter = new ExtensionFilter("x", "y")
        assertTrue(filter.accept(FILE, "file.x"))
        assertTrue(filter.accept(FILE, "file.y"))
        assertFalse(filter.accept(FILE, "file.z"))
    }


    @Test
    public void multipleDots() {
        def filter = new ExtensionFilter("x")
        assertTrue(filter.accept(FILE, "file.y.x"))
        assertFalse(filter.accept(FILE, "file.x.y"))        
        
    }

    @Test
    public void longExtension() {
        def filter = new ExtensionFilter("x12345", "ab")
        assertTrue(filter.accept(FILE, "file.x12345"))
        assertTrue(filter.accept(FILE, "x.ab"))
        assertFalse(filter.accept(FILE, "a.x"))        
    }

    @Test
    public void caseInsensitive() {
        def filter = new ExtensionFilter("x", "Y")
        assertTrue(filter.accept(FILE, "file.X"))
        assertTrue(filter.accept(FILE, "file.y"))
    }
}

package de.brazzy.nikki.test;

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

import de.brazzy.nikki.util.log_parser.ExtensionFilter 
import org.junit.Test;

/**
 * @author Michael Borgwardt
 *
 */
class ExtensionFilterTest {
    private static final File DIR = new File(".")
    
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
        filter.accept(DIR, null)        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void emptyName() {
        def filter = new ExtensionFilter("x")
        filter.accept(DIR, "")
    }
    
    @Test
    public void strangeNames() {
        def filter = new ExtensionFilter("x")
        assertFalse(filter.accept(DIR, "x"))
        assertFalse(filter.accept(DIR, "X"))
        assertFalse(filter.accept(DIR, "x."))
        assertFalse(filter.accept(DIR, "."))
        assertFalse(filter.accept(DIR, "\n"))
        assertFalse(filter.accept(DIR, " "))
        assertFalse(filter.accept(DIR, ".."))
    }
    
    
    @Test
    public void multipleMatch() {
        def filter = new ExtensionFilter("x", "y")
        assertTrue(filter.accept(DIR, "file.x"))
        assertTrue(filter.accept(DIR, "file.y"))
        assertFalse(filter.accept(DIR, "file.z"))
    }
    
    
    @Test
    public void multipleDots() {
        def filter = new ExtensionFilter("x")
        assertTrue(filter.accept(DIR, "file.y.x"))
        assertFalse(filter.accept(DIR, "file.x.y"))        
        
    }
    
    @Test
    public void longExtension() {
        def filter = new ExtensionFilter("x12345", "ab")
        assertTrue(filter.accept(DIR, "file.x12345"))
        assertTrue(filter.accept(DIR, "x.ab"))
        assertFalse(filter.accept(DIR, "a.x"))        
    }
    
    @Test
    public void caseInsensitive() {
        def filter = new ExtensionFilter("x", "Y")
        assertTrue(filter.accept(DIR, "file.X"))
        assertTrue(filter.accept(DIR, "file.y"))
    }
}

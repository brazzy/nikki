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
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Matches any file that has one of the given extensions, for
 * use by LogParser implementations
 * 
 * @author Michael Borgwardt
 */
public class ExtensionFilter implements FilenameFilter {
    /**
     * @param extensions files with these extensions will be accepted
     */
    public ExtensionFilter(String... extensions) {
        super();
        if(!extensions || Arrays.asList(extensions).contains(null)){
            throw new IllegalArgumentException("No extensions: "+extensions);
        }
        this.extensions = extensions.collect{ 
            it.padLeft(it.length()+1, ".").toUpperCase() 
        }
    }
    
    private List<String> extensions;
    
    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name) {
        if(!name){
            throw new IllegalArgumentException("No name: "+name)
        }
        for(String e : extensions) {
            if(name.length() < e.length()){
                continue
            }
            if(name.toUpperCase().endsWith(e)) {
                return true
            }
        }
        return false
    }
    
}
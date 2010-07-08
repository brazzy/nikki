package de.brazzy.nikki.util.log_parser
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


/**
 * Finds parsers for GPS log files
 * 
 * @author Michael Borgwardt
 */
class ParserFactory {
    
    Collection<LogParser> parsers = [];
    
    public Map<String, LogParser> findParsers(File dir, String[] fileNames) {
        def result = [:]
        for(name in fileNames) {
            for(parser in parsers){
                if(parser.accept(dir, name)) {
                    result[name] = parser
                }
            }            
        }
        return result
    }
}

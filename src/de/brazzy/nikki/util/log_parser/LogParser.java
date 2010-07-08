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
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Iterator;

import de.brazzy.nikki.model.Waypoint;

/**
 * A parser for a GPS logger file format. The inherited
 * {@link #accept(java.io.File, String)} method should accept files that this
 * parser can parse
 * 
 * @author Michael Borgwardt
 */
public interface LogParser extends FilenameFilter {
    /**
     * @param input
     *            stream to read from
     * @return iterator over all waypoints in the file
     * @throws ParserException
     *             when the file's contents are not parseable. This can also be
     *             thrown by the iterator
     */
    public Iterator<Waypoint> parse(InputStream input) throws ParserException;
}

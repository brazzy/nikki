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

/**
 * Signals that something went wrong while parsing a GPS log file
 * 
 * @author Michael Borgwardt
 */
public class ParserException extends RuntimeException {
    /**
     * @param message
     *            should contain the part of the file that could not be parsed
     * @param cause
     *            exception that happened during parsing
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            should contain the part of the file that could not be parsed
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * @param cause
     *            exception that happened during parsing
     */
    public ParserException(Throwable cause) {
        super(cause);
    }

}

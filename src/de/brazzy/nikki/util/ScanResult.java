package de.brazzy.nikki.util;

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
 * Describes the result of scanning a directory for image and GPS track files
 * 
 * @author Michael Borgwardt
 */
public enum ScanResult {
    /**
     * Scan was completed without requiring any data from the user
     */
    COMPLETE,

    /**
     * Scan was not completed because there were images with no time zone in
     * their EXIF data.
     */
    TIMEZONE_MISSING
}

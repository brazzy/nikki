package de.brazzy.nikki.model
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

import de.brazzy.nikki.util.PropertyComparator;
import de.brazzy.nikki.util.Texts;


/**
 * Provides options for sorting the image list
 */
public enum ImageSortField {
    FILENAME(Texts.Image.ORDERED_BY_FILENAME, "fileName", null),
    TIME(Texts.Image.ORDERED_BY_TIME,"time","fileName")
    
    private ImageSortField(description, field, secondary){
        this.comparator = new PropertyComparator(
        propertyName: field, secondary: secondary)
        this.description = description
    }
    
    def comparator
    def description
    
    public String toString(){
        description
    }
}

package de.brazzy.nikki.util
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

import java.io.Serializable
import java.util.Comparator

/**
 * Compares objects on a given property, 
 * optionally using a second one to break ties
 * 
 * @author Michael Borgwardt
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable {
    public static final long serialVersionUID = 1L
    
    String propertyName
    String secondary
    
    @Override
    public int compare(T o1, T o2) {
        int result = o1."$propertyName".compareTo(o2."$propertyName")
        if(result == 0 && secondary) {
            result = o1."$secondary".compareTo(o2."$secondary")            
        }
        return result
    }
    
    @Override
    public boolean equals(Object o){
        return o instanceof PropertyComparator && 
        o.propertyName == this.propertyName &&
        o.secondary == this.secondary
    }
    
    @Override
    public int hashCode() {
        int result =propertyName.hashCode() 
        if(secondary){
            result ^= secondary.hashCode()
        }
        return result
    }
    
}

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

/**
 * Represents one dimension (latitude or longitude) of a GPS coordinate
 * 
 * @author Michael Borgwardt
 */
public class GeoCoordinate implements Serializable{
    public static final long serialVersionUID = 1;
    
    /**
     * Cardinal direction of this dimension (determines sign)
     */
    Cardinal direction;
    
    /**
     * Unsigned magnitude of this dimension
     */
    double magnitude;
    
    /**
     * @return Signed magnitude of this dimension
     */
    public double getValue() {
        return magnitude * direction.sign
    }
    
    public String toString() {
        return String.valueOf(getValue()) + " " +direction;
    }
}

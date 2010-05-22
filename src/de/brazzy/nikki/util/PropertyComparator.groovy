package de.brazzy.nikki.util
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

import java.io.Serializable
import java.util.Comparator

/**
 * Compares objects on a given property, 
 * optionally using a second one to break ties
 * 
 * @author Michael Borgwardt
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable
{
    public static final long serialVersionUID = 1L

    String propertyName
    String secondary
    
    @Override
    public int compare(T o1, T o2)
    {
        int result = o1."$propertyName".compareTo(o2."$propertyName")
        if(result == 0 && secondary)
        {
            result = o1."$secondary".compareTo(o2."$secondary")            
        }
        return result
    }
}

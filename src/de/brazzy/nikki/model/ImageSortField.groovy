package de.brazzy.nikki.model
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

import de.brazzy.nikki.Texts 
import de.brazzy.nikki.util.PropertyComparator;


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
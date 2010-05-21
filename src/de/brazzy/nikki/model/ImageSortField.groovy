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


/**
 * Provides options for sorting the image list
 */
enum ImageSortField {
    FILENAME(Image.metaClass.fileName, Texts.Image.ORDERED_BY_FILENAME),
    TIME(Image.metaClass.time, Texts.Image.ORDERED_BY_TIME)
    
    private ImageSortField(field, name){
        this.field = field
        this.name = name
    }
    
    def field
    def name

    public String toString(){
        name
    }
}

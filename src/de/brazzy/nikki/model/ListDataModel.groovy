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

import java.util.Collections
import javax.swing.AbstractListModel 

/**
 * Base class for domain objects to be displayed in a sorted list on the GUI.
 * 
 * @author Michael Borgwardt
 *
 * @param <T> type to display in list - must be Comparable if
 *        no comparator is supplied during construction
 */
public class ListDataModel<T> 
extends AbstractListModel implements Iterable<T> {
    public static final long serialVersionUID = 1
    private static DEFAULT_COMPARATOR = {o1, o2 -> o1.compareTo(o2)
    } as Comparator
    
    /** Contains the elements, kept sorted automatically */
    protected List<T> dataList = new ArrayList<T>()
    
    /** Used to implement the list ordering */
    def comparator = DEFAULT_COMPARATOR
    public void setComparator(Comparator comp){
        if(comp != this.comparator){
            if(comp){
                this.comparator = comp
            }else{
                this.comparator = DEFAULT_COMPARATOR            
            }
            dataList.sort(this.comparator)            
        }
    }
    
    /**
     * Adds new element at the appropriate place in the sort order
     */
    public void add(T d) {
        if(!d) {
            throw new IllegalArgumentException("must not be null!")
        }
        int index = Collections.binarySearch(dataList, d, this.comparator)
        def size = dataList.size()
        
        if(index >= 0) {
            throw new IllegalArgumentException("Already present!")
        }
        else if(-index-1 == size) {
            dataList.add(d)
            fireIntervalAdded(this, size, size)
        }
        else {
            dataList.add(-index-1, d)
            fireIntervalAdded(this, -index-1, -index-1)
        }
    }
    
    public boolean remove(T d) {
        def index = dataList.indexOf(d)
        if(index >= 0) {
            dataList.remove(d)
            fireIntervalRemoved(this, index, index)
            return true
        }
        return false
    }
    
    public boolean contains(T d) {
        dataList.contains(d)
    }
    
    public List<T> asList(){
        return Collections.unmodifiableList(dataList);
    }
    
    @Override
    public Iterator<T> iterator() {
        return dataList.iterator()
    }
    
    
    @Override
    int getSize() {
        dataList.size()
    }    
    
    @Override
    T getElementAt(int index) {
        getAt(index)
    }
    T getAt(int index) {
        dataList[index]
    }
    
}

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

import java.util.Collections;
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
extends AbstractListModel implements Iterable<T>
{
    public static final long serialVersionUID = 1
    private static DEFAULT_COMPARATOR = {o1, o2 -> o1.compareTo(o2)} as Comparator<T>

    /** Contains the elements, kept sorted automatically */
    protected List<T> dataList = new ArrayList<T>()

    /** Used to implement the list ordering */
    Comparator<? super T> comparator = DEFAULT_COMPARATOR
    public void setComparator(Comparator<? super T> comp){
        if(comp){
            this.comparator = comp
        }else{
            comparator = DEFAULT_COMPARATOR            
        }
        dataList.sort(comparator)
    }

    /**
     * Adds new element at the appropriate place in the sort order
     */
    public void add(T d)
    {
        if(!d)
        {
            throw new IllegalArgumentException("must not be null!")
        }
        int index = Collections.binarySearch(dataList, d, this.comparator)
        def size = dataList.size()
        
        if(index >= 0)
        {
            throw new IllegalArgumentException("Already present!")
        }
        else if(-index-1 == size)
        {
            dataList.add(d)
            fireIntervalAdded(this, size, size)
        }
        else
        {
            dataList.add(-index-1, d)
            fireIntervalAdded(this, -index-1, -index-1)
        }
    }
    
    public boolean remove(T d)
    {
        def index = dataList.indexOf(d)
        if(index >= 0)
        {
            dataList.remove(d)
            fireIntervalRemoved(this, index, index)
            return true
        }
        return false
    }
    
    public boolean contains(T d)
    {
        dataList.contains(d)
    }
    
    public List<T> asList(){
        return Collections.unmodifiableList(dataList);
    }
    
    @Override
    public Iterator<T> iterator()
    {
        return dataList.iterator()
    }
    

    @Override
    int getSize()
    {
        dataList.size()
    }    
    
    @Override
    T getElementAt(int index)
    {
        getAt(index)
    }
    T getAt(int index)
    {
        dataList[index]
    }

}

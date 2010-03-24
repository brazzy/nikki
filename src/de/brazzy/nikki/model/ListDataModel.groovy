package de.brazzy.nikki.model

import javax.swing.AbstractListModel
import java.io.ObjectInput
import java.lang.ClassNotFoundException
import java.io.IOException
import java.io.ObjectOutput

public class ListDataModel<T extends Comparable> extends AbstractListModel
{
    public static final long serialVersionUID = 1;

    protected List<T> dataList = new ArrayList<T>()

    public void add(T d)
    {
        if(!d)
        {
            throw new IllegalArgumentException("must not be null!");
        }
        int index = Collections.binarySearch(dataList, d)
        def lastIndex = dataList.size();
        
        if(index > 0)
        {
            throw new IllegalArgumentException("Already present!");            
        }
        else if(-index-1 == lastIndex)
        {
            dataList.add(d)
            fireIntervalAdded(this, lastIndex, lastIndex)
        }
        else
        {
            dataList.add(-index-1, d)
            fireIntervalAdded(this, -index-1, -index-1)
        }
    }
    public boolean remove(T d)
    {
        def index = dataList.indexOf(d);
        if(index >= 0)
        {
            dataList.remove(d)
            fireIntervalRemoved(this, index, index)
            return true;
        }
        return false;
    }
    
    public boolean contains(T d)
    {
        dataList.contains(d)
    }
    int getSize()
    {
        dataList.size()
    }    
    T getElementAt(int index)
    {
        getAt(index)
    }    
    T getAt(int index)
    {
        dataList[index]
    }

}

package de.brazzy.nikki.model

import javax.swing.AbstractListModel
import java.io.ObjectInputimport java.lang.ClassNotFoundExceptionimport java.io.IOExceptionimport java.io.ObjectOutputimport java.io.ObjectInputpublic class ListDataModel<T> extends AbstractListModel
{
    public static final long serialVersionUID = 1;

    protected List<T> data = [];

    public void add(T d)
    {
        if(!d)
        {
            throw new IllegalArgumentException("must not be null!");
        }
        data.add(d)
        fireIntervalAdded(this, data.size()-1, data.size()-1)
    }
    public boolean remove(T d)
    {
        def index = data.indexOf(d);
        if(index >= 0)
        {
            data.remove(d)
            fireIntervalRemoved(this, index, index)
            return true;
        }
        return false;
    }
    
    
    int getSize()
    {
        return data.size()
    }    
    T getElementAt(int index)
    {
        return data[index]
    }
}

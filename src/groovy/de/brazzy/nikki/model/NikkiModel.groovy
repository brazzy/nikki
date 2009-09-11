package de.brazzy.nikki.model

import javax.swing.AbstractListModel

/**
 * @author Michael Borgwardt
 *
 */
public class NikkiModel extends AbstractListModel{
    
    private List<Directory> directories = [];

    public void addDirectory(Directory d)
    {
        if(!d)
        {
            throw new IllegalArgumentException("must not be null!");
        }
        directories.add(d)
        fireIntervalAdded(this, directories.size()-1, directories.size()-1)
    }
    public boolean removeDirectory(Directory d)
    {
        def index = directories.indexOf(d);
        if(index >= 0)
        {
            directories.remove(d)
            fireIntervalRemoved(this, index, index)
            return true;
        }
        return false;
    }
    
    
    int getSize()
    {
        return directories.size()
    }    
    Object getElementAt(int index)
    {
        return directories[index]
    }

}

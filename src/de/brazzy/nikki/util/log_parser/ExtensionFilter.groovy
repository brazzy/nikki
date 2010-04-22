package de.brazzy.nikki.util.log_parser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Matches any file that has one of the given extensions, for
 * use by LogParser implementations
 * 
 * @author Michael Borgwardt
 */
public class ExtensionFilter implements FilenameFilter
{
    /**
     * @param extensions files with these extensions will be accepted
     */
    private ExtensionFilter(String... extensions)
    {
        super();
        if(!extensions || Arrays.asList(extensions).contains(null)){
            throw new IllegalArgumentException("No extensions: "+extensions);
        }
        this.extensions = extensions.collect{ 
            it.padLeft(it.length()+1, ".").toUpperCase() 
        }
    }

    private List<String> extensions;

    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name)
    {
        if(!name){
            throw new IllegalArgumentException("No name: "+name)
        }
        for(String e : extensions)
        {
            if(name.length() < e.length()){
                continue
            }
            if(name.toUpperCase().endsWith(e)) 
            {
                return true
            }
        }
        return false
    }

}
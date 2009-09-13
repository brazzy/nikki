package de.brazzy.nikki.model

import java.util.prefs.Preferences
/**
 * @author Michael Borgwardt
 *
 */
public class NikkiModel extends ListDataModel<Directory>{    
    public static final String PREF_KEY_DIRECTORIES = "directories"
    public static final String PREF_KEY_SELECTION_DIR = "selectionDir"
    public static final String SEP = System.getProperty("path.separator")
    
    static Preferences prefs = Preferences.userNodeForPackage(NikkiModel.class)
    
    File selectionDir
    public void setSelectionDir(File f)
    {
        Preferences.userNodeForPackage(this.class).put(PREF_KEY_SELECTION_DIR, f.absolutePath)
        this.selectionDir = f;
    }

    public NikkiModel()
    {
        selectionDir = new File(prefs.get(PREF_KEY_SELECTION_DIR, null))        
        def dirs = prefs.get(PREF_KEY_DIRECTORIES, null);
        if(dirs)
        {
            dirs = dirs.split(SEP)
            dirs.each{
                if(it && it.length()>0)
                {
                    super.add(new Directory(path: new File(it)))
                }
            }            
        }
    }
    
    
    public void add(Directory d)
    {
        super.add(d)
        def dirs = prefs.get(PREF_KEY_DIRECTORIES, SEP);
        prefs.put(PREF_KEY_DIRECTORIES, dirs+d.path.absolutePath+SEP)
    }
    public boolean remove(Directory d)
    {
        def dirs = prefs.get(PREF_KEY_DIRECTORIES, "");
        prefs.put(PREF_KEY_DIRECTORIES, dirs.replace(SEP+d.path.absolutePath+SEP, SEP))
        return super.remove(d)
    }

}

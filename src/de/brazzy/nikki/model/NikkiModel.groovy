package de.brazzy.nikki.model

import java.util.prefs.Preferences

/**
 * @author Michael Borgwardt
 *
 */
public class NikkiModel extends ListDataModel<Directory>{
    public static final long serialVersionUID = 1;

    public static final String PREF_KEY_DIRECTORIES = "directories"
    public static final String PREF_KEY_SELECTION_DIR = "selectionDir"
    public static final String PREF_KEY_EXPORT_DIR = "exportDir"
    public static final String SEP = System.getProperty("path.separator")
   
    static Preferences prefs = Preferences.userNodeForPackage(NikkiModel.class)
    
    File selectionDir
    File exportDir
    boolean usePrefs
    
    public void setSelectionDir(File f)
    {
        if(usePrefs)
        {
            Preferences.userNodeForPackage(this.class).put(PREF_KEY_SELECTION_DIR, f.absolutePath)
        }
        this.selectionDir = f;
    }
    public void setExportDir(File f)
    {
        if(usePrefs)
        {
            Preferences.userNodeForPackage(this.class).put(PREF_KEY_EXPORT_DIR, f.absolutePath)
        }
        this.exportDir = f;
    }

    public NikkiModel(boolean usePrefs)
    {
        this.usePrefs = usePrefs
        selectionDir = new File(prefs.get(PREF_KEY_SELECTION_DIR, System.getProperty("user.dir")))
        exportDir = new File(prefs.get(PREF_KEY_EXPORT_DIR, System.getProperty("user.dir")))
        
        if(usePrefs)
        {
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
    }
    
    
    public void add(Directory d)
    {
        super.add(d)
        if(usePrefs)
        {
            def dirs = prefs.get(PREF_KEY_DIRECTORIES, SEP)
            prefs.put(PREF_KEY_DIRECTORIES, dirs+d.path.absolutePath+SEP)
        }
    }
    public boolean remove(Directory d)
    {
        if(usePrefs)
        {
            def dirs = prefs.get(PREF_KEY_DIRECTORIES, "")
            prefs.put(PREF_KEY_DIRECTORIES, dirs.replace(SEP+d.path.absolutePath+SEP, SEP))
        }
        return super.remove(d)
    }

}

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
    
    File selectionDir
    File exportDir

    Preferences prefs
    
    public void setSelectionDir(File f)
    {
        prefs.put(PREF_KEY_SELECTION_DIR, f.absolutePath)
        this.selectionDir = f;
        prefs.flush()
    }
    public void setExportDir(File f)
    {
        prefs.put(PREF_KEY_EXPORT_DIR, f.absolutePath)
        this.exportDir = f;
        prefs.flush()
    }

    public NikkiModel(Class prefsClass)
    {
        prefs = Preferences.userNodeForPackage(prefsClass)
        selectionDir = new File(prefs.get(PREF_KEY_SELECTION_DIR, System.getProperty("user.dir")))
        exportDir = new File(prefs.get(PREF_KEY_EXPORT_DIR, System.getProperty("user.dir")))
        
        parseDirectories(prefs.get(PREF_KEY_DIRECTORIES, ""));
    }
    
    private parseDirectories(String dirList)
    {
        for(String entry in dirList.split(SEP))
        {
            if(entry.length()>0)
            {
                super.add(new Directory(path: new File(entry)))
            }
        }
    }
    
    
    public void add(Directory d)
    {
        super.add(d)
        def dirs = prefs.get(PREF_KEY_DIRECTORIES, SEP)
        prefs.put(PREF_KEY_DIRECTORIES, dirs+d.path.absolutePath+SEP)
        prefs.flush()
    }
    public boolean remove(Directory d)
    {
        def dirs = prefs.get(PREF_KEY_DIRECTORIES, "")
        prefs.put(PREF_KEY_DIRECTORIES, dirs.replace(SEP+d.path.absolutePath+SEP, SEP))
        prefs.flush()
        return super.remove(d)
    }

}

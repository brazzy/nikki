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

import java.util.prefs.Preferences

/**
 * Root domain object, visible as a list of directories.
 * The list is persisted via the preferences API
 * 
 * @author Michael Borgwardt
 *
 */
public class NikkiModel extends ListDataModel<Directory>{
    public static final long serialVersionUID = 1;

    private static final String SEP = System.getProperty("path.separator")

    /** key used in the Preferences API for the list of directories */
    public static final String PREF_KEY_DIRECTORIES = "directories"

    /** key used in the Preferences API to remember the selectionDir */
    public static final String PREF_KEY_SELECTION_DIR = "selectionDir"
        
    /** key used in the Preferences API to remember the exportDir */
    public static final String PREF_KEY_EXPORT_DIR = "exportDir"
        
    
    /** (FileChooser default) directory from which to add more directories */
    File selectionDir
    
    /** (FileChooser default) directory to which KMZ files are exported */
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

    /**
     * @param prefsClass used for access to the preferences
     */
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

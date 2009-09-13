package de.brazzy.nikki.model

import java.util.prefs.Preferences
/**
 * @author Michael Borgwardt
 *
 */
public class NikkiModel extends ListDataModel<Directory>{    
    File selectionDir = new File(Preferences.userNodeForPackage(this.class).get("selectionDir", null))
    
    void setSelectionDir(File f)
    {
        Preferences.userNodeForPackage(this.class).put("selectionDir", f.getAbsolutePath())
        this.selectionDir = f;
    }
}

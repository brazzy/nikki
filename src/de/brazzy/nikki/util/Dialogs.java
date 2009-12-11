/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.brazzy.nikki.util;

import java.io.File;
import java.util.TimeZone;

/**
 *
 * @author Brazil
 */
public interface Dialogs {
    public File askDirectory(File startDir);
    public File askFile(File dir, String defaultFileName);
    public Integer askOffset();
    public TimeZone askTimeZone(TimeZone defaultZone);
}

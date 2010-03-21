package de.brazzy.nikki.util;

/**
 * Describes the result of scanning a directory for image
 * and GPS track files
 * 
 * @author Michael Borgwardt
 */
public enum ScanResult
{
    /**
     * Scan was completed without requiring any data from the user
     */
    COMPLETE, 
    
    /**
     * Scan was not completed because there were images with no
     * time zone in their EXIF data.
     */
    TIMEZONE_MISSING
}

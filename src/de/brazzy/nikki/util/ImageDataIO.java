package de.brazzy.nikki.util;
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

import java.io.File;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

/**
 * Base class for dealing with EXIF headers
 *
 * @author Michael Borgwardt
 */
public abstract class ImageDataIO {
    /** For formatting/parsing timestamps */
    protected static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
    
    /** clear text marker for app-specific EXIF IFD */
    protected static final String ENTRY_NIKKI_CONTENT = 
        "Application-specific data of the Nikki GPS/Photo log tool http://www.brazzy.de/nikki";
    protected static final int ENTRY_NIKKI_INDEX = 1;
    protected static final int ENTRY_TIMEZONE_INDEX = 2;
    protected static final int ENTRY_TITLE_INDEX = 3;
    protected static final int ENTRY_DESCRIPTION_INDEX = 4;
    protected static final int ENTRY_EXPORT_INDEX = 5;

    /** File to read from / write to */
    protected File file;
    
    /** Exif manipulation API */
    protected LLJTran llj;
    
    /** Root EXIF data object */
    protected Exif exifData;

    /** Root EXIF IFD */
    protected IFD mainIFD;
    
    /** Main data EXIF IFD */
    protected IFD exifIFD;

    /** GPS data EXIF IFD */
    protected IFD gpsIFD;

    /** App-specific EXIF IFD */
    protected IFD nikkiIFD;

    /**
     * Read EXIF headers
     * 
     * @param file to read from
     * @param readUpto see {@link LLJTran#read(int, boolean)}
     */
    public ImageDataIO(File file, int readUpto) throws LLJTranException
    {
        this.file = file;
        this.llj = new LLJTran(file);
        llj.read(readUpto, true);
        if(llj.getImageInfo() instanceof Exif)
        {
            this.exifData = (Exif) llj.getImageInfo();            
        }
        if(exifData != null && exifData.getIFDs() != null &&
           exifData.getIFDs().length > 0 && exifData.getIFDs()[0] != null)
        {
            mainIFD = exifData.getIFDs()[0];
            
            if(mainIFD != null && mainIFD.getIFDs() != null)
            {
                gpsIFD = mainIFD.getIFD(Exif.GPSINFO);                
                exifIFD = mainIFD.getIFD(Exif.EXIFOFFSET);
                if(exifIFD != null && exifIFD.getIFDs() != null)
                {
                    this.nikkiIFD = exifIFD.getIFD(Exif.APPLICATIONNOTE);
                    if(this.nikkiIFD != null && !ENTRY_NIKKI_CONTENT.equals(this.nikkiIFD.getEntry(ENTRY_NIKKI_INDEX, 0).getValue(0)))
                    {
                        throw new IllegalArgumentException("Foreign Appnote IFD present");
                    }
                }
            }
        }
    }

}

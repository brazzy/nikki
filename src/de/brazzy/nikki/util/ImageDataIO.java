package de.brazzy.nikki.util;

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
    protected static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
    protected static final String ENTRY_NIKKI_CONTENT = 
        "Application-specific data of the Nikki GPS/Photo log tool http://www.brazzy.de/nikki";
    protected static final int ENTRY_NIKKI = 1;
    protected static final int ENTRY_TIMEZONE = 2;
    protected static final int ENTRY_TITLE = 3;
    protected static final int ENTRY_DESCRIPTION = 4;
    protected static final int ENTRY_EXPORT = 5;

    protected File file;
    protected LLJTran llj;
    protected Exif exifData;
    protected IFD nikkiIFD;
    protected IFD mainIFD;
    protected IFD exifIFD;
    protected IFD gpsIFD;

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
                    if(this.nikkiIFD != null && !ENTRY_NIKKI_CONTENT.equals(this.nikkiIFD.getEntry(ENTRY_NIKKI, 0).getValue(0)))
                    {
                        throw new IllegalArgumentException("Foreign Appnote IFD present");
                    }
                }
            }
        }
    }

}

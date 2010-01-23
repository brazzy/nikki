package de.brazzy.nikki.util;

import java.io.File;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;

/**
 *
 * @author Brazil
 */
public class ImageDataIO {
    protected static final String ENTRY_NIKKI_CONTENT = "Application-specific data of the Nikki GPS/Photo log tool http://www.brazzy.de/nikki";
    protected static final int ENTRY_NIKKI = 1;
    protected static final int ENTRY_TIMEZONE = 2;
    protected static final int ENTRY_TITLE = 3;
    protected static final int ENTRY_DESCRIPTION = 4;
    protected static final int ENTRY_EXPORT = 5;

    protected File file;
    protected LLJTran llj;
    protected Exif metadata;
    protected IFD nikkiIFD;
    protected IFD gpsIFD;
    protected Exception exception;

    public ImageDataIO(File file, int readUpto)
    {
        this.file = file;
        try
        {
            this.llj = new LLJTran(file);
            llj.read(readUpto, true);
            this.metadata = (Exif) llj.getImageInfo();
            if(metadata != null && metadata.getIFDs() != null &&
               metadata.getIFDs().length > 0 && metadata.getIFDs()[0] != null)
            {
                IFD mainIFD = metadata.getIFDs()[0];
                this.gpsIFD = mainIFD.getIFD(Exif.GPSINFO);

                if(mainIFD.getIFD(Exif.EXIFOFFSET) != null)
                {
                    this.nikkiIFD = mainIFD.getIFD(Exif.EXIFOFFSET).getIFD(Exif.APPLICATIONNOTE);
                    if(this.nikkiIFD != null && !ENTRY_NIKKI_CONTENT.equals(this.nikkiIFD.getEntry(ENTRY_NIKKI, 0).getValue(0)))
                    {
                        throw new IllegalArgumentException("Foreign Appnote IFD present");
                    }
                }
            }
        }
        catch(Exception e)
        {
            this.exception = e;
        }
    }

}
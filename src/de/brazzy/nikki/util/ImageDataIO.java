package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;

/**
 * Base class for dealing with EXIF headers
 * 
 * @author Michael Borgwardt
 */
public abstract class ImageDataIO {
    /** For formatting/parsing timestamps */
    protected static final DateTimeFormatter TIME_FORMAT = DateTimeFormat
            .forPattern("yyyy:MM:dd HH:mm:ss");

    /** clear text marker for app-specific EXIF IFD */
    protected static final String ENTRY_NIKKI_CONTENT = "Application-specific data of the Nikki GPS/Photo log tool http://www.brazzy.de/nikki";
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

    /** Thrown while reading headers */
    protected Exception createException;

    /**
     * Read EXIF headers
     * 
     * @param file
     *            to read from
     * @param readUpto
     *            see {@link LLJTran#read(int, boolean)}
     */
    public ImageDataIO(File file, int readUpto) {
        try {
            this.file = file;
            this.llj = new LLJTran(file);
            llj.read(readUpto, true);
            if (llj.getImageInfo() instanceof Exif) {
                this.exifData = (Exif) llj.getImageInfo();
            }
            if (exifData != null && exifData.getIFDs() != null
                    && exifData.getIFDs().length > 0
                    && exifData.getIFDs()[0] != null) {
                mainIFD = exifData.getIFDs()[0];

                if (mainIFD != null && mainIFD.getIFDs() != null) {
                    gpsIFD = mainIFD.getIFD(Exif.GPSINFO);
                    exifIFD = mainIFD.getIFD(Exif.EXIFOFFSET);
                    if (exifIFD != null && exifIFD.getIFDs() != null) {
                        this.nikkiIFD = exifIFD.getIFD(Exif.APPLICATIONNOTE);
                        if (this.nikkiIFD != null
                                && !ENTRY_NIKKI_CONTENT.equals(this.nikkiIFD
                                        .getEntry(ENTRY_NIKKI_INDEX, 0)
                                        .getValue(0))) {
                            throw new IllegalArgumentException(
                                    "Foreign Appnote IFD present");
                        }
                    }
                }
            }
        } catch (Exception e) {
            createException = e;
        }
    }

}

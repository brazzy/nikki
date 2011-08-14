package de.brazzy.nikki.model;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import mediautil.gen.Rational;
import mediautil.image.ImageResources;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import org.apache.commons.io.IOUtils;

import de.brazzy.nikki.util.ImageDataIO;

/**
 * Writes image data to EXIF headers, creating new ones if necessary.
 * 
 * @author Michael Borgwardt
 */
public class ImageWriter extends ImageDataIO {
    private static final byte[] EMPTY_EXIF = readEmptyExif();

    private static byte[] readEmptyExif() {
        try {
            return IOUtils.toByteArray(ImageWriter.class
                    .getResourceAsStream("empty_exif.bin"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image image;

    /**
     * @param img
     *            contains the image data
     * @param directory
     *            contains the actual image file
     */
    public ImageWriter(Image img, File directory) throws LLJTranException {
        super(new File(directory.getPath(), img.getFileName()),
                LLJTran.READ_ALL);

        this.image = img;

        if (exifData == null) {
            llj.addAppx(EMPTY_EXIF, 0, EMPTY_EXIF.length, true);
            exifData = (Exif) llj.getImageInfo();
        }
        if (mainIFD == null) {
            mainIFD = new IFD(0, Exif.UNDEFINED);
            exifData.getIFDs()[0] = mainIFD;
        }
        if (exifIFD == null) {
            exifIFD = new IFD(Exif.EXIFOFFSET, Exif.LONG);
            mainIFD.addIFD(exifIFD);
        }
        if (nikkiIFD == null) {
            nikkiIFD = new IFD(Exif.APPLICATIONNOTE, Exif.LONG);
            nikkiIFD.addEntry(ENTRY_NIKKI_INDEX, new Entry(Exif.ASCII,
                    ENTRY_NIKKI_CONTENT));
            exifIFD.addIFD(nikkiIFD);
        }
        if (img.getWaypoint() != null && gpsIFD == null) {
            gpsIFD = new IFD(Exif.GPSINFO, Exif.LONG);
            mainIFD.addIFD(gpsIFD);
        }
    }

    /**
     * causes all image data to be written to the file's EXIF headers.
     */
    public void saveImage() throws Exception {
        if (createException != null) {
            throw createException;
        }
        writeTitle();
        writeDescription();
        writeTime();
        writeExport();
        writeGPS();
        writeThumbnail();

        File tmpFile = File.createTempFile("nikki", "tmp", new File(file
                .getParent()));
        InputStream fip = new BufferedInputStream(new FileInputStream(file));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(
                tmpFile));
        llj.refreshAppx();
        llj.xferInfo(fip, out, LLJTran.REPLACE, LLJTran.RETAIN);
        llj.freeMemory();
        fip.close();
        out.close();
        if (!file.delete()) {
            throw new IllegalStateException();
        }
        if (!tmpFile.renameTo(file)) {
            throw new IllegalStateException();
        }
    }

    private static Entry utf8Entry(String content) {
        try {
            Entry entry = new Entry(Exif.UNDEFINED);
            byte[] data = content.getBytes("UTF-8");
            for (int i = data.length - 1; i >= 0; i--) {
                entry.setValue(i, Integer.valueOf(data[i]));
            }
            return entry;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Can't happen", ex);
        }
    }

    private void writeTitle() {
        if (image.getTitle() != null) {
            nikkiIFD.addEntry(ENTRY_TITLE_INDEX, utf8Entry(image.getTitle()));
        }
    }

    private void writeDescription() {
        if (image.getDescription() != null) {
            nikkiIFD.addEntry(ENTRY_DESCRIPTION_INDEX, utf8Entry(image
                    .getDescription()));
        }
    }

    private void writeTime() {
        if (image.getTime() != null) {
            String timeString = TIME_FORMAT.print(image.getTime());
            Entry entry = new Entry(Exif.ASCII);
            entry.setValue(0, timeString);
            mainIFD.addEntry(Exif.DATETIME, entry);
            entry = new Entry(Exif.ASCII);
            entry.setValue(0, image.getTime().getZone().getID());
            nikkiIFD.addEntry(ENTRY_TIMEZONE_INDEX, entry);
        }
    }

    private void writeExport() {
        Entry entry = new Entry(Exif.BYTE);
        entry.setValue(0, image.getExport() ? 1 : 0);
        nikkiIFD.addEntry(ENTRY_EXPORT_INDEX, entry);
    }

    private void writeGPS() {
        // Set Latitude
        Waypoint wp = image.getWaypoint();
        if (wp != null) {
            Entry entry = new Entry(Exif.ASCII);
            entry.setValue(0, wp.getLatitude().getDirection().getCharacter());
            gpsIFD.setEntry(Integer.valueOf(Exif.GPSLatitudeRef), 0, entry);

            gpsIFD.setEntry(Integer.valueOf(Exif.GPSLatitude), 0,
                    writeGpsMagnitude(wp.getLatitude().getMagnitude()));

            entry = new Entry(Exif.ASCII);
            entry.setValue(0, wp.getLongitude().getDirection().getCharacter());
            gpsIFD.setEntry(Integer.valueOf(Exif.GPSLongitudeRef), 0, entry);

            gpsIFD.setEntry(Integer.valueOf(Exif.GPSLongitude), 0,
                    writeGpsMagnitude(wp.getLongitude().getMagnitude()));
        }
    }

    /**
     * Creates {@link Entry} containing GPS coordinate as arc degree, minute and
     * second values.
     */
    public static Entry writeGpsMagnitude(double value) {
        Entry entry = new Entry(Exif.RATIONAL);

        double magnitude = Math.abs(value);
        int degrees = (int) magnitude;
        double minutes = (magnitude - degrees) * 60.0;
        double seconds = (minutes - (int) minutes) * 60.0;

        entry.setValue(0, new Rational(degrees, 1));
        entry.setValue(1, new Rational((int) minutes, 1));
        entry.setValue(2, new Rational((float) seconds));

        return entry;
    }

    private void writeThumbnail() throws IOException {
        if (exifData.getThumbnailBytes() == null
                && image.getThumbnail() != null
                && !llj.setThumbnail(image.getThumbnail(), 0, image
                        .getThumbnail().length, ImageResources.EXT_JPG)) {
            throw new IllegalStateException();
        }
    }
}

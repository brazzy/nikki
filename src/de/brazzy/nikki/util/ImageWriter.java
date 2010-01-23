package de.brazzy.nikki.util;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.Waypoint;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mediautil.gen.Rational;
import mediautil.image.ImageResources;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

/**
 *
 * @author Brazil
 */
public class ImageWriter extends ImageDataIO
{
    private Image image;

    public ImageWriter(Image img, File directory)
    {
        super(new File(directory.getPath(), img.getFileName()), LLJTran.READ_ALL);
        this.image = img;

        if(metadata == null)
        {
            llj.addAppx(LLJTran.dummyExifHeader, 0,
                        LLJTran.dummyExifHeader.length, true);
            metadata = (Exif)llj.getImageInfo();
            IFD mainIFD = metadata.getIFDs()[0];
            IFD exifIFD = mainIFD.getIFD(Exif.EXIFOFFSET);
            nikkiIFD = exifIFD.getIFD(Exif.APPLICATIONNOTE);
            gpsIFD = mainIFD.getIFD(Exif.GPSINFO);
        }
        if (nikkiIFD == null)
        {
            nikkiIFD = new IFD(Exif.APPLICATIONNOTE, Exif.LONG);
            nikkiIFD.addEntry(ENTRY_NIKKI, new Entry(Exif.ASCII, ENTRY_NIKKI_CONTENT));
            metadata.getIFDs()[0].getIFD(Exif.EXIFOFFSET).addIFD(nikkiIFD);
        }
        if(img.getWaypoint() != null && gpsIFD == null)
        {
            gpsIFD = new IFD(Exif.GPSINFO, Exif.LONG);
            metadata.getIFDs()[0].addIFD(gpsIFD);
        }
    }

    public void saveImage() throws IOException
    {
        try {
            writeTitle();
            writeDescription();
            writeTimezone();
            writeExport();
            writeGPS();
            writeThumbnail();

            File tmpFile = File.createTempFile("nikki", "tmp", new File(file.getParent()));
            InputStream fip = new BufferedInputStream(new FileInputStream(file));
            OutputStream out = new BufferedOutputStream(
                                    new FileOutputStream(tmpFile));
            llj.refreshAppx();
            llj.xferInfo(fip, out, LLJTran.REPLACE, LLJTran.RETAIN);
            llj.freeMemory();
            fip.close();
            out.close();
            if(!file.delete())
            {
                throw new IllegalStateException();
            }
            if(!tmpFile.renameTo(file))
            {
                throw new IllegalStateException();
            }

        } catch (LLJTranException ex) {
            Logger.getLogger(ImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Entry utf8Entry(String content)
    {
        try {
            Entry entry = new Entry(Exif.UNDEFINED);
            byte[] data = content.getBytes("UTF-8");
            for (int i = data.length - 1; i >= 0; i--) {
                entry.setValue(i, Integer.valueOf(data[i]));
            }
            return entry;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex); // can't happen
        }
    }

    private void writeTitle()
    {
        nikkiIFD.addEntry(ENTRY_TITLE, utf8Entry(image.getTitle()));
    }

    private void writeDescription()
    {
        nikkiIFD.addEntry(ENTRY_DESCRIPTION, utf8Entry(image.getDescription()));
    }

    private void writeTimezone()
    {
        Entry entry = new Entry(Exif.ASCII);
        entry.setValue(0, image.getZone().getID());
        nikkiIFD.addEntry(ENTRY_TIMEZONE, entry);
    }

    private void writeExport()
    {
        Entry entry = new Entry(Exif.BYTE);
        entry.setValue(0, image.getExport() ? 1 : 0);
        nikkiIFD.addEntry(ENTRY_EXPORT, entry);
    }

    private void writeGPS()
    {
        // Set Latitude
        Waypoint wp = image.getWaypoint();
        if(wp != null)
        {
            Entry entry = new Entry(Exif.ASCII);
            entry.setValue(0, wp.getLatitude().getDirection().getCharacter());
            gpsIFD.setEntry(new Integer(Exif.GPSLatitudeRef), 0, entry);
            entry = new Entry(Exif.RATIONAL);
            entry.setValue(0, new Rational((float)wp.getLatitude().getMagnitude()));
            gpsIFD.setEntry(new Integer(Exif.GPSLatitude), 0, entry);

            entry = new Entry(Exif.ASCII);
            entry.setValue(0, wp.getLongitude().getDirection().getCharacter());
            gpsIFD.setEntry(new Integer(Exif.GPSLongitudeRef), 0, entry);
            entry = new Entry(Exif.RATIONAL);
            entry.setValue(0, new Rational((float)wp.getLongitude().getMagnitude()));
            gpsIFD.setEntry(new Integer(Exif.GPSLongitude), 0, entry);
        }
    }

    private void writeThumbnail() throws IOException
    {
        llj.removeThumbnail();
        if(image.getThumbnail() != null && !llj.setThumbnail(image.getThumbnail(), 0, image.getThumbnail().length,
                             ImageResources.EXT_JPG))
        {
            throw new IllegalStateException();
        }
    }
}

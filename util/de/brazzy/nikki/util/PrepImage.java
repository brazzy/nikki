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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import mediautil.gen.Rational;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.IFD;
import mediautil.image.jpeg.LLJTran;

/**
 * Used for trying out EXIF access code and producing image files with specific
 * data for unit tests
 * 
 * @author Michael Borgwardt
 */
public class PrepImage {

    private static void print(IFD ifd, int depth) {
        String indent = "";
        for (int i = 0; i < depth; i++) {
            indent += "    ";
        }
        System.out.println(indent + "----- IFD " + ifd.getTag() + " -----\n");
        @SuppressWarnings("unchecked")
        Set<Map.Entry<Object, Object>> s = ifd.getEntries().entrySet();
        for (Map.Entry<Object, Object> entry : s) {
            System.out.println(indent + entry.getKey().toString() + " => "
                    + entry.getValue());
        }
        if (ifd.getIFDs() != null) {
            for (IFD i : ifd.getIFDs()) {
                print(i, depth + 1);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File f1 = new File(PrepImage.class.getResource("IMG2009-11-11.JPG")
                .toURI());
        File f2 = new File(f1.getParent(), "alt.JPG");

        LLJTran llj;
        Exif e;

        llj = new LLJTran(f1);
        llj.read(LLJTran.READ_HEADER, true);
        e = (Exif) llj.getImageInfo();

        IFD mainIFD = e.getIFDs()[0];
        IFD exifIFD = mainIFD.getIFD(Exif.EXIFOFFSET);
        IFD gpsIfd = mainIFD.getIFD(Exif.GPSINFO);

        IFD data = exifIFD.getIFD(Exif.APPLICATIONNOTE);
        if (data == null) {
            data = new IFD(Exif.APPLICATIONNOTE, Exif.LONG);
            exifIFD.addIFD(data);
        }
        Entry entry;

        if (gpsIfd == null) {
            System.out.println("Gps IFD not found adding..");
            gpsIfd = new IFD(Exif.GPSINFO, Exif.LONG);
            mainIFD.addIFD(gpsIfd);
        }
        // Set Latitude
        entry = new Entry(Exif.ASCII);
        entry.setValue(0, 'N');
        gpsIfd.setEntry(new Integer(Exif.GPSLatitudeRef), 0, entry);
        entry = new Entry(Exif.RATIONAL);
        entry.setValue(0, new Rational(45.5f));
        gpsIfd.setEntry(new Integer(Exif.GPSLatitude), 0, entry);

        // Set Longitude
        entry = new Entry(Exif.ASCII);
        entry.setValue(0, 'W');
        gpsIfd.setEntry(new Integer(Exif.GPSLongitudeRef), 0, entry);
        entry = new Entry(Exif.RATIONAL);
        entry.setValue(0, new Rational(16.5f));
        gpsIfd.setEntry(new Integer(Exif.GPSLongitude), 0, entry);

        entry = new Entry(Exif.ASCII);
        entry
                .setValue(
                        0,
                        "Application-specific data of the Nikki GPS/Photo log tool http://www.brazzy.de/nikki");
        data.addEntry(1, entry);

        entry = new Entry(Exif.ASCII);
        entry.setValue(0, "Australia/North");
        data.addEntry(2, entry); // Timezone

        entry = new Entry(Exif.UNDEFINED);
        byte[] title = "Überschrift".getBytes("UTF-8");
        for (int i = title.length - 1; i >= 0; i--) {
            entry.setValue(i, Integer.valueOf(title[i]));
        }
        data.addEntry(3, entry);

        entry = new Entry(Exif.UNDEFINED);
        byte[] comment = "Kommentar\näöüß".getBytes("UTF-8");
        for (int i = comment.length - 1; i >= 0; i--) {
            entry.setValue(i, Integer.valueOf(comment[i]));
        }
        data.addEntry(4, entry);

        entry = new Entry(Exif.BYTE);
        entry.setValue(0, 1);
        data.addEntry(5, entry); // Export

        InputStream fip = new BufferedInputStream(new FileInputStream(f1));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f2));
        llj.refreshAppx();
        llj.xferInfo(fip, out, LLJTran.REPLACE, LLJTran.RETAIN);
        fip.close();
        out.close();

        llj.freeMemory();
        System.out.println(f2.getPath());

        llj = new LLJTran(f2);
        llj.read(LLJTran.READ_HEADER, true);
        e = (Exif) llj.getImageInfo();
        for (IFD i : e.getIFDs()) {
            print(i, 0);
        }

        Object[] commentValues = mainIFD.getIFD(Exif.EXIFOFFSET).getIFD(
                Exif.APPLICATIONNOTE).getEntry(4, 0).getValues();
        comment = new byte[commentValues.length];
        for (int i = 0; i < commentValues.length; i++) {
            comment[i] = (byte) ((Integer) commentValues[i]).intValue();
        }
        System.out.println("xyzzy");
        System.out.println("found comment " + Exif.USERCOMMENT + ": "
                + new String(comment, "UTF-8"));
        System.out.println("xyzzy");

        /*
         * System.out.println("found comment "+Exif.USERCOMMENT+": "+e.getTagValue
         * (Exif.USERCOMMENT, true));
         * System.out.println("found description "+Exif
         * .IMAGEDESCRIPTION+": "+e.getTagValue(Exif.IMAGEDESCRIPTION,true));
         * System
         * .out.println("found offset "+0x882a+": "+e.getTagValue(0x882a,true));
         */
    }

}

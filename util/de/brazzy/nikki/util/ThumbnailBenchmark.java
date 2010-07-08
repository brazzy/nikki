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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;

import de.brazzy.nikki.util.ImageReader;

/**
 * @author Michael Borgwardt
 * 
 */
public class ThumbnailBenchmark {
    public static void main(String[] args) throws Exception {
        ImageReader r = new ImageReader(new File("C:/tmp/test.JPG"),
                DateTimeZone.UTC);
        long start = System.nanoTime();
        byte[] t = r.scale(150, false, true);
        System.out.println();
        System.out.println("ThumpnailRescaleOp: " + (System.nanoTime() - start)
                / (1000 * 1000 * 1000.0));
        File out = File.createTempFile("thumbnail", ".jpg");
        FileOutputStream stream = new FileOutputStream(out);
        IOUtils.write(t, stream);
        stream.close();
        Desktop.getDesktop().open(out);

        start = System.nanoTime();
        t = r.scale(150, false, false);
        System.out.println();
        System.out.println("ResampleOp: " + (System.nanoTime() - start)
                / (1000 * 1000 * 1000.0));
        out = File.createTempFile("thumbnail", ".jpg");
        stream = new FileOutputStream(out);
        IOUtils.write(t, stream);
        stream.close();
        Desktop.getDesktop().open(out);
    }
}

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
import java.io.FileOutputStream;
import java.io.OutputStream;

import mediautil.image.jpeg.LLJTran;

/**
 * Extracts a JPEG image's EXIF data to a file
 * 
 * @author Michael Borgwardt
 */
public class ExtractExif {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        File image = new File(args[0]);
        File target = new File(args[1]);

        LLJTran llj;

        llj = new LLJTran(image);
        llj.read(LLJTran.READ_HEADER, true);

        byte[] data = new byte[4096];
        llj.getAppx(1, data, 0, llj.getAppxLen(0));

        OutputStream out = new FileOutputStream(target);
        out.write(data, 0, llj.getAppxLen(1));
        out.close();
    }

}

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
import java.io.FileOutputStream;
import java.io.OutputStream;

import mediautil.image.jpeg.LLJTran;

/**
 * Extracts a JPEG image's EXIF data to a file
 * 
 * @author Michael Borgwardt
 */
public class ExtractExif
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
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

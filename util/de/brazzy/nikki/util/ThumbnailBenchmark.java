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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;

/**
 * @author Michael Borgwardt
 *
 */
public class ThumbnailBenchmark
{
    public static void main(String[] args) throws Exception
    {
        ImageReader r = new ImageReader(new File("C:/tmp/test.JPG"), DateTimeZone.UTC);
        long start = System.nanoTime();
        byte[] t = r.scale(150, false, true);        
        System.out.println();
        System.out.println("ThumpnailRescaleOp: "+ (System.nanoTime()-start)/(1000*1000*1000.0));        
        File out = File.createTempFile("thumbnail", ".jpg");
        FileOutputStream stream = new FileOutputStream(out);
        IOUtils.write(t, stream);
        stream.close();
        Desktop.getDesktop().open(out);
        
        start = System.nanoTime();
        t = r.scale(150, false, false); 
        System.out.println();
        System.out.println("ResampleOp: " + (System.nanoTime()-start)/(1000*1000*1000.0));        
        out = File.createTempFile("thumbnail", ".jpg");
        stream = new FileOutputStream(out);
        IOUtils.write(t, stream);
        stream.close();
        Desktop.getDesktop().open(out);
    }
}

package de.brazzy.nikki.test

import java.net.URIimport de.brazzy.nikki.util.ImageReader
import de.brazzy.nikki.model.Image


public class ImageReaderBenchmark{
    public static void main(def args){
        def start = System.currentTimeMillis()
        
        File f = new File(ImageReaderBenchmark.class.getResource("test.jpg").toURI());
        10.times{
            Image i = ImageReader.createImage(f, null)            
        }
        System.out.println("Total time: "+(System.currentTimeMillis()-start))
    }
}

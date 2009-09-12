package de.brazzy.nikki.model;

import org.apache.sanselan.formats.jpeg.JpegImageMetadataimport org.apache.sanselan.Sanselanimport org.apache.sanselan.formats.tiff.constants.TagInfoimport org.apache.sanselan.formats.tiff.constants.TiffConstantsimport org.apache.sanselan.formats.tiff.TiffFieldimport java.io.FilenameFilterimport java.text.SimpleDateFormatimport java.util.HashMapimport javax.imageio.ImageIOimport java.awt.image.BufferedImageimport java.io.ByteArrayOutputStream
import java.awt.Graphics2Dimport java.awt.geom.AffineTransformimport java.text.DateFormatimport java.awt.RenderingHints
import de.brazzy.nikki.util.ImageReaderclass Directory extends ListDataModel<Day>{
    
    List<Image> images = [];
    List<WaypointFile> waypointFiles = [];    
    File path;
    
    public String toString()
    {
        path.name
    }
    
    public void scan()
    {        
        def filter = { dir, name ->
            name.toUpperCase().endsWith(".JPG")
        } as FilenameFilter
        
        def images = path.listFiles(filter)
        
        def days = new HashMap()
        images.each{
            Image image = ImageReader.createImage(it, this)
            
            def date = DateFormat.getDateInstance().parse(image.time.dateString)
            def list = days.get(date)
            if(list)
            {
                list.add(image)
            }
            else
            {
                days.put(date, [image])                    
            }
        }
        
        def toSort = []
        days.each
        { key,value ->
            Day d = new Day(date:key)
            d.images = value
            toSort.add(d)
        }
        
        toSort.sort{ it.date }        
        toSort.each{ add(it) }
    }
    
}

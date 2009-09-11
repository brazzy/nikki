package de.brazzy.nikki.model;

import org.apache.sanselan.formats.jpeg.JpegImageMetadataimport org.apache.sanselan.Sanselanimport org.apache.sanselan.formats.tiff.constants.TagInfoimport org.apache.sanselan.formats.tiff.constants.TiffConstantsimport org.apache.sanselan.formats.tiff.TiffFieldimport java.io.FilenameFilterimport java.text.SimpleDateFormatclass Directory extends ListDataModel<Day>{
    
    List<Image> images = [];
    List<WaypointFile> waypointFiles = [];    
    File path;
    
    public String toString()
    {
        path.name
    }
    
    public void scan()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd")
        
        def filter = { dir, name ->
            name.toUpperCase().endsWith(".JPG")
        } as FilenameFilter
        
        def imgs = path.listFiles(filter)
        
        def days = new TreeMap()
        imgs.each{
            Image i = new Image(directory:this, fileName:it.name)
            JpegImageMetadata md =  Sanselan.getMetadata(it);
            if(md)
            {
                images.add(i)
                
                TiffField f = md.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL)            
                def d = format.parse(f.value.substring(0, 10))
                def list = days.get(d)
                if(list)
                {
                    list.add(i)
                }
                else
                {
                    days.put(d, [i])                    
                }
            }
        }
        
        days.each
        { key,value ->
            Day d = new Day(date:key)
            d.images = value
            add(d)
        }
    }
}

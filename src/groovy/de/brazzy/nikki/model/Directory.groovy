package de.brazzy.nikki.model;

import org.apache.sanselan.formats.jpeg.JpegImageMetadataimport org.apache.sanselan.Sanselanimport org.apache.sanselan.formats.tiff.constants.TagInfoimport org.apache.sanselan.formats.tiff.constants.TiffConstantsimport org.apache.sanselan.formats.tiff.TiffFieldimport java.io.FilenameFilterimport java.text.SimpleDateFormatimport java.util.HashMapclass Directory extends ListDataModel<Day>{
    
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
        
        def images = path.listFiles(filter)
        
        def days = new HashMap()
        images.each{
            Image image = new Image(directory:this, fileName:it.name)
            this.images.add(image)
            def date;
            
            JpegImageMetadata md =  Sanselan.getMetadata(it);
            if(md)
            {                
                TiffField f = md.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL)            
                if(f != null && f.value != null)
                {
                    date = format.parse(f.value.substring(0, 10))
                }                
            }
            
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

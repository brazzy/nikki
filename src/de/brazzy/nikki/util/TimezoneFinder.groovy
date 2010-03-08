package de.brazzy.nikki.util;

import org.joda.time.DateTimeZone;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

class TimezoneFinder {
    RTree tree;
    DateTimeZone[] zones;
    
    public TimezoneFinder(InputStream zoneData)
    {
        this.zones = []
        this.tree = new RTree()
        tree.init(new Properties())
        
        ObjectInputStream data = new ObjectInputStream(zoneData)
        for(def zone=data.readUTF();zone != "";zone=data.readUTF())
        {
            zones.add(zone)
        }
        
        while(true)
        {
            def lat=data.readFloat()
            if(Float.isNaN(lat))
            {
                break
            }
            def lng=data.readFloat()
            def zone=data.readShort()
            tree.add(new Rectangle(lat, lng, (float)lat+0.00001, (float)lng+0.00001), zone)
        }
    }
    
    public DateTimeZone find(float latitude, float longitude)
    {
        def result;
        def point = new Point(latitude, longitude)
        def callback = { result = zones[it]; return false } as IntProcedure
        tree.nearest(point, callback, Float.POSITIVE_INFINITY)
        return result
    }

}

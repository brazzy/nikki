package de.brazzy.nikki.util


import com.infomatiq.jsi.Rectangle;

import java.util.zip.ZipInputStream;

import gnu.trove.TIntObjectHashMap;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.rtree.RTree;

import de.brazzy.nikki.model.GeoCoordinate;

class PrepTimezoneData
{
    /**
     * Converts location data from geonames.org to a compact, quickly parseable
     * binary format:
     * - An arbitrary number of timezone IDs (written by ObjectOutputStream.writeUTF())
     * - One empty String (written by ObjectOutputStream.writeUTF())
     * - An arbitrary number of locations, consisting of
     *   - latitude as (written by ObjectOutputStream.writeFloat())
     *   - longitude as (written by ObjectOutputStream.writeFloat())
     *   - timezone as index into the above list of timezone IDs
     *     (written by ObjectOutputStream.writeShort())
     * - An end-of-file marker consisting of Float.NaN
     *   (written by ObjectOutputStream.writeFloat())
     */
    public static void main(args)
    {
        def zip = new ZipInputStream(new FileInputStream(args[0]))
        def entry = zip.getNextEntry()
        assert entry.name == "cities1000.txt"
        def zones = []
        def entries = []

        zip.eachLine("UTF-8"){ String line ->
            def data = line.split("\t")
            def lat = data[4] as float
            def lng = data[5] as float
            def zone = data[17] 
            def index = zones.indexOf(zone)
            if(index==-1)
            {
                zones.add(zone)
                index = zones.size()-1
            }
            entries.add([lat,lng,index])
        }
        def out = new ObjectOutputStream(new FileOutputStream(args[1]))
        zones.each{
            if(it == "Asia/Kathmandu")
            {
                out.writeUTF("Asia/Katmandu")                
            }
            else
            {
                out.writeUTF(it)
            }
        }
        out.writeUTF("")
        entries.each{
            out.writeFloat(it[0])
            out.writeFloat(it[1])
            out.writeShort(it[2])
        }
        out.writeFloat(Float.NaN)
        out.close()
        
        testRTree(args[1])
    }
    
    public static void testRTree(String file)
    {
        RTree tree = new RTree()
        tree.init(new Properties())
        
        ObjectInputStream data = new ObjectInputStream(new FileInputStream(file))
        def zones = []
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
            tree.add(new Rectangle((float)lat, (float)lng, (float)(lat+0.00001), (float)(lng+0.00001)), zone)
        }
        
        def result;
        tree.nearest(new Point(-24.95867f, 146.138763f), { result = zones[it]; return false } as IntProcedure, Float.POSITIVE_INFINITY)
        assert result == "Australia/Brisbane"
        tree.nearest(new Point(48.110383f, 11.567788f), { result = zones[it]; return false } as IntProcedure, Float.POSITIVE_INFINITY)
        assert result == "Europe/Berlin"
        tree.nearest(new Point(35.543658f, 139.508954f), { result = zones[it]; return false } as IntProcedure, Float.POSITIVE_INFINITY)            
        assert result == "Asia/Tokyo"        
    }
}

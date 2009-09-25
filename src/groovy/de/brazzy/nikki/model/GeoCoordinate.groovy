package de.brazzy.nikki.model

/**
 * @author Michael Borgwardt
 *
 */
public class GeoCoordinate{
    Cardinal direction;
    double magnitude;
    
    public double getValue()
    {
        return magnitude * direction.sign
    }
    
    public static GeoCoordinate parse(String mag, String dir)
    {
        if(dir.length()!=1)
        {
            throw new IllegalArgumentException(dir)
        }
        def spl = mag.tokenize(".")
        int degrees = Integer.parseInt(spl[0].substring(0, spl[0].length()-2))
        double minutes = Double.parseDouble(spl[0].substring(spl[0].length()-2, spl[0].length())+"."+spl[1])
        degrees += minutes / 60.0
        def result = new GeoCoordinate(direction: Cardinal.parse(dir), magnitude: degrees)
        return result;
    }
    
    public String toString()
    {
        return String.valueOf(getValue()) + " " +direction;
    }
}

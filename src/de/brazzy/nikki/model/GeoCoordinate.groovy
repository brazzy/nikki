package de.brazzy.nikki.model

/**
 * Represents one dimension (latitude or longitude) of a GPS coordinate
 * 
 * @author Michael Borgwardt
 */
public class GeoCoordinate implements Serializable{
    public static final long serialVersionUID = 1;

    /**
     * Cardinal direction of this dimension (determines sign)
     */
    Cardinal direction;
    
    /**
     * Unsigned magnitude of this dimension
     */
    double magnitude;
    
    /**
     * @return Signed magnitude of this dimension
     */
    public double getValue()
    {
        return magnitude * direction.sign
    }
    
    /**
     * Parses from NMEA data
     * 
     * @param mag magnitude String
     * @param dir direction String
     * @return parsed result
     */
    public static GeoCoordinate parse(String mag, String dir)
    {
        if(dir.length()!=1)
        {
            throw new IllegalArgumentException(dir)
        }
        def spl = mag.tokenize(".")
        def degrees = Integer.parseInt(spl[0].substring(0, spl[0].length()-2))
        double minutes = Double.parseDouble(spl[0].substring(spl[0].length()-2, spl[0].length())+"."+spl[1])
        degrees += minutes / 60.0d
        def result = new GeoCoordinate(direction: Cardinal.parse(dir), magnitude: degrees)
        return result;
    }
    
    public String toString()
    {
        return String.valueOf(getValue()) + " " +direction;
    }
}

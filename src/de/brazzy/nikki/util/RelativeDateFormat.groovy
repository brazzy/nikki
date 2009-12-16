package de.brazzy.nikki.util

import java.text.SimpleDateFormat


/**
 * Special date formatter that handles the representation of dates
 * using the time zone where photos were taken rather than the
 * current system time zone
 * 
 * @author Michael Borgwardt
 */
public class RelativeDateFormat extends SimpleDateFormat{

    public RelativeDateFormat(TimeZone zone)
    {
        super("yyyy-MM-dd")
        if(zone)
        {
            this.timeZone = zone
        }
    }

    /**
     * Removes time fields, leaving
     * a time of 00:00:00 in the format's time zone
     */
    public Date stripTime(Date d)
    {
        parse(format(d))
    }

    /**
     * Checks whether the two dates are on the same
     * day in this format's time zone
     */
    public boolean sameDay(Date d1, Date d2)
    {
        format(d1).equals(format(d2))
    }
    
}

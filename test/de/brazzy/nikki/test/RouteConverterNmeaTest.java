package de.brazzy.nikki.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

import slash.navigation.nmea.NmeaFormat;
import slash.navigation.nmea.NmeaPosition;
import slash.navigation.nmea.NmeaRoute;

/**
 * @author Michael Borgwardt
 * 
 */
public class RouteConverterNmeaTest {
    @BeforeClass
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Darwin"));
    }

    @Test
    public void timezone() throws Exception {
        NmeaFormat parser = new NmeaFormat();
        NmeaPosition pos = parse("$GPRMC,050904.000,A,2358.2851,S,13312.0840,E,000.00,0.0,111109,,,E*4A");
        assertEquals("GMT", pos.getTime().getTimeZoneId());
    }

    @Test
    public void time() throws Exception {
        NmeaFormat parser = new NmeaFormat();
        NmeaPosition pos = parse("$GPRMC,050904.000,A,2358.2851,S,13312.0840,E,000.00,0.0,111109,,,E*4A");
        Calendar cal = new GregorianCalendar();
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.LONG);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2009);
        cal.set(Calendar.MONTH, 11 - 1);
        cal.set(Calendar.DAY_OF_MONTH, 11);
        cal.set(Calendar.HOUR_OF_DAY, 5);
        cal.set(Calendar.MINUTE, 9);
        cal.set(Calendar.SECOND, 4);
        assertEquals(format.format(cal.getTime()), format.format(pos.getTime()
                .getTime()));
    }

    private static NmeaPosition parse(String data)
            throws UnsupportedEncodingException, IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(data
                .getBytes("US-ASCII"));
        List<NmeaRoute> routes = new NmeaFormat().read(input);
        assertEquals(1, routes.size());
        List<NmeaPosition> positions = routes.get(0).getPositions();
        assertEquals(1, positions.size());
        NmeaPosition pos = positions.get(0);
        return pos;
    }
}

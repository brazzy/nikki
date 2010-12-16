package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Michael Borgwardt
 */
public class ComputeNmeaChecksum {
    public static void main(String[] args) {
        String line = "$GPRMC,153034.000,A,5932.3129,N,01303.3055,E,000.00,0.0,220710,,,E*5d";
        String lineForChecksum = line.substring(1, line.length() - 3);
        byte expected = computeChecksum(lineForChecksum);
        System.out.println(Integer.toHexString(expected & 0xff));
    }

    private static byte computeChecksum(String line) {
        byte result = 0;
        for (int i = 0; i < line.length(); i++) {
            result ^= line.charAt(i);
        }
        return result;
    }
}

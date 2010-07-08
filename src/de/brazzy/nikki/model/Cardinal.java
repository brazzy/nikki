package de.brazzy.nikki.model;

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
 * Represents the cardinal directions
 * 
 * @author Michael Borgwardt
 */
public enum Cardinal {
    NORTH('N', 1), SOUTH('S', -1), EAST('E', 1), WEST('W', -1);

    public static final long serialVersionUID = 1;
    private char character;
    private int sign;

    private Cardinal(char character, int sign) {
        this.character = character;
        this.sign = sign;
    }

    /**
     * Parses a letter (NSEW) into the corresponding Cardinal
     */
    public static Cardinal parse(String c) {
        switch (c.charAt(0)) {
        case 'N':
            return NORTH;
        case 'S':
            return SOUTH;
        case 'E':
            return EAST;
        case 'W':
            return WEST;
        default:
            throw new IllegalArgumentException("Unknown cardinal: " + c);
        }
    }

    /**
     * @return the character corresponding to the first letter in the Cardinal's
     *         name
     */
    public char getCharacter() {
        return character;
    }

    /**
     * @return 1 or -1, depending on whether this cardinal direction has a
     *         positive or negative sign in GPS coordinates
     */
    public int getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}

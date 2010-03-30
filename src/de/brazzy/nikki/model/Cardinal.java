package de.brazzy.nikki.model;
/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/**
 * Represents the cardinal directions
 * 
 * @author Michael Borgwardt
 */
public enum Cardinal
{
    NORTH('N',1),
    SOUTH('S', -1),
    EAST('E', 1),
    WEST('W', -1);
    
    public static final long serialVersionUID = 1;
    private char character;
    private int sign;

    private Cardinal(char character, int sign)
    {
        this.character = character;
        this.sign = sign;
    }

    /**
     * Parses a letter (NSEW) into the corresponding Cardinal
     */
    public static Cardinal parse(String c)
    {
        switch(c.charAt(0))
        {
            case 'N': return NORTH;
            case 'S': return SOUTH;
            case 'E': return EAST;
            case 'W': return WEST;
            default: throw new IllegalArgumentException("Unknown cardinal: "+c);
        }
    }

    /**
     * @return the character corresponding to the first 
     * letter in the Cardinal's name
     */
    public char getCharacter()
    {
        return character;
    }

    /**
     * @return 1 or -1, depending on whether this cardinal direction has a positive or
     * negative sign in GPS coordinates
     */
    public int getSign()
    {
        return sign;
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(character);
    }
}

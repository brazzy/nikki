package de.brazzy.nikki.model;

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

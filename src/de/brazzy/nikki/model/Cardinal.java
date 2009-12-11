package de.brazzy.nikki.model;

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

    public char getCharacter()
    {
        return character;
    }

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

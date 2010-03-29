package de.brazzy.nikki.model;

import mediautil.image.jpeg.LLJTran;

/**
 * Represents image rotation angles 
 * 
 * @author Michael Borgwardt
 */
public enum Rotation
{
    RIGHT(LLJTran.ROT_90), 
    LEFT(LLJTran.ROT_270), 
    ROT180D(LLJTran.ROT_180), 
    NONE(LLJTran.NONE);
    
    private Rotation(int tranConstant)
    {
        this.tranConstant = tranConstant;
    }

    private int tranConstant;

    /**
     * Returns the constant representing the corresponding rotation
     * in {@link LLJTran}.
     */
    public int getLLJTranConstant()
    {
        return tranConstant;
    }
}

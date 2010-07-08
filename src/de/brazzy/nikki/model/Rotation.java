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

import mediautil.image.jpeg.LLJTran;

/**
 * Represents image rotation angles
 * 
 * @author Michael Borgwardt
 */
public enum Rotation {
    RIGHT(LLJTran.ROT_90), LEFT(LLJTran.ROT_270), ROT180D(LLJTran.ROT_180), NONE(
            LLJTran.NONE);

    private Rotation(int tranConstant) {
        this.tranConstant = tranConstant;
    }

    private int tranConstant;

    /**
     * Returns the constant representing the corresponding rotation in
     * {@link LLJTran}.
     */
    public int getLLJTranConstant() {
        return tranConstant;
    }
}

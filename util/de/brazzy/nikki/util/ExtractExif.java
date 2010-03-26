package de.brazzy.nikki.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import mediautil.image.jpeg.LLJTran;

public class ExtractExif
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        File image = new File(args[0]);
        File target = new File(args[1]);

        LLJTran llj;

        llj = new LLJTran(image);
        llj.read(LLJTran.READ_HEADER, true);
        
        byte[] data = new byte[4096];
        llj.getAppx(1, data, 0, llj.getAppxLen(0));
       
        OutputStream out = new FileOutputStream(target);
        out.write(data, 0, llj.getAppxLen(1));
        out.close();
    }

}

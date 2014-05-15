package com.sibilantsolutions.iptools.util;

public abstract class Convert
{

    private Convert() {}    //Prevent instantiation.

    static public long toNum( byte[] bytes, int offset, int length )
    {
        long num = 0;

        final int endIndex = offset + length;

        while ( offset < endIndex )
        {
            char b = (char)( bytes[offset++] & 0xFF );

            num <<= 8;

            num += b;
        }

        return num;
    }

}

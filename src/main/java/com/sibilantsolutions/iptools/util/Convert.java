package com.sibilantsolutions.iptools.util;

import java.nio.ByteOrder;

public abstract class Convert
{

    private Convert() {}    //Prevent instantiation.

    static public long toNum( byte[] bytes, int offset, int length )
    {
        return toNum( bytes, offset, length, ByteOrder.BIG_ENDIAN );
    }

    static public long toNum( byte[] bytes, int offset, int length, ByteOrder byteOrder )
    {
        long num = 0;

        final int endIndex = offset + length;

        if ( byteOrder == ByteOrder.BIG_ENDIAN )
        {
            while ( offset < endIndex )
            {
                char b = (char)( bytes[offset++] & 0xFF );

                num <<= 8;

                num += b;
            }
        }
        else if ( byteOrder == ByteOrder.LITTLE_ENDIAN )
        {
            for ( int i = endIndex - 1; i >= offset; i-- )
            {
                char b = (char)( bytes[i] & 0xFF );

                num <<= 8;

                num += b;
            }
        }
        else
        {
            throw new IllegalArgumentException( "Unexpected byte order=" + byteOrder );
        }

        return num;
    }

}

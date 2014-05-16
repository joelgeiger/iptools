package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import java.nio.ByteOrder;

import org.junit.Test;

public class ConvertTest
{

    @Test
    public void testToNum()
    {
        assertEquals( 0, Convert.toNum( new byte[]{}, 0, 0 ) );
        assertEquals( 0, Convert.toNum( new byte[]{ 0 }, 0, 0 ) );
        assertEquals( 0, Convert.toNum( new byte[]{ 0 }, 0, 1 ) );

        assertEquals( 0, Convert.toNum( new byte[]{ 1 }, 0, 0 ) );
        assertEquals( 1, Convert.toNum( new byte[]{ 1 }, 0, 1 ) );

        assertEquals( 255, Convert.toNum( new byte[]{ (byte)0xFF, (byte)0xFF }, 0, 1 ) );
        assertEquals( 65535, Convert.toNum( new byte[]{ (byte)0xFF, (byte)0xFF }, 0, 2 ) );

        assertEquals( 255, Convert.toNum( new byte[]{ 0, 0, (byte)0xFF, (byte)0xFF }, 2, 1 ) );
        assertEquals( 65535, Convert.toNum( new byte[]{ 0, 0, (byte)0xFF, (byte)0xFF }, 2, 2 ) );


        assertEquals( 18, Convert.toNum( new byte[]{ 0, 0, (byte)0x12, (byte)0x34, 0, 0 }, 2, 1 ) );
        assertEquals( 4660, Convert.toNum( new byte[]{ 0, 0, (byte)0x12, (byte)0x34, 0, 0 }, 2, 2 ) );


        assertEquals( 18, Convert.toNum( new byte[]{ 0, 0, (byte)0x12, (byte)0x34, 0, 0 }, 2, 1, ByteOrder.LITTLE_ENDIAN ) );
        assertEquals( 13330, Convert.toNum( new byte[]{ 0, 0, (byte)0x12, (byte)0x34, 0, 0 }, 2, 2, ByteOrder.LITTLE_ENDIAN ) );
    }

}

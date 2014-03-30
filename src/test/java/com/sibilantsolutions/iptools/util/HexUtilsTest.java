package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexUtilsTest
{

    @Test
    public void testEncodeNum()
    {
        assertEquals( "" + (char)0x00, HexUtils.encodeNum( 0, 1 ) );
        assertEquals( "" + (char)0x0F, HexUtils.encodeNum( 15, 1 ) );
        assertEquals( "" + (char)0x00 + (char)0x0F, HexUtils.encodeNum( 15, 2 ) );
        assertEquals( "" + (char)0xFF + (char)0xFF, HexUtils.encodeNum( 65535, 2 ) );

    }

    @Test
    public void testNumToHex()
    {
        assertEquals(       "00", HexUtils.numToHex( 0 ) );
        assertEquals(       "0F", HexUtils.numToHex( 15 ) );
        assertEquals(       "10", HexUtils.numToHex( 16 ) );
        assertEquals(       "FF", HexUtils.numToHex( 255 ) );
        assertEquals(     "0100", HexUtils.numToHex( 256 ) );
        assertEquals(     "FFFF", HexUtils.numToHex( 65535 ) );
        assertEquals(   "010000", HexUtils.numToHex( 65536 ) );
        assertEquals(   "FFFFFF", HexUtils.numToHex( 16777215 ) );
        assertEquals( "FFFFFFFF", HexUtils.numToHex( 4294967295L ) );
    }

}

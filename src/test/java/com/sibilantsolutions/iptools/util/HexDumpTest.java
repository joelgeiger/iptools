package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexDumpTest
{

    @Test
    public void testComputeLines()
    {
        assertEquals( 1, HexDump.computeLines( 0 ) );
        assertEquals( 1, HexDump.computeLines( 1 ) );
        assertEquals( 1, HexDump.computeLines( 16 ) );
        assertEquals( 2, HexDump.computeLines( 17 ) );
        assertEquals( 2, HexDump.computeLines( 32 ) );
        assertEquals( 3, HexDump.computeLines( 33 ) );
        assertEquals( 256, HexDump.computeLines( 4096 ) );
    }

    @Test
    public void testNumToHex()
    {
        assertEquals(       "00", HexDump.numToHex( 0 ) );
        assertEquals(       "0F", HexDump.numToHex( 15 ) );
        assertEquals(       "10", HexDump.numToHex( 16 ) );
        assertEquals(       "FF", HexDump.numToHex( 255 ) );
        assertEquals(     "0100", HexDump.numToHex( 256 ) );
        assertEquals(     "FFFF", HexDump.numToHex( 65535 ) );
        assertEquals(   "010000", HexDump.numToHex( 65536 ) );
        assertEquals(   "FFFFFF", HexDump.numToHex( 16777215 ) );
        assertEquals( "FFFFFFFF", HexDump.numToHex( 4294967295L ) );
    }
    

    @Test
    public void testNumBytes()
    {
        assertEquals( 1, HexDump.numBytes( 0x00 ) );
        assertEquals( 1, HexDump.numBytes( 0x0F ) );
        assertEquals( 1, HexDump.numBytes( 0x10 ) );
        assertEquals( 1, HexDump.numBytes( 0xFF ) );
        assertEquals( 2, HexDump.numBytes( 0x0100 ) );
        assertEquals( 2, HexDump.numBytes( 0xFFFF ) );
        assertEquals( 3, HexDump.numBytes( 0x010000 ) );
        assertEquals( 3, HexDump.numBytes( 0xFFFFFF ) );
        //assertEquals( 4, HexDump.numBytes( 0xFFFFFFFF ) );
    }
    
    @Test
    public void testPrettyDumpByteArray()
    {
        byte[] data = new byte[] { '1' };

        String s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0001/1\n" +
                      "0000: 31                      |                         || 1", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );

        data = new byte[] { '1', '2', '3' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0003/3\n" +
                      "0000: 31 32 33                |                         || 123", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );


        data = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0010/16\n" +
                      "0000: 31 32 33 34 35 36 37 38 | 39 61 62 63 64 65 66 67 || 123456789abcdefg", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );


        data = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0011/17\n" +
                      "0000: 31 32 33 34 35 36 37 38 | 39 61 62 63 64 65 66 67 || 123456789abcdefg\n" +
                      "0010: 68                      |                         || h", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );


        data = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0020/32\n" +
                      "0000: 31 32 33 34 35 36 37 38 | 39 61 62 63 64 65 66 67 || 123456789abcdefg\n" +
                      "0010: 68 69 6A 6B 6C 6D 6E 6F | 70 71 72 73 74 75 76 77 || hijklmnopqrstuvw", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );
    }

    @Test
    public void testPrettyDumpByteArray_empty()
    {
        String s = HexDump.prettyDump( new byte[0] );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F || 0x0000/0\n" + 
                      "0000:                         |                         || ", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( 0 ) );
    }

    @Test( expected = NullPointerException.class )
    public void testPrettyDumpByteArray_null()
    {
        HexDump.prettyDump( (byte[])null );
    }

}

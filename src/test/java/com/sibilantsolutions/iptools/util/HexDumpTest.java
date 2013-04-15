package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexDumpTest
{

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
    public void testPrettyDumpByteArray()
    {
        byte[] data = new byte[] { '1' };

        String s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||\n" +
                      "0000: 31                      |                         || 1", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );

        data = new byte[] { '1', '2', '3' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||\n" +
                      "0000: 31 32 33                |                         || 123", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );


        data = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||\n" +
                      "0000: 31 32 33 34 35 36 37 38 | 39 61 62 63 64 65 66 67 || 123456789abcdefg", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );
        

        data = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        s = HexDump.prettyDump( data );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||\n" +
                      "0000: 31 32 33 34 35 36 37 38 | 39 61 62 63 64 65 66 67 || 123456789abcdefg\n" +
                      "0010: 68                      |                         || h", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( data.length ) );
    }

    @Test
    public void testPrettyDumpByteArray_empty()
    {
        String s = HexDump.prettyDump( new byte[0] );
        assertEquals( "      -0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||\n" + 
                      "0000:                         |                         || ", s );
        assertEquals( s.length(), HexDump.computePrettyDumpLength( 0 ) );
    }

    @Test( expected = NullPointerException.class )
    public void testPrettyDumpByteArray_null()
    {
        HexDump.prettyDump( null );
    }

}

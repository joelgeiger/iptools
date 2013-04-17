package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StringEscapeTest
{

    @Test
    public void testEscape()
    {
        assertNull( StringEscape.escape( null ) );
        assertEquals( "", StringEscape.escape( "" ) );
        assertEquals( "abc", StringEscape.escape( "abc" ) );
        assertEquals( "ta co", StringEscape.escape( "ta\\20co" ) );
        assertEquals( "ta co", StringEscape.escape( "ta co" ) );
        assertEquals( "ab\ncd", StringEscape.escape( "ab\\0acd" ) );
        assertEquals( "ab\ncd", StringEscape.escape( "ab\\0Acd" ) );
        assertEquals( "ab\r\ncd", StringEscape.escape( "ab\\0D\\0Acd" ) );
        assertEquals( "ab" + (char)0xFF + "cd", StringEscape.escape( "ab\\ffcd" ) );
    }


    @Test
    public void testHexCharToVal()
    {
        assertEquals( 1, StringEscape.hexCharToVal( '1' ) );
        assertEquals( 10, StringEscape.hexCharToVal( 'a' ) );
        assertEquals( 10, StringEscape.hexCharToVal( 'A' ) );
        assertEquals( 15, StringEscape.hexCharToVal( 'f' ) );
        assertEquals( 15, StringEscape.hexCharToVal( 'F' ) );
    }

}

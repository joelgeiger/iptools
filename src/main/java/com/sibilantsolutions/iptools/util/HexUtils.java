package com.sibilantsolutions.iptools.util;

abstract public class HexUtils
{

    final static /*package*/ char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    private HexUtils() {}   //Prevent instantiation.

    static public String encodeNum( long num )
    {
        StringBuilder buf = new StringBuilder();    //TODO Precompute the length.

        if ( num == 0 ) //HACK
            return "" + (char)0x00;

        while ( num > 0 )
        {
            char c = (char)( num & 0xFF );
            buf.insert( 0, c ); //HACK: Inserting at front.
            num = num >>> 8;
        }

        return buf.toString();
    }

    static public String encodeNum( long num, int numBytes )
    {
        return pad( encodeNum( num ), numBytes, (char)0x00 );
    }

    static public String numToHex( long num )
    {
        StringBuilder buf = new StringBuilder();    //TODO Precompute the length.

        if ( num == 0 ) //HACK
            return "00";

        while ( num > 0 )
        {
            int hi = (int)( (num & 0xF0) >>> 4 );
            int lo = (int)( num & 0x0F );
            String c = "" + HEX_CHARS[hi] + HEX_CHARS[lo];
            buf.insert( 0, c ); //HACK: Inserting at front.
            num = num >>> 8;
        }

        return buf.toString();
    }

    static public String pad( String str, int len, char padChar )
    {
        int numPad = len - str.length();

        if ( numPad > 0 )
        {
            StringBuilder buf = new StringBuilder( str.length() + numPad );
            for ( int i = 0; i < numPad; i++ )
                buf.append( padChar );

            buf.append( str );

            str = buf.toString();
        }

        return str;
    }

}

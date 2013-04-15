package com.sibilantsolutions.iptools.util;

/**
 * Static methods to produce hex dumps in various formats.
 */

public abstract class HexDump
{
    final static private String PRETTY_HEADER = "-0 -1 -2 -3 -4 -5 -6 -7 | -8 -9 -A -B -C -D -E -F ||";
    
    final static private int MIN_OFFSET_LEN = 4;
    
    final static private char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
            'A', 'B', 'C', 'D', 'E', 'F'};
    
    final static private String BEGIN_SEP = ": ";
    final static private String MID_SEP = "| ";
    final static private String END_SEP = "|| ";
    
    final static private int BYTES_PER_LINE = 16;
    
    private HexDump() {}    //Prevent instantiation.

    /*package*/ static int computePrettyDumpLength( final int len )
    {
        //int lines = Math.max( 1, len / 16 );
        int lines;
        if ( len <= BYTES_PER_LINE )
            lines = 1;
        else
        {
            lines = len / BYTES_PER_LINE + 1;
        }
        int remainder = len % 16;
        if ( len > 0 && remainder == 0 )    //Last line has exactly 16.
            remainder = 16;
        int offsetLen = MIN_OFFSET_LEN; //TODO
        int dumpLen = offsetLen + 
                      BEGIN_SEP.length() +
                      PRETTY_HEADER.length() +
                      1 +   //newline
                      lines * (
                        offsetLen + 
                        BEGIN_SEP.length() +
                        16 * 3 + 
                        MID_SEP.length() +
                        END_SEP.length()
                        ) +
                      ( lines - 1 ) * (16 + 1) +
                      remainder;
        
        return dumpLen;
    }

    static public String numToHex( long num )
    {
        StringBuilder buf = new StringBuilder(); //TODO compute
        
        if ( num == 0 ) //HACK
            return "00";
        
        while ( num > 0 )
        {
            //long c = num & 0xFF;
            int hi = (int)( (num & 0xF0) >>> 4 );
            int lo = (int)( num & 0x0F );
            String c = "" + HEX_CHARS[hi] + HEX_CHARS[lo];
            buf.insert( 0, c ); //HACK: Inserting at front.
            num = num >>> 8;
        }
        
        return buf.toString();
    }

    static public String prettyDump( byte[] bytes )
    {
        return prettyDump( bytes, 0, bytes.length );
    }
    
    static public String prettyDump( byte[] bytes, int length )
    {
        return prettyDump( bytes, 0, length );
    }

    public static String prettyDump( byte[] bytes, final int offset, final int length )
    {
        StringBuilder buf = new StringBuilder( computePrettyDumpLength( length ) );
        
        
        int lines;
        if ( length <= BYTES_PER_LINE )
            lines = 1;
        else
        {
            lines = length / BYTES_PER_LINE + 1;
        }
        
        int offsetWidth = MIN_OFFSET_LEN + 2;   //TODO: Compute if need more than 4 for > 65535.
        
        for ( int i = 0; i < offsetWidth; i++ )
            buf.append( ' ' );
        
        buf.append( PRETTY_HEADER + '\n' );
        
        int counter = offset;
        
        for ( int line = 0; line < lines; line++ )
        {
            if ( line > 0 )
                buf.append( '\n' );
            
            int lineOffset = line * BYTES_PER_LINE;

            if ( line == 0 )
                buf.append( "0000" ); //TODO
            else
            {
                buf.append( "00" ); //TODO
                buf.append( numToHex( lineOffset ) );
            }
            buf.append( BEGIN_SEP );
            
            StringBuilder charBuf = new StringBuilder( BYTES_PER_LINE );
            
            //for ( int i = offset; i < offset + length; i++ )
            for ( int i = 0; i < BYTES_PER_LINE; i++, counter++ )
            {
                if ( i == BYTES_PER_LINE / 2 )
                    buf.append( MID_SEP );
                    
                if ( counter < offset + length )
                {
                    char c = (char)( bytes[counter] & 0xFF );
                    char hi = HEX_CHARS[ c >>> 4 ];
                    char lo = HEX_CHARS[ c & 0x0F ];
                    buf.append( hi );
                    buf.append( lo );
                    buf.append( ' ' );
                    
                    if ( c == ' ' || ( c >= '0' && c < 127 ) )  //TODO
                        charBuf.append( c );
                    else
                        charBuf.append( '.' );
                }
                else
                {
                    buf.append( "   " );
                }
                
                
            }
            
            buf.append( END_SEP );
            buf.append( charBuf );
        }
        
        return buf.toString();
    }

    static public String simpleDump( final byte[] bytes, final int offset, final int len )
    {
        char[] chars = new char[len * 3 - 1];
        for ( int i = offset; i < offset + len; i++ )
        {
            int b = bytes[i] & 0xFF;
            chars[i * 3] = HEX_CHARS[b / 16];    //Or >>>4, but compiler may already do this.
            chars[i * 3 + 1] = HEX_CHARS[b % 16];    //Or &0x0F, but compiler may already do this.
            if ( i + 1 < len )
                chars[i * 3 + 2] = ' ';
        }

        return new String( chars );
    }

}

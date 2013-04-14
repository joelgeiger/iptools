package com.sibilantsolutions.iptools.util;

/**
 * Provides an object that will return a hex dump when the toString() method is called.
 * 
 * Useful with logging frameworks like Slf4j which should do minimal work until the framework
 * decides that the log level is indeed enabled.  This class will not produce the hex dump
 * until the moment it is needed.
 * 
 * @author jt
 *
 */
public class HexDump
{
    private enum HexDumpMode
    {
        SIMPLE
    }

    private byte[] bytes;
    private int offset;
    private int len;
    private HexDumpMode mode;

    private HexDump( byte[] bytes, int offset, int len, HexDumpMode mode )   //Prevent external instantiation.
    {
        this.bytes = bytes;
        this.offset = offset;
        this.len = len;
        this.mode = mode;
    }

    static private String doSimple( final byte[] bytes, final int offset, final int len )
    {
        final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
                'A', 'B', 'C', 'D', 'E', 'F'};
        char[] chars = new char[len * 3 - 1];
        for ( int i = offset; i < offset + len; i++ )
        {
            int b = bytes[i] & 0xFF;
            chars[i * 3] = hexChars[b / 16];    //Or >>>4, but compiler may already do this.
            chars[i * 3 + 1] = hexChars[b % 16];    //Or &0x0F, but compiler may already do this.
            if ( i + 1 < len )
                chars[i * 3 + 2] = ' ';
        }

        return new String( chars );
    }

    static public HexDump simpleDump( byte[] bytes )
    {
        return simpleDump( bytes, 0, bytes.length );
    }

    static public HexDump simpleDump( byte[] bytes, int offset, int len )
    {
        return new HexDump( bytes, offset, len, HexDumpMode.SIMPLE );
    }

    @Override
    public String toString()
    {
        switch ( mode )
        {
            case SIMPLE:
                return doSimple( bytes, offset, len );
            default:
                throw new IllegalArgumentException( "Unexpected mode=" + mode );
        }
    }

}

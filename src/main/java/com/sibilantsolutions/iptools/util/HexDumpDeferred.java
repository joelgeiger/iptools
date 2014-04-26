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
public class HexDumpDeferred
{
    private enum HexDumpMode
    {
        SIMPLE_BYTES,
        SIMPLE_STRING,
        PRETTY_BYTES,
        PRETTY_STRING,
    }

    private byte[] bytes;
    private int offset;
    private int len;

    private String str;

    private HexDumpMode mode;

    private HexDumpDeferred( byte[] bytes, int offset, int len, HexDumpMode mode )   //Prevent external instantiation.
    {
        this.bytes = bytes;
        this.offset = offset;
        this.len = len;
        this.mode = mode;
    }

    private HexDumpDeferred( String str, HexDumpMode mode )   //Prevent external instantiation.
    {
        this.str = str;
        this.mode = mode;
    }

    static public HexDumpDeferred prettyDump( byte[] bytes )
    {
        return prettyDump( bytes, 0, bytes.length );
    }

    static public HexDumpDeferred prettyDump( byte[] bytes, int offset, int len )
    {
        return new HexDumpDeferred( bytes, offset, len, HexDumpMode.PRETTY_BYTES );
    }

    static public HexDumpDeferred prettyDump( String str )
    {
        return new HexDumpDeferred( str, HexDumpMode.PRETTY_STRING );
    }

    static public HexDumpDeferred simpleDump( byte[] bytes )
    {
        return simpleDump( bytes, 0, bytes.length );
    }

    static public HexDumpDeferred simpleDump( byte[] bytes, int offset, int len )
    {
        return new HexDumpDeferred( bytes, offset, len, HexDumpMode.SIMPLE_BYTES );
    }

    static public HexDumpDeferred simpleDump( String str )
    {
        return new HexDumpDeferred( str, HexDumpMode.SIMPLE_STRING );
    }

    @Override
    public String toString()
    {
        switch ( mode )
        {
            case SIMPLE_BYTES:
                return HexDump.simpleDump( bytes, offset, len );

            case SIMPLE_STRING:
                return HexDump.simpleDump( str );

            case PRETTY_BYTES:
                return HexDump.prettyDump( bytes, offset, len );

            case PRETTY_STRING:
                return HexDump.prettyDump( str );

            default:
                throw new IllegalArgumentException( "Unexpected mode=" + mode );
        }
    }

}

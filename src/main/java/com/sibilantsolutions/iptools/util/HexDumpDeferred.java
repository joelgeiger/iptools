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
        SIMPLE,
        PRETTY
    }

    private byte[] bytes;
    private int offset;
    private int len;
    private HexDumpMode mode;

    private HexDumpDeferred( byte[] bytes, int offset, int len, HexDumpMode mode )   //Prevent external instantiation.
    {
        this.bytes = bytes;
        this.offset = offset;
        this.len = len;
        this.mode = mode;
    }

    static public HexDumpDeferred prettyDump( byte[] bytes )
    {
        return prettyDump( bytes, 0, bytes.length );
    }

    static public HexDumpDeferred prettyDump( byte[] bytes, int offset, int len )
    {
        return new HexDumpDeferred( bytes, offset, len, HexDumpMode.PRETTY );
    }

    static public HexDumpDeferred simpleDump( byte[] bytes )
    {
        return simpleDump( bytes, 0, bytes.length );
    }

    static public HexDumpDeferred simpleDump( byte[] bytes, int offset, int len )
    {
        return new HexDumpDeferred( bytes, offset, len, HexDumpMode.SIMPLE );
    }

    @Override
    public String toString()
    {
        switch ( mode )
        {
            case SIMPLE:
                return HexDump.simpleDump( bytes, offset, len );
            case PRETTY:
                return HexDump.prettyDump( bytes, offset, len );
            default:
                throw new IllegalArgumentException( "Unexpected mode=" + mode );
        }
    }

}

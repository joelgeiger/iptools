package com.sibilantsolutions.iptools.util;

import static com.sibilantsolutions.iptools.util.HexDumpDeferred.prettyDump;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socker
{
    final static private Logger log = LoggerFactory.getLogger( Socker.class );
    
    static public void send( byte[] buf, Socket socket )
    {
        send( buf, 0, buf.length, socket );
    }

    static public void send( byte[] buf, int length, Socket socket )
    {
        send( buf, 0, length, socket );
    }

    static public void send( byte[] buf, int offset, int length, Socket socket )
    {
        log.info( "Send=0x{}/{}: \n{}", HexDump.numToHex( length ), length, prettyDump( buf, offset, length ) );

        try
        {
            OutputStream os = socket.getOutputStream();
            os.write( buf, offset, length );
            os.flush();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }

    }

    static public void send( String s, Socket socket )
    {
        byte[] bytes = s.getBytes( HexDump.cs );
        send( bytes, socket );
    }

}

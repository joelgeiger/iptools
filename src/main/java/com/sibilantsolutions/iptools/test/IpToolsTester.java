/*
package com.sibilantsolutions.iptools.test;

import static com.sibilantsolutions.iptools.util.HexDump.simpleDump;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpToolsTester
{
    final static private Logger log = LoggerFactory.getLogger( IpToolsTester.class );

    static public void main( String[] args )
    {
        long startMs = System.currentTimeMillis();

        log.info( "main() started." );

        new IpToolsTester().test();

        long endMs = System.currentTimeMillis();

        log.info( "main() finished; duration={} ms.", endMs - startMs );
    }

    private void test()
    {
        ServerSocketFactory ssf = ServerSocketFactory.getDefault();

        Charset cs = Charset.forName( "US-ASCII" );
        
        try
        {
            InetAddress loopback = InetAddress.getByName( null );
            ServerSocket serverSocket = ssf.createServerSocket( 8888, 50, loopback );
            log.info( "Created server socket={}.", serverSocket );
            boolean isRunning = true;
            while ( isRunning )
            {
                log.info( "Waiting to accept connection={}.", serverSocket );
                Socket socket = serverSocket.accept();
                log.info( "Accepted connection={} from server={}.", socket, serverSocket );
                InputStream ins = socket.getInputStream();
                OutputStream outs = socket.getOutputStream();
//                OutputStreamWriter ow = new OutputStreamWriter( outs );
                String greeting = "Hi there!\n";
                byte[] outBytes = greeting.getBytes( cs );
                log.info( "Send={}: \n{}", outBytes.length, simpleDump( outBytes ) );
                outs.write( outBytes );
                outs.flush();
//                ow.write( greeting );
//                ow.flush();
                byte[] b = new byte[1024];
                int numRead;
                while ( ( numRead = ins.read( b ) ) >= 0 )
                {
//                    String str = new String( b, 0, numRead, cs );
                    log.info( "Read={}: \n{}", numRead, simpleDump( b, 0, numRead ) );
                }
                log.info( "Socket closed by remote peer (read returned={})={}.", numRead, socket );
                socket.close();
            }
        }
        catch ( IOException e )
        {
            log.error( "Trouble with socketry:", e );
        }
    }

}
*/

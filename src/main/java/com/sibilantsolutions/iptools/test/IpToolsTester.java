/*
package com.sibilantsolutions.iptools.test;

import static com.sibilantsolutions.iptools.util.HexDumpDeferred.prettyDump;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import com.sibilantsolutions.iptools.layer.app.http.HttpReceiver;

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

        try
        {
            InetAddress loopback = InetAddress.getByName( null );
            ServerSocket serverSocket = ssf.createServerSocket( 8888, 50, loopback );
            log.info( "Created server socket={}.", serverSocket );
            boolean isRunning = true;
            while ( isRunning )
            {
                log.info( "Waiting to accept connection={}.", serverSocket );
                final Socket socket = serverSocket.accept();
                log.info( "Accepted connection={} from server={}.", socket, serverSocket );
                Runnable r = new Runnable() {
                    
                    @Override
                    public void run()
                    {
                        log.info( "Started receiver thread for socket={}.", socket );
                        try
                        {
                            readLoop( socket );
                        }
                        catch ( IOException e )
                        {
                            log.error( "Trouble in read loop:", new Exception( e ) );
                        }
                        log.info( "Finished receiver thread for socket={}.", socket );
                    }
                };
                new Thread( r ).start();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Trouble with socketry:", e );
        }
    }

    private void readLoop( Socket socket ) throws IOException
    {
//        Charset cs = Charset.forName( "US-ASCII" );
        
        SocketListenerI listener = new HttpReceiver();
        InputStream ins = socket.getInputStream();
//        OutputStream outs = socket.getOutputStream();
//                OutputStreamWriter ow = new OutputStreamWriter( outs );
//        String greeting = "Hi there!\n";
//        byte[] outBytes = greeting.getBytes( cs );
//        log.info( "Send={}: \n{}", outBytes.length, simpleDump( outBytes ) );
//        outs.write( outBytes );
//        outs.flush();
//                ow.write( greeting );
//                ow.flush();
        byte[] b = new byte[1024];
        int numRead;
        while ( ( numRead = ins.read( b ) ) >= 0 )
        {
            log.info( "Read={}: \n{}", numRead, prettyDump( b, 0, numRead ) );
            try
            {
                listener.onReceive( new ReceiveEvt( b, numRead, socket ) );
            }
            catch ( Exception e )
            {
                log.error( "Trouble processing data:", new Exception( e ) );
                //TODO: Send a 503.
            }
        }
        log.info( "Socket closed by remote peer (read returned={})={}.", numRead, socket );
        socket.close();
    }

}
*/

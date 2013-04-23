/*
package com.sibilantsolutions.iptools.test;

import static com.sibilantsolutions.iptools.util.HexDumpDeferred.prettyDump;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import com.sibilantsolutions.iptools.gui.SocketTwoPane;
import com.sibilantsolutions.iptools.layer.app.http.HttpReceiver;
import com.sibilantsolutions.iptools.redir.Redirector;
import com.sibilantsolutions.iptools.util.HexDump;

public class IpToolsTester
{
    final static private Logger log = LoggerFactory.getLogger( IpToolsTester.class );

    static private String[] args;
    
    static public void main( String[] args )
    {
        long startMs = System.currentTimeMillis();

        log.info( "main() started." );
        
        IpToolsTester.args = args;

        new SocketTwoPane().buildUi();
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
            final ServerSocket serverSocket = ssf.createServerSocket( 8888, 50, loopback );
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
                        log.info( "Started onConnect thread for socket={}.", socket );

                        ConnectionListenerI connListener = httpConnectionListener();
                        //ConnectionListenerI connListener = redirConnectionListener();
                        connListener.onConnect( new ConnectEvent( socket, serverSocket ) );

                        log.info( "Finished onConnect thread for socket={}.", socket );
                    }
                };

                    //Start the onConnect thread immediately after accept, so that the server
                    //can accept another connection right away.
                new Thread( r ).start();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Trouble with socketry:", e );
        }
    }

    private ConnectionListenerI httpConnectionListener()
    {
        return new ConnectionListenerI() {

            @Override
            public void onConnect( ConnectEvent evt )
            {
                final Socket socket = evt.getSocket();

                Runnable r = new Runnable() {

                    @Override
                    public void run()
                    {
                        log.info( "Started receiver thread for socket={}.", socket );
                        SocketListenerI listener = new HttpReceiver();
                        readLoop( socket, listener );
                        log.info( "Finished receiver thread for socket={}.", socket );
                    }
                };
                new Thread( r ).start();
            }
        };
    }

    private ConnectionListenerI redirConnectionListener()
    {
        Redirector redirector = new Redirector();
        redirector.setTargetHost( args[0] );
        redirector.setTargetPort( Integer.parseInt( args[1] ) );

        return redirector;
    }

    static public void readLoop( Socket socket, SocketListenerI listener )
    {
        log.info( "Running read loop for socket={}.", socket );

        InputStream ins;
        try
        {
            ins = socket.getInputStream();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }

        byte[] b = new byte[1024];
        boolean isRunning = true;
        while ( isRunning )
        {
            int numRead = -1304231933;
            isRunning = false;

            try
            {
                numRead = ins.read( b );
                isRunning = ( numRead >= 0 );
                
                if ( ! isRunning )
                {
                    log.info( "Socket closed intentionally by remote host (read returned={})={}.", numRead, socket );
                }
            }
            catch ( SocketException e )
            {
                if ( socket.isClosed() )
                {
                    log.info( "Socket read unblocked after being closed={}.", socket );
                }
                else
                {
                    // TODO Auto-generated catch block
                    throw new UnsupportedOperationException( "OGTE TODO!", e );
                }
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                throw new UnsupportedOperationException( "OGTE TODO!", e );
            }
            
            if ( isRunning )
            {
                log.info( "Read=0x{}/{}: \n{}", HexDump.numToHex( numRead ), numRead, prettyDump( b, 0, numRead ) );
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
        }

        try
        {
            socket.close();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }
        
        log.info( "Finished read loop for socket={}.", socket );
    }

}
*/

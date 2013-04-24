/*
package com.sibilantsolutions.iptools.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import com.sibilantsolutions.iptools.gui.SocketTwoPane;
import com.sibilantsolutions.iptools.layer.app.http.HttpReceiver;
import com.sibilantsolutions.iptools.redir.Redirector;
import com.sibilantsolutions.iptools.util.Socker;

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

                SocketListenerI listener = new HttpReceiver();
                Socker.readLoopThread( socket, listener );
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

}
*/

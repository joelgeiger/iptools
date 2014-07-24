package com.sibilantsolutions.iptools.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.gui.SocketTwoPane;
import com.sibilantsolutions.iptools.util.DurationLoggingRunnable;

public class IpToolsTester
{
    final static private Logger log = LoggerFactory.getLogger( IpToolsTester.class );

//    static private String[] args;

    static public void main( final String[] args )
    {
        log.info( "main() started." );

        try
        {
            Runnable r = new Runnable() {

                @Override
                public void run()
                {
//                    IpToolsTester.args = args;

                    new SocketTwoPane().buildUi();
                    //new IpToolsTester().test();
            //        new IpToolsTester().ircTest();
            //        new IpToolsTester().jCommanderTest( args );
            //        new IpToolsTester().args4jTest( args );
                }
            };

            r = new DurationLoggingRunnable( r, null );

            r.run();
        }
        catch( Exception e )
        {
            log.error( "Trouble:", new RuntimeException( e ) );
        }
    }
/* out
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
                        log.info( "Started onConnect thread={} for socket={}.", Thread.currentThread(), socket );

                        //ConnectionListenerI connListener = httpConnectionListener();
                        ConnectionListenerI connListener = redirConnectionListener();
                        connListener.onConnect( new ConnectEvent( socket, serverSocket ) );

                        log.info( "Finished onConnect thread={} for socket={}.", Thread.currentThread(), socket );
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

    private ConnectionListenerI redirConnectionListener()
    {
        Redirector redirector = new Redirector();
        redirector.setTargetHost( args[0] );
        redirector.setTargetPort( Integer.parseInt( args[1] ) );
        redirector.setTargetSsl( Boolean.parseBoolean( args[2] ) );

        return redirector;
    }
//*/
}

package com.sibilantsolutions.iptools.util;

import static com.sibilantsolutions.iptools.util.HexDumpDeferred.prettyDump;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class Socker
{
    final static private Logger log = LoggerFactory.getLogger( Socker.class );

    static public Socket connect( String hostName, int hostPort )
    {
        return connect( hostName, hostPort, false );
    }

    static public Socket connect( String hostName, int hostPort, boolean isSsl )
    {
        SocketFactory socketFactory;
        if ( isSsl )
            socketFactory = SSLSocketFactory.getDefault();
        else
            socketFactory = SocketFactory.getDefault();


        log.info( "Connecting to host={}:{} SSL={}.", hostName, hostPort, isSsl );

        Socket socket;
        try
        {
            socket = socketFactory.createSocket( hostName, hostPort );
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }

        log.info( "Connected to host={}.", socket );

        if ( isSsl )
        {
            SSLSocket ssl = (SSLSocket)socket;
            ssl.addHandshakeCompletedListener( new HandshakeCompletedListener() {

                @Override
                public void handshakeCompleted( HandshakeCompletedEvent event )
                {
                    log.info( "Finished SSL handshake ({})={}.", event.getCipherSuite(), event.getSocket() );
                    Certificate[] peerCertificates;
                    try
                    {
                        peerCertificates = event.getPeerCertificates();
                    }
                    catch ( SSLPeerUnverifiedException e )
                    {
                        // TODO Auto-generated catch block
                        throw new UnsupportedOperationException( "OGTE TODO!", e );
                    }

                    for ( int i = 0; i < peerCertificates.length; i++ )
                    {
                        Certificate certificate = peerCertificates[i];
                        log.info( "Server cert {}/{}: {}", i + 1, peerCertificates.length, certificate );
                    }
                }
            } );

            log.info( "Starting SSL handshake={}.", ssl );

            try
            {
                ssl.startHandshake();
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                throw new UnsupportedOperationException( "OGTE TODO!", e );
            }
        }

        return socket;
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

        byte[] b = new byte[4096];
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
                log.info( "Read=0x{}/{}: \n{}", HexUtils.numToHex( numRead ), numRead, prettyDump( b, 0, numRead ) );
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

    static public Thread readLoopThread( final Socket socket, final SocketListenerI listener )
    {
        Runnable r = new Runnable() {

            @Override
            public void run()
            {
                log.info( "Started receiver thread={} for socket={}.", Thread.currentThread(), socket );

                readLoop( socket, listener );

                log.info( "Finished receiver thread={} for socket={}.", Thread.currentThread(), socket );
            }

        };

        Thread thread = new Thread( r );
        thread.start();
        return thread;
    }

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
        log.info( "Send=0x{}/{}: \n{}", HexUtils.numToHex( length ), length, prettyDump( buf, offset, length ) );

        sendNoLog( buf, offset, length, socket );

    }

    static public void send( String s, Socket socket )
    {
        byte[] bytes = s.getBytes( HexDump.cs );
        send( bytes, socket );
    }

    public static void sendNoLog( byte[] buf, int offset, int length, Socket socket )
    {
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

}

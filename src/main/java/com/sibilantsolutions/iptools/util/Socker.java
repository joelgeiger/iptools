package com.sibilantsolutions.iptools.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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

//TODO: Log connect duration.
//TODO: Log SSL handshake duration.
//TODO: Log connection duration when socket closes (or just use thread duration?).
//TODO: Log socket id for send & recv.
//TODO: Close socket if readLoop exits by exception (try/finally).
//TODO: Handle connection reset gracefully, similar to a socket close.
//TODO: Provide a single method to connect and start the read thread.
//TODO: Set to decide whether an exception should close the socket or not; also apply setting to
//      buffers (e.g. LengthByteBuffer).

public class Socker
{
    final static private Logger log = LoggerFactory.getLogger( Socker.class );

    static public Socket connect( InetSocketAddress socketAddress )
    {
        return connect( socketAddress, false );
    }

    static public Socket connect( InetSocketAddress socketAddress, boolean isSsl )
    {
        SocketFactory socketFactory;
        if ( isSsl )
            socketFactory = SSLSocketFactory.getDefault();
        else
            socketFactory = SocketFactory.getDefault();


        log.info( "Making TCP/IP connection to host={} SSL={}.", socketAddress, isSsl );

        Socket socket;
        try
        {
            socket = socketFactory.createSocket( socketAddress.getAddress(), socketAddress.getPort() );
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }

        log.info( "Made TCP/IP connection to host={}.", socket );

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

    static public Socket connect( String hostName, int hostPort )
    {
        return connect( hostName, hostPort, false );
    }

    static public Socket connect( String hostName, int hostPort, boolean isSsl )
    {
        InetSocketAddress inetSocketAddress = new InetSocketAddress( hostName, hostPort );

        return connect( inetSocketAddress, isSsl );
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
                log.info( "Read=0x{}/{} {}: \n{}",
                        HexUtils.numToHex( numRead ), numRead, socket, HexDumpDeferred.prettyDump( b, 0, numRead ) );

                try
                {
                    listener.onReceive( new ReceiveEvt( b, numRead, socket ) );
                }
                catch ( Exception e )
                {
                    log.error( "Trouble processing data:", e );
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
        Runnable r = new DurationLoggingRunnable( new Runnable() {

            @Override
            public void run()
            {
                readLoop( socket, listener );
            }
        }, "socket=" + socket );

        Thread thread = new Thread( r, socket.toString() );
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
        log.info( "Send=0x{}/{} {}: \n{}",
                HexUtils.numToHex( length ), length, socket, HexDumpDeferred.prettyDump( buf, offset, length ) );

        sendNoLog( buf, offset, length, socket );

    }

    static public void send( DatagramPacket packet, DatagramSocket socket )
    {
        int length = packet.getLength();
        byte[] buf = packet.getData();
        int offset = packet.getOffset();

        String sockID = socket.getLocalSocketAddress().toString();
        String destID = packet.getSocketAddress().toString();

        log.info( "Send UDP=0x{}/{} {} -> {}: \n{}",
                HexUtils.numToHex( length ), length, sockID, destID,
                HexDumpDeferred.prettyDump( buf, offset, length ) );

        sendNoLog( packet, socket );

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

    public static void sendNoLog( DatagramPacket packet, DatagramSocket socket )
    {
        try
        {
            socket.send( packet );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }
    }

}

package com.sibilantsolutions.iptools.net;

import com.sibilantsolutions.iptools.event.DatagramReceiveEvt;
import com.sibilantsolutions.iptools.event.DatagramReceiverI;
import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import com.sibilantsolutions.utils.util.DurationLoggingRunnable;
import com.sibilantsolutions.utils.util.HexDump;
import com.sibilantsolutions.utils.util.HexDumpDeferred;
import com.sibilantsolutions.utils.util.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.Certificate;

//TODO: Log connect duration.
//TODO: Log SSL handshake duration.
//TODO: Log connection duration when socket closes (or just use thread duration?).
//TODO: Log socket id for send & recv.
//TODO: Close socket if readLoop exits by exception (try/finally).
//TODO: Handle connection reset gracefully, similar to a socket close.
//TODO: Provide a single method to connect and start the read thread.
//TODO: Set to decide whether an exception should close the socket or not; also apply setting to
//      buffers (e.g. LengthByteBuffer).

public class SocketUtils
{
    final static private Logger log = LoggerFactory.getLogger( SocketUtils.class );
    private static final Marker datastreamMarker = MarkerFactory.getMarker("DATASTREAM");

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
            // Logging at info instead of error; caller can log error if it wants.
            log.info( "Failed to make TCP/IP connection to host=" + socketAddress + ":", e );

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
                        log.debug("Server cert {}/{}: {}", i + 1, peerCertificates.length, certificate);
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

    static public void readLoop( int bufferSize, Socket socket, SocketListenerI listener )
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

        byte[] b = new byte[bufferSize];
        boolean isRunning = true;
        while ( isRunning )
        {
            isRunning = false;

            int numRead = -1304231933;

            try
            {
                numRead = ins.read( b );
                isRunning = ( numRead >= 0 );

                if ( ! isRunning )
                {
                    log.info( "Socket closed intentionally by remote host (read returned={})={}.", numRead, socket );
                    listener.onLostConnection( new LostConnectionEvt( null, socket ) );
                }
            }
            catch ( SocketException e )
            {
                    //Work around apparent bug in SSLSocket.isClosed(), where value of isClosed()
                    //may change over time.
                if ( socket instanceof SSLSocket )
                {
                    boolean initiallyClosed = socket.isClosed();

                    if ( ! initiallyClosed )
                    {
                        final int sleepMs = 25 * 1;

                        try
                        {
                            Thread.sleep( sleepMs );
                        }
                        catch ( InterruptedException e1 )
                        {
                            // TODO Auto-generated catch block
                            throw new UnsupportedOperationException( "OGTE TODO!", e1 );
                        }

                        boolean nowClosed = socket.isClosed();

                        if ( initiallyClosed != nowClosed )
                        {
                            //The bug has occurred: the value of isClosed() has changed.
                            log.info( "isClosed state changed from {} to {}.", initiallyClosed, nowClosed );
                        }
                    }
                }

                if ( socket.isClosed() )
                {
                        //Somebody in another thread called Socket.close().
                        //Don't want to call onLostConnection for an internally-generated event,
                        //only for external event (remote host intentionally closed connection,
                        //reset connection, or network error).
                    log.info( "Socket closed intentionally by local host (close invocation caused read to unblock)={}.",
                            socket );
                }
                else
                {
                        //e.g. Connection reset at TCP level.
                    listener.onLostConnection( new LostConnectionEvt( e, socket ) );
                }
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                throw new UnsupportedOperationException( "OGTE TODO!", e );
            }

            if ( isRunning )
            {
                    //Give a copy to the listener, not our original buffer.  They may queue this
                    //or otherwise delay processing and we don't want to read again into the same
                    //array.
                byte[] copy = new byte[numRead];
                System.arraycopy( b, 0, copy, 0, numRead );

                log.trace(datastreamMarker, "Read=0x{}/{} {}: \n{}",
                        HexUtils.numToHex( numRead ), numRead, socket,
                        HexDumpDeferred.prettyDump( copy ) );

                try
                {
                    listener.onReceive( new ReceiveEvt( copy, socket ) );
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
        return readLoopThread( 4096, socket, listener );
    }

    static public Thread readLoopThread( final int bufferSize, final Socket socket, final SocketListenerI listener )
    {
        Runnable r = new Runnable() {

            @Override
            public void run()
            {
                readLoop( bufferSize, socket, listener );
            }
        };

        r = new DurationLoggingRunnable( r, "socket=" + socket );

        Thread thread = new Thread( r, socket.toString() );
        thread.start();
        return thread;
    }

    static public void receiveLoop( DatagramPacket packet, DatagramSocket socket, DatagramReceiverI receiver )
    {
        final String sockID = socket.getLocalSocketAddress().toString();

        log.info( "Running receive loop for socket={}.", sockID );

        boolean isRunning = true;

        while ( isRunning )
        {
            isRunning = false;

            try
            {
                socket.receive( packet );
                isRunning = true;
            }
            catch ( SocketException e )
            {
                if ( socket.isClosed() )
                {
                        //Somebody in another thread called DatagramSocket.close().
                        //Don't want to call onLostConnection for an internally-generated event,
                        //only for external event (remote host intentionally closed connection,
                        //reset connection, or network error).
                    log.info( "DatagramSocket closed intentionally by local host (close invocation caused receive to unblock)={}.",
                            sockID );
                }
                else
                {
                    // TODO Auto-generated catch block
                    throw new UnsupportedOperationException( "MY TODO!", e );
                }
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                throw new UnsupportedOperationException( "MY TODO!", e );
            }

            if ( isRunning )
            {
                log.trace(datastreamMarker, "Received UDP=0x{}/{}, socket={} <- from={}: \n{}",
                        HexUtils.numToHex( packet.getLength() ), packet.getLength(),
                        sockID, packet.getSocketAddress(),
                        HexDumpDeferred.prettyDump( packet.getData(), packet.getOffset(), packet.getLength() ) );

                DatagramReceiveEvt evt = new DatagramReceiveEvt( packet, socket );

                receiver.onReceive( evt );
            }
        }

        log.info( "Finished receive loop for socket={}.", sockID );
    }

    static public Thread receiveLoopThread( final DatagramPacket packet, final DatagramSocket socket, final DatagramReceiverI receiver )
    {
        Runnable r = new Runnable() {

            @Override
            public void run()
            {
                receiveLoop( packet, socket, receiver );
            }
        };

        final String socketId = socket.getLocalSocketAddress().toString();

        r = new DurationLoggingRunnable( r, "socket=" + socketId );

        Thread t = new Thread( r, socketId );
        t.start();
        return t;
    }

    static public void send( byte[] buf, int offset, int length, Socket socket )
    {
        log.trace(datastreamMarker, "Send=0x{}/{} {}: \n{}",
                HexUtils.numToHex( length ), length, socket, HexDumpDeferred.prettyDump( buf, offset, length ) );

        sendNoLog( buf, offset, length, socket );

    }

    static public void send( byte[] buf, int length, Socket socket )
    {
        send( buf, 0, length, socket );
    }

    static public void send( byte[] buf, Socket socket )
    {
        send( buf, 0, buf.length, socket );
    }

    static public void send( DatagramPacket packet, DatagramSocket socket )
    {
        int length = packet.getLength();
        byte[] buf = packet.getData();
        int offset = packet.getOffset();

        String sockID = socket.getLocalSocketAddress().toString();
        String destID = packet.getSocketAddress().toString();

        log.trace(datastreamMarker, "Send UDP=0x{}/{} socket={} -> to={}: \n{}",
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

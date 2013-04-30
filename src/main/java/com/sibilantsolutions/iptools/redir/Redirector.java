package com.sibilantsolutions.iptools.redir;

import java.io.IOException;
import java.net.Socket;
import java.security.cert.Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;

public class Redirector implements ConnectionListenerI
{
    final static private Logger log = LoggerFactory.getLogger( Redirector.class );

    private RedirPeer incomingPeer;
    private RedirPeer targetPeer;

    private String targetHost;
    private int targetPort;
    private boolean isTargetSsl = false;

    @Override
    public void onConnect( ConnectEvent evt )
    {
        Socket socket = evt.getSocket();
        incomingPeer = new RedirPeer();
        incomingPeer.setSocket( socket );

        targetPeer = new RedirPeer();
        SocketFactory socketFactory;
        if ( isTargetSsl )
            socketFactory = SSLSocketFactory.getDefault();
        else
            socketFactory = SocketFactory.getDefault();

        Socket targetSocket;
        try
        {
            log.info( "Connecting to target={}:{} SSL={}.", targetHost, targetPort, isTargetSsl );
            targetSocket = socketFactory.createSocket( targetHost, targetPort );
            log.info( "Connected to target={}.", targetSocket );
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }
        
        if ( isTargetSsl )
        {
            SSLSocket ssl = (SSLSocket)targetSocket;
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

        targetPeer.setSocket( targetSocket );
        targetPeer.setPeer( incomingPeer );

        incomingPeer.setPeer( targetPeer );

        new Thread( incomingPeer ).start();
        new Thread( targetPeer ).start();
    }

    public String getTargetHost()
    {
        return targetHost;
    }

    public void setTargetHost( String targetHost )
    {
        this.targetHost = targetHost;
    }

    public int getTargetPort()
    {
        return targetPort;
    }

    public void setTargetPort( int targetPort )
    {
        this.targetPort = targetPort;
    }

    public boolean isTargetSsl()
    {
        return isTargetSsl;
    }

    public void setTargetSsl( boolean isTargetSsl )
    {
        this.isTargetSsl = isTargetSsl;
    }

}

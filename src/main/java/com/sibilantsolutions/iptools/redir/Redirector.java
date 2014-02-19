package com.sibilantsolutions.iptools.redir;

import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;

import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;
import com.sibilantsolutions.iptools.util.CertDuplicator;
import com.sibilantsolutions.iptools.util.Socker;

public class Redirector implements ConnectionListenerI
{
    //final static private Logger log = LoggerFactory.getLogger( Redirector.class );

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

        Socket targetSocket = Socker.connect( targetHost, targetPort, isTargetSsl );

            //HACK TODO: Cert duplicator needs to be a configurable option.
        if ( isTargetSsl )
        {
            SSLSocket sslTargetSocket = (SSLSocket)targetSocket;
            try
            {
                Certificate[] peerCertificates = sslTargetSocket.getSession().getPeerCertificates();
                X509Certificate peerCert = (X509Certificate)peerCertificates[0];
                CertDuplicator.duplicate( peerCert );
            }
            catch ( SSLPeerUnverifiedException e )
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

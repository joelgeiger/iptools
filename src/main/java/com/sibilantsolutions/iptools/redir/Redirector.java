package com.sibilantsolutions.iptools.redir;

import java.net.Socket;

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

    @Override
    public void onConnect( ConnectEvent evt )
    {
        Socket socket = evt.getSocket();
        incomingPeer = new RedirPeer();
        incomingPeer.setSocket( socket );

        targetPeer = new RedirPeer();
        Socket targetSocket;
        try
        {
            log.info( "Connecting to target={}:{}.", targetHost, targetPort );
            targetSocket = new Socket( targetHost, targetPort );
            log.info( "Connected to target={}.", targetSocket );
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
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

}

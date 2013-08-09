package com.sibilantsolutions.iptools.redir;

import java.net.Socket;

import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;
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

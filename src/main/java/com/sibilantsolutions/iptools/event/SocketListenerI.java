package com.sibilantsolutions.iptools.event;

public interface SocketListenerI
{

    public void onLostConnection( LostConnectionEvt evt );
    public void onReceive( ReceiveEvt evt );

}

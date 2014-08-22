package com.sibilantsolutions.iptools.event;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectEvent
{

    private Socket socket;
    private ServerSocket server;

    final private long timestamp = System.currentTimeMillis();

    public ConnectEvent( Socket socket, ServerSocket server )
    {
        this.socket = socket;
        this.server = server;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public ServerSocket getServer()
    {
        return server;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

}

package com.sibilantsolutions.iptools.event;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectEvent
{

    private Socket socket;
    private ServerSocket server;

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

}

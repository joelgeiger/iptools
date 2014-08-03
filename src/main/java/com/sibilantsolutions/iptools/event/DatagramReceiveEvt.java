package com.sibilantsolutions.iptools.event;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramReceiveEvt
{

    private DatagramPacket packet;
    private DatagramSocket source;

    public DatagramReceiveEvt( DatagramPacket packet, DatagramSocket source )
    {
        this.packet = packet;
        this.source = source;
    }

    public DatagramPacket getPacket()
    {
        return packet;
    }

    public DatagramSocket getSource()
    {
        return source;
    }

}

package com.sibilantsolutions.iptools.event;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramReceiveEvt
{

    private DatagramPacket packet;
    private DatagramSocket source;

    final private long timestamp = System.currentTimeMillis();

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

    public long getTimestamp()
    {
        return timestamp;
    }

}

package com.sibilantsolutions.iptools.event;

import java.net.Socket;

public class ReceiveEvt
{

    private byte[] data;
    private int offset;
    private int length;
    private Socket source;

    final private long timestamp = System.currentTimeMillis();

    public ReceiveEvt( byte[] data, Socket source )
    {
        this( data, 0, data.length, source );
    }

    public ReceiveEvt( byte[] data, int length, Socket source )
    {
        this( data, 0, length, source );
    }

    public ReceiveEvt( byte[] data, int offset, int length, Socket source )
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.source = source;
    }

    public byte[] getData()
    {
        return data;
    }

    public int getOffset()
    {
        return offset;
    }

    public int getLength()
    {
        return length;
    }

    public Socket getSource()
    {
        return source;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

}

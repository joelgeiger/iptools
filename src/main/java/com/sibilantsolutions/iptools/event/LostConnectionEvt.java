package com.sibilantsolutions.iptools.event;

import java.io.IOException;
import java.net.Socket;

public class LostConnectionEvt
{

    private IOException cause;
    private Socket source;

    final private long timestamp = System.currentTimeMillis();

    public LostConnectionEvt( IOException cause, Socket source )
    {
        this.cause = cause;
        this.source = source;
    }

    public IOException getCause()
    {
        return cause;
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

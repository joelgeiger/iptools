package com.sibilantsolutions.iptools.util;

import java.text.Format;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationLoggingRunnable implements Runnable
{
    final static private Logger log = LoggerFactory.getLogger( DurationLoggingRunnable.class );

    private Runnable target;
    private String debugId;

    static private Format format = NumberFormat.getInstance();

    public DurationLoggingRunnable( Runnable target, String debugId )
    {
        this.target = target;
        this.debugId = debugId;
    }

    @Override
    public void run()
    {
        final long startMs = System.currentTimeMillis();

        if ( debugId != null )
            log.info( "Starting thread={} for {}.", Thread.currentThread(), debugId );
        else
            log.info( "Starting thread={}.", Thread.currentThread() );

        try
        {
            target.run();
        }
        catch ( Exception e )
        {
            if ( debugId != null )
                log.error( "Trouble running thread=" + Thread.currentThread() + " for " + debugId + ":", e );
            else
                log.error( "Trouble running thread=" + Thread.currentThread() + ":", e );
        }

        long endMs = System.currentTimeMillis();

        long duration = endMs - startMs;

        if ( debugId != null )
        {
            log.info( "Finished thread={} for {}, duration={} ms.",
                    Thread.currentThread(), debugId, format.format( duration ) );
        }
        else
        {
            log.info( "Finished thread={}, duration={} ms.",
                    Thread.currentThread(), format.format( duration ) );
        }
    }

}

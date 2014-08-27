package com.sibilantsolutions.iptools.net;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class ReceiveQueue implements SocketListenerI
{
    final static private Logger log = LoggerFactory.getLogger( ReceiveQueue.class );

    private SocketListenerI dest;

    final private ThreadPoolExecutor executorService = createExecutorService();

    public ReceiveQueue( SocketListenerI dest )
    {
        this.dest = dest;
    }

    private ThreadPoolExecutor createExecutorService()
    {
        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread( Runnable r )
            {
                Thread t = new Thread( r, "queueTaker " + dest );

                t.setUncaughtExceptionHandler( new UncaughtExceptionHandler() {

                    @Override
                    public void uncaughtException( Thread t, Throwable e )
                    {
                        log.error( "Uncaught exception in thread=" + t + ":", e );
                    }
                } );

                return t;
            }
        };

        return new ThreadPoolExecutor( 1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory );
    }

    @Override
    public void onLostConnection( LostConnectionEvt evt )
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException( "MY TODO!" );
    }

    @Override
    public void onReceive( final ReceiveEvt evt )
    {
        Runnable r = new Runnable() {

            @Override
            public void run()
            {
                dest.onReceive( evt );
            }
        };

        executorService.execute( r );
    }

}

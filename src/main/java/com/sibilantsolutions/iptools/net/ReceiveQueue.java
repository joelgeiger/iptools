package com.sibilantsolutions.iptools.net;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
        log.info( "Lost connection: Shutting down executor service." );

        shutdownAndAwaitTermination( executorService, executorService.getQueue().size() );

        log.info( "Lost connection: Firing event to destination." );

        dest.onLostConnection( evt );
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

        //Adapted from example in javadoc of ExecutorService.
    private void shutdownAndAwaitTermination( ExecutorService pool, int queueSize )
    {
        log.info( "Shutting down executor service; queue size={}.", queueSize );

        pool.shutdown(); // Disable new tasks from being submitted

        log.info( "Waiting for executor service to finish all queued tasks." );

        try
        {
            // Wait a while for existing tasks to terminate
            if ( ! pool.awaitTermination( 60, TimeUnit.SECONDS ) )
            {
                log.info( "Executor service did not terminate before timeout; forcing shutdown." );

                List<Runnable> queuedTasks = pool.shutdownNow(); // Cancel currently executing tasks

                log.info( "Discarded {} tasks that were still queued; still waiting for executor service to terminate."
                        , queuedTasks.size() );

                // Wait a while for tasks to respond to being cancelled
                if ( ! pool.awaitTermination( 60, TimeUnit.SECONDS ) )
                {
                    log.error( "Pool did not terminate." );
                }
                else
                {
                    log.info( "Executor service terminated after forced shutdown." );
                }
            }
            else
            {
                log.info( "Executor service finished all queued tasks and terminated." );
            }
        }
        catch ( InterruptedException ie )
        {
            log.error( "Interrupted while waiting for pool to terminate; forcing shutdown.", ie );

            // (Re-)Cancel if current thread also interrupted
            List<Runnable> queuedTasks = pool.shutdownNow();

            log.info( "Discarded {} tasks that were still queued; no longer waiting for executor service to terminate."
                    , queuedTasks.size() );

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

}

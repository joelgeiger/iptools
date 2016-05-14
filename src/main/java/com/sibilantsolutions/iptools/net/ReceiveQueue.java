package com.sibilantsolutions.iptools.net;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ReceiveQueue is a <a href="https://en.wikipedia.org/wiki/Decorator_pattern">decorator</a> for a SocketListenerI and
 * is intended to be used in a "receiver chain" with another instance of SocketListenerI.  This class maintains an
 * executor service containing a single thread, which is used as a queue.  When data is received from the socket and
 * onReceive is called, the data will be put into the queue and the calling thread can immediately go back to read more
 * data.  Meanwhile, the queued message will be fired to the decorated SocketListenerI for handling.
 * <p>
 * This is a low-level queue which contains only the raw byte stream received from the operating system.  It is the
 * responsibility of the application to assemble the byte stream into more meaningful data structures.
 * <p>
 * Benefits:
 * - gets data out of the operating system receive queue and into the control of the application
 * - allows queued data to be drained into the application in case of a lost connection before notifying the application of lost connection
 */
public class ReceiveQueue implements SocketListenerI
{
    final static private Logger log = LoggerFactory.getLogger( ReceiveQueue.class );

    private final SocketListenerI dest;
    private final String name;

    private final ThreadPoolExecutor executorService = createExecutorService();

    public ReceiveQueue( SocketListenerI dest )
    {
        this(dest, "recvQ " + dest);
    }

    public ReceiveQueue(SocketListenerI dest, String name) {
        this.dest = dest;
        this.name = name;
    }

    private ThreadPoolExecutor createExecutorService()
    {
        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread( Runnable r )
            {
                Thread t = new Thread(r, name);

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

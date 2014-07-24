package com.sibilantsolutions.iptools.net;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class LineParserBuffer implements SocketListenerI
{
    final static private Logger log = LoggerFactory.getLogger( LineParserBuffer.class );

    private SocketListenerI receiver;

    //private ByteBuffer buf = ByteBuffer.allocate( 2048 );
        //TODO: This should be switched back to something smaller.  The real solution ought to see
        //how much room is left in the buffer and only put in that many bytes from the
        //ReceiveEvt, and check to see if there is a full line yet.  If so, great.  If not then
        //it means we exceeded our max line length without finding a line.  So I guess the size
        //here ought to be no bigger than the max allowed line (is it defined in the RFC?).
    private ByteBuffer buf = ByteBuffer.allocate( 1024 * 8 );

/*
    private void doReceive( ReceiveEvt evt )
    {
        //TODO: Handle partial receives.
        ByteArrayInputStream bis = new ByteArrayInputStream( evt.getData(), evt.getOffset(), evt.getLength() );
        InputStreamReader isr = new InputStreamReader( bis );
        BufferedReader reader = new BufferedReader( isr );

        String line;

        try
        {
            while ( ( line = reader.readLine() ) != null )
            {
                log.info( "The line={}", line );
                ircDataProc.onReceiveLine( line );
            }
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }
    }
*/

    private void doReceiveBuffer( ReceiveEvt evt )
    {
        log.trace( "BEFORE PUT: The buf={}", buf );

        buf.put( evt.getData(), evt.getOffset(), evt.getLength() );

        log.trace( "AFTER PUT: The buf={}", buf );

        buf.flip();

        log.trace( "AFTER FLIP: The buf={}", buf );

        boolean foundCrLf = false;

        while ( buf.hasRemaining() )
        {
            foundCrLf = false;

            boolean foundCr = false;

                //TODO: Don't collect the bytes as we go, rather find the end index and get the
                //the whole string at once.  Will be more efficient than growing the stringbuilder.
            StringBuilder sBuf = new StringBuilder();

            log.trace( "BEFORE MARK: The buf={}", buf );
            buf.mark();
            log.trace( "AFTER MARK: The buf={}", buf );

            while ( ! foundCrLf && buf.hasRemaining() )
            {
                byte b = buf.get();

                if ( b == '\r' )
                {
                    foundCr = ! foundCr;
                }
                else if ( b == '\n' )
                {
                    if ( foundCr )
                    {
                        foundCrLf = true;
                    }
                    else
                        foundCr = false;
                }
                else
                {
                    foundCr = false;
                }

                    //This is intended to allow stray CRs or LFs to be added to the line.
                    //Can it ever happen?
                if ( ! foundCr )
                    sBuf.append( (char)b );   //TODO Is this the right way to convert >127 bytes?
            }

            if ( foundCrLf )
            {
                log.trace( "FOUND CRLF: The buf={}", buf );

                String line = sBuf.toString();
                log.debug( "The line={}", line );

                //ircDataProc.onReceiveLine( line );
                try
                {
                    receiver.onReceive( new ReceiveEvt( line.getBytes(), evt.getSource() ) );
                }
                catch ( Exception e )
                {
                        //If there is an exception while processing the line, then log it and
                        //keep going in case there is more data in the buffer.
                    log.error( "Trouble processing line=" + line, e );
                }
            }
        }

        if ( foundCrLf )
        {
            log.trace( "BEFORE CLEAR: The buf={}", buf );
            buf.clear();
            log.trace( "AFTER CLEAR: The buf={}", buf );
        }
        else
        {
            log.trace( "NOT FOUND CRLF: The buf={}", buf );
            buf.reset();
            log.trace( "AFTER RESET: The buf={}", buf );
            buf.compact();
            log.trace( "AFTER COMPACT: The buf={}", buf );
            //throw new UnsupportedOperationException( "TODO" );
        }

        log.trace( "ALL DONE: The buf={}", buf );
    }

    @Override
    public void onLostConnection( LostConnectionEvt evt )
    {
        receiver.onLostConnection( evt );
    }

    @Override
    public void onReceive( ReceiveEvt evt )
    {
        //doReceive( evt );
        doReceiveBuffer( evt );
    }

    public SocketListenerI getReceiver()
    {
        return receiver;
    }

    public void setReceiver( SocketListenerI receiver )
    {
        this.receiver = receiver;
    }

}

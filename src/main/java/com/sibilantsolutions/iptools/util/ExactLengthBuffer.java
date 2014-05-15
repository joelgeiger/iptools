package com.sibilantsolutions.iptools.util;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class ExactLengthBuffer implements SocketListenerI
{

    final static private Logger log = LoggerFactory.getLogger( ExactLengthBuffer.class );

    final private int exactLength;
    final private SocketListenerI receiver;

    final private ByteBuffer buf;

    public ExactLengthBuffer( int exactLength, SocketListenerI receiver )
    {
        this.exactLength = exactLength;
        this.receiver = receiver;

        buf = ByteBuffer.allocate( exactLength );
    }

    private void doReceiveBuffer( ReceiveEvt evt )
    {
        int offset = evt.getOffset();
        final int rawLength = evt.getLength();

        log.debug( "======== doReceiveBuffer: offset={}, length={}.",
                offset, rawLength );

        while ( offset < rawLength )
        {
            log.debug( "before put: {}.", buf );

            int remaining = buf.remaining();

            int len = Math.min( rawLength - offset, remaining );

            buf.put( evt.getData(), offset, len );
            offset += len;

            log.debug( "after put: {}.", buf );

            int curLen = buf.position();

            if ( curLen >= exactLength )
            {
                log.debug( "curLen={} so reached target={}.", curLen, exactLength );

                buf.flip();

                log.debug( "after flip: {}.", buf );

                byte[] singlePacket = new byte[exactLength];

                buf.get( singlePacket );

                log.debug( "after get: {}.", buf );

                //buf.compact();
                buf.clear();    //Clear is cheaper than compact and serves the same purpose in our case.

                //log.debug( "after compact: {}.", buf );
                log.debug( "after clear: {}.", buf );

                ReceiveEvt packetEvt = new ReceiveEvt( singlePacket, evt.getSource() );

                try
                {
                    receiver.onReceive( packetEvt );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( "Trouble processing packet (exception follows data): \n" +
                            HexDump.prettyDump( singlePacket ), e );
                }
            }
        }

    }

    @Override
    public void onReceive( ReceiveEvt evt )
    {
        doReceiveBuffer( evt );
    }

}

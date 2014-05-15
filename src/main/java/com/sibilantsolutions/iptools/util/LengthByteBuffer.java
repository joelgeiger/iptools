package com.sibilantsolutions.iptools.util;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

//TODO: Byte order
//TODO: Length bytes inclusive or not

public class LengthByteBuffer implements SocketListenerI
{

    final static private Logger log = LoggerFactory.getLogger( LengthByteBuffer.class );

    final private int numLengthBytes;
    final private int lengthBytesOffset;
    final private SocketListenerI receiver;

    final private ByteBuffer buf;

    public LengthByteBuffer( int numLengthBytes, int lengthBytesOffset, SocketListenerI receiver )
    {
        this.numLengthBytes = numLengthBytes;
        this.lengthBytesOffset = lengthBytesOffset;
        this.receiver = receiver;

            //TODO: This may be too aggressive, if we know in advance that the protocol will not
            //exceed a certain length.  Certainly it doesn't make sense to allocate a 4 gigabyte
            //buffer if we are given 4 length bytes.
        int capacity = (int)( Math.pow( 256, this.numLengthBytes ) - 1 );
        buf = ByteBuffer.allocate( capacity );
    }

    private void doReceiveBuffer( ReceiveEvt evt )
    {
        int rawOffset = evt.getOffset();
        final int rawLength = evt.getLength();

        log.debug( "======== doReceiveBuffer: offset={}, length={}.",
                rawOffset, rawLength );

        while ( rawOffset < rawLength )
        {
            log.debug( "before put: {}.", buf );

            int remaining = buf.remaining();

            int len = Math.min( rawLength - rawOffset, remaining );

            buf.put( evt.getData(), rawOffset, len );
            rawOffset += len;

            log.debug( "after put: {}.", buf );

            final int curLen = buf.position();

            log.debug( "rawOffset={}, lengtyBytesOffset={}, numLengthBytes={}.",
                    rawOffset, lengthBytesOffset, numLengthBytes );

            if ( curLen >= lengthBytesOffset + numLengthBytes )
            {
                buf.flip();

                log.debug( "after flip: {}.", buf );

                buf.position( lengthBytesOffset );

                log.debug( "after position: {}.", buf );

                byte[] lengthBytes = new byte[numLengthBytes];
                buf.get( lengthBytes );

                log.debug( "after get: {}.", buf );

                int packetLen = (int)Convert.toNum( lengthBytes, 0, lengthBytes.length );

                log.debug( "curLen={}, packetLen={}.", curLen, packetLen );

                if ( curLen >= packetLen )
                {
                    log.debug( "curLen={} so reached target={}.", curLen, packetLen );

                    buf.rewind();

                    log.debug( "after rewind: {}.", buf );

                    byte[] singlePacket = new byte[packetLen];

                    buf.get( singlePacket );

                    log.debug( "after get: {}.", buf );

                    buf.compact();

                    log.debug( "after compact: {}.", buf );

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
                else
                {
                    log.debug( "Received length bytes, but not all data yet." );

                        //TODO: Is there a simple way to do both at once?
                    buf.limit( buf.capacity() );
                    buf.position( curLen );

                    log.debug( "after limit & position: {}.", buf );
                }
            }
            else
            {
                log.debug( "Received data, but not length bytes yet." );
            }
        }

        if ( buf.position() != 0 )
        {
            log.debug( "Have a partial message; waiting for more data." );
        }
    }

    @Override
    public void onReceive( ReceiveEvt evt )
    {
        doReceiveBuffer( evt );
    }

}

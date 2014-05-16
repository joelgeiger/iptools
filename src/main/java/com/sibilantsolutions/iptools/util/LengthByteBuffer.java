package com.sibilantsolutions.iptools.util;

import java.nio.BufferOverflowException;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class LengthByteBuffer implements SocketListenerI
{

    final static private Logger log = LoggerFactory.getLogger( LengthByteBuffer.class );

    static public enum LengthByteType
    {
        /**
         * The length bytes describe the length of the entire packet including the number of length
         * bytes, any other header bytes, and the payload.
         * <p>
         * The length bytes will never be 0 even if there is no payload data; the length bytes
         * will always account for themselves and any other fixed-length header bytes if any.
         */
        LENGTH_OF_ENTIRE_PACKET,

        /**
         * The length bytes describe the length of the payload only, and do not include the
         * number of length bytes or the length of any other header bytes.
         * <p>
         * The payload length may be 0, indicating that the packet is only comprised of header bytes.
         */
        LENGTH_OF_PAYLOAD;
    }

    final private int lengthBytesOffset;
    final private int numLengthBytes;
    final private LengthByteType lengthByteType;
    final private ByteOrder byteOrder;
    final private int padBytes;
    final private SocketListenerI receiver;

    //final private ByteBuffer buf;
    final private byte[] buf;
    private int curOff;

    /**
     *
     * @param lengthBytesOffset
     * @param numLengthBytes
     * @param lengthByteType
     * @param byteOrder
     * @param padBytes
     *      Number of bytes after the length byte(s) and before the data; only used when
     *      lengthByteType is LENGTH_OF_PAYLOAD.
     * @param bufferCapacity
     * @param receiver
     */
    public LengthByteBuffer( int lengthBytesOffset, int numLengthBytes, LengthByteType lengthByteType,
            ByteOrder byteOrder, int padBytes, int bufferCapacity, SocketListenerI receiver )
    {
        this.lengthBytesOffset = lengthBytesOffset;
        this.numLengthBytes = numLengthBytes;
        this.lengthByteType = lengthByteType;
        this.byteOrder = byteOrder;
        this.padBytes = padBytes;
        this.receiver = receiver;

        //buf = ByteBuffer.allocate( bufferCapacity );
        buf = new byte[bufferCapacity];
    }

    private void doReceiveArray( ReceiveEvt evt )
    {
        int rawOffset = evt.getOffset();
        final int rawLength = evt.getLength();

        log.debug( "======== doReceiveBuffer: offset={}, length={}.",
                rawOffset, rawLength );

        while ( rawOffset < rawLength )
        {
            int remaining = buf.length - curOff;

            int len = Math.min( rawLength - rawOffset, remaining );

            if ( len == 0 )
            {
                throw new BufferOverflowException();
            }

            System.arraycopy( evt.getData(), rawOffset, buf, curOff, len );
            curOff += len;
            rawOffset += len;

            boolean keepChecking = true;

            for ( ; keepChecking; )
            {
                final int minNeeded;

                switch ( lengthByteType )
                {
                    case LENGTH_OF_ENTIRE_PACKET:
                        minNeeded = lengthBytesOffset + numLengthBytes;
                        break;

                    case LENGTH_OF_PAYLOAD:
                        minNeeded = lengthBytesOffset + numLengthBytes + padBytes;
                        break;

                    default:
                        throw new RuntimeException( "Unexpected lengthByteType=" + lengthByteType );
                }

                if ( curOff >= minNeeded )
                {
                    final int lengthBytesVal =
                            (int)Convert.toNum( buf, lengthBytesOffset, numLengthBytes, byteOrder );

                    final int packetLen;

                    switch ( lengthByteType )
                    {
                        case LENGTH_OF_ENTIRE_PACKET:
                            packetLen = lengthBytesVal;
                            break;

                        case LENGTH_OF_PAYLOAD:
                            packetLen = minNeeded + lengthBytesVal;
                            break;

                        default:
                            throw new RuntimeException( "Unexpected lengthByteType=" + lengthByteType );
                    }

                    if ( curOff >= packetLen )
                    {
                        byte[] singlePacket = new byte[packetLen];

                        System.arraycopy( buf, 0, singlePacket, 0, packetLen );

                        if ( curOff > packetLen )
                        {
                            System.arraycopy( buf, packetLen, buf, 0, curOff - packetLen );
                            curOff -= packetLen;
                        }
                        else
                        {
                            curOff = 0;
                            keepChecking = false;
                        }

                        ReceiveEvt packetEvt = new ReceiveEvt( singlePacket, evt.getSource() );

                        log.debug( "Firing single packet to receiver: \n{}",
                                HexDumpDeferred.prettyDump( singlePacket ) );

                        try
                        {
                            receiver.onReceive( packetEvt );
                        }
                        catch ( Exception e )
                        {
                            //throw new RuntimeException( "Trouble processing packet (exception follows data): \n" +
                            //        HexDump.prettyDump( singlePacket ), e );
                            log.error( "Trouble processing packet (exception follows data): \n" +
                                    HexDump.prettyDump( singlePacket ), e );
                        }
                    }
                    else
                    {
                        log.debug( "Received length bytes, but not all data yet ({} of {} bytes).",
                                curOff, packetLen );
                        keepChecking = false;
                    }
                }
                else
                {
                    log.debug( "Received data, but not length bytes yet ({} bytes).", curOff );
                    keepChecking = false;
                }
            }
        }

//        if ( curOff != 0 )
//        {
//            log.debug( "Have a partial message; waiting for more data." );
//        }
    }

/**out
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
//*/
    @Override
    public void onReceive( ReceiveEvt evt )
    {
        //doReceiveBuffer( evt );
        doReceiveArray( evt );
    }

}

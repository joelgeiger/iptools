package com.sibilantsolutions.iptools.net;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;
import com.sibilantsolutions.utils.util.Convert;
import com.sibilantsolutions.utils.util.HexDump;
import com.sibilantsolutions.utils.util.HexDumpDeferred;
import com.sibilantsolutions.utils.util.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferOverflowException;
import java.nio.ByteOrder;

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
     * @param lengthBytesOffset Number of bytes from start of message at which length byte(s) begin.
     * @param numLengthBytes Number of bytes used to represent the length of the message.
     * @param lengthByteType Length byte type
     * @param byteOrder Byte order of length bytes
     * @param padBytes
     *      Number of bytes after the length byte(s) and before the data; only used when
     *      lengthByteType is LENGTH_OF_PAYLOAD.
     * @param bufferCapacity Capacity of internal buffer used to hold paritial messages
     * @param receiver Receiver that will be given single, complete messages.
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

        log.trace("======== doReceiveBuffer: offset={}, length={}.",
                rawOffset, rawLength );

        while ( rawOffset < rawLength )
        {
            int remaining = buf.length - curOff;

            int len = Math.min( rawLength - rawOffset, remaining );

            if ( len == 0 )
            {
//            	throw new BufferOverflowException();
            	//After lost network connection (wifi dropped):
                throw new RuntimeException( "offset=" + rawOffset + ", length=" + rawLength +
                		", buf len=" + buf.length + ", cur offset=" + curOff +
                		", remaining=" + remaining, new BufferOverflowException() );
            }

            System.arraycopy( evt.getData(), rawOffset, buf, curOff, len );
            curOff += len;
            rawOffset += len;

            int numFired = 0;

            for ( boolean keepChecking = true; keepChecking; numFired++)
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

                            //Log the single packet, but only if there was more than one in the receive.
                            //The receive was already logged so we don't need to log again for the
                            //normal case of having received a single complete packet.
                        if ( keepChecking || numFired > 0 )
                        {
                            log.trace("Firing single packet=0x{}/{} to receiver: \n{}",
                                    HexUtils.numToHex( singlePacket.length ), singlePacket.length,
                                    HexDumpDeferred.prettyDump( singlePacket ) );
                        }
                        else
                        {
                            log.trace("Firing single packet=0x{}/{} to receiver.",
                                    HexUtils.numToHex( singlePacket.length ), singlePacket.length );
                        }

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
                        log.trace("Received length bytes, but not all data yet ({} of {} bytes).",
                                curOff, packetLen );
                        keepChecking = false;
                    }
                }
                else
                {
                    log.trace("Received data, but not length bytes yet ({} bytes).", curOff);
                    keepChecking = false;
                }
            }
        }

//        if ( curOff != 0 )
//        {
//            log.debug( "Have a partial message; waiting for more data." );
//        }
    }

    @Override
    public void onLostConnection( LostConnectionEvt evt )
    {
        receiver.onLostConnection( evt );
    }

    @Override
    public void onReceive( ReceiveEvt evt )
    {
        //doReceiveBuffer( evt );
        doReceiveArray( evt );
    }

}

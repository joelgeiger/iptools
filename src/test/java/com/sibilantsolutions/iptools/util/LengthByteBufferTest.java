package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.util.LengthByteBuffer.LengthByteType;

public class LengthByteBufferTest
{

    @Test
    public void testOnReceive01()
    {
        List<byte[]> expecteds = new ArrayList<byte[]>();

        expecteds.add( new byte[]{ 'a', 0, 5, 'd', 'e' } );
        expecteds.add( new byte[]{ 'a', 0, 6, 'd', 'e', 'f' } );

        PacketComparingReceiver receiver = new PacketComparingReceiver( expecteds );
        LengthByteBuffer testBuf = new LengthByteBuffer( 1, 2, LengthByteType.LENGTH_OF_ENTIRE_PACKET,
                ByteOrder.BIG_ENDIAN, 0, 4096, receiver );
        byte[] data;
        ReceiveEvt evt;

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'a', 0 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 5 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'd', 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 0 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 6 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'd' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'f' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 2, receiver.getTimesCalled() );

        ///////////////////////////

    }

    @Test
    public void testOnReceive02()
    {
        List<byte[]> expecteds = new ArrayList<byte[]>();

        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 'e' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 6, 'e', 'f' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 'e' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 6, 'e', 'f' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 'e' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 6, 'e', 'f' } );

        PacketComparingReceiver receiver = new PacketComparingReceiver( expecteds );
        LengthByteBuffer testBuf = new LengthByteBuffer( 3, 1, LengthByteType.LENGTH_OF_ENTIRE_PACKET,
                ByteOrder.BIG_ENDIAN, 0, 4096, receiver );
        byte[] data;
        ReceiveEvt evt;

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'a', 'b' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'c' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 5 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'b' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'c' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 6 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'f' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 2, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a', 'b', 'c', 5, 'e', 'a', 'b', 'c', 6, 'e', 'f' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 4, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a', 'b', 'c', 5, 'e', 'a', 'b', 'c', 6, 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 5, receiver.getTimesCalled() );

        data = new byte[]{ 'f' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 6, receiver.getTimesCalled() );

        ///////////////////////////

    }

    @Test
    public void testOnReceive03()
    {
        List<byte[]> expecteds = new ArrayList<byte[]>();

        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' } );
        expecteds.add( new byte[]{ 'a', 'b', 'c', 0, 0, 'e', 'f', 'g', 'h' } );

        PacketComparingReceiver receiver = new PacketComparingReceiver( expecteds );
        LengthByteBuffer testBuf = new LengthByteBuffer( 3, 2, LengthByteType.LENGTH_OF_PAYLOAD,
                ByteOrder.LITTLE_ENDIAN, 4, 4096, receiver );
        byte[] data;
        ReceiveEvt evt;

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                           'a', 'b', 'c', 5, 0, 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'b' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'c' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 5 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 0 };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'e' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'f' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'g' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'h' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'i' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'j' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'k' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'l' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'm' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 4, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'a', 'b', 'c', 0, 0, 'e', 'f', 'g' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 4, receiver.getTimesCalled() );

        data = new byte[]{ 'h' };
        evt = new ReceiveEvt( data, null );
        testBuf.onReceive( evt );

        assertEquals( 5, receiver.getTimesCalled() );

        ///////////////////////////

    }

}

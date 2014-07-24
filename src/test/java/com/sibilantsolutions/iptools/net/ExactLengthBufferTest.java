package com.sibilantsolutions.iptools.net;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sibilantsolutions.iptools.event.ReceiveEvt;

public class ExactLengthBufferTest
{

    @Test
    public void testOnReceive()
    {
        List<byte[]> expecteds = new ArrayList<byte[]>();

        expecteds.add( new byte[]{ 'a', 'b', 'c', 'd', 'e' } );
        expecteds.add( new byte[]{ 'f', 'g', 'h', 'i', 'j' } );
        expecteds.add( new byte[]{ 'k', 'l', 'm', 'n', 'o' } );
        expecteds.add( new byte[]{ 'p', 'q', 'r', 's', 't' } );
        expecteds.add( new byte[]{ 'u', 'v', 'w', 'x', 'y' } );
        expecteds.add( new byte[]{ 'z', 'A', 'B', 'C', 'D' } );
        expecteds.add( new byte[]{ 'E', 'F', 'G', 'H', 'I' } );
        expecteds.add( new byte[]{ 'J', 'K', 'L', 'M', 'N' } );
        expecteds.add( new byte[]{ 'O', 'P', 'Q', 'R', 'S' } );
        expecteds.add( new byte[]{ 'T', 'U', 'V', 'W', 'X' } );
        expecteds.add( new byte[]{ 'Y', 'Z', '1', '2', '3' } );
        expecteds.add( new byte[]{ '4', '5', '6', '7', '8' } );


        PacketComparingReceiver receiver = new PacketComparingReceiver( expecteds );
        ExactLengthBuffer elb = new ExactLengthBuffer( 5, receiver );

        byte[] data = new byte[]{ 'a', 'b' };
        ReceiveEvt evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'c', 'd' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 0, receiver.getTimesCalled() );

        data = new byte[]{ 'e' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 1, receiver.getTimesCalled() );

        data = new byte[]{ 'f', 'g', 'h', 'i', 'j', 'k' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 2, receiver.getTimesCalled() );

        data = new byte[]{ 'l' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 2, receiver.getTimesCalled() );

        data = new byte[]{ 'm', 'n', 'o' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 3, receiver.getTimesCalled() );

        data = new byte[]{ 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 5, receiver.getTimesCalled() );

        data = new byte[]{ 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 7, receiver.getTimesCalled() );

        data = new byte[]{ 'N' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 8, receiver.getTimesCalled() );

        data = new byte[]{ 'O', 'P', 'Q', 'R', 'S' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 9, receiver.getTimesCalled() );

        data = new byte[]{ 'T' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 9, receiver.getTimesCalled() );

        data = new byte[]{ 'U' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 9, receiver.getTimesCalled() );

        data = new byte[]{ 'V' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 9, receiver.getTimesCalled() );

        data = new byte[]{ 'W' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 9, receiver.getTimesCalled() );

        data = new byte[]{ 'X' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 10, receiver.getTimesCalled() );

        ///////////////////////////

        data = new byte[]{ 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8' };
        evt = new ReceiveEvt( data, null );
        elb.onReceive( evt );

        assertEquals( 12, receiver.getTimesCalled() );

    }

}

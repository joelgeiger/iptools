package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

/*package*/ final class PacketComparingReceiver implements SocketListenerI
{
    private List<byte[]> expecteds;
    private int timesCalled = 0;

    public PacketComparingReceiver( List<byte[]> expecteds )
    {
        this.expecteds = expecteds;
    }

    @Override
    public void onLostConnection( LostConnectionEvt evt )
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException( "OGTE TODO!" );
    }

    @Override
    public void onReceive( ReceiveEvt evt )
    {
        byte[] expected = expecteds.get( timesCalled++ );
        assertEquals( expected.length, evt.getLength() );
        assertEquals( 0, evt.getOffset() );
        byte[] data = evt.getData();
        assertArrayEquals( expected, data );
    }

    public int getTimesCalled()
    {
        return timesCalled;
    }

}
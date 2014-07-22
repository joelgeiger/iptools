package com.sibilantsolutions.iptools.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sibilantsolutions.iptools.event.LostConnectionEvt;
import com.sibilantsolutions.iptools.event.ReceiveEvt;
import com.sibilantsolutions.iptools.event.SocketListenerI;

public class LineParserBufferTest
{

    @Test
    public void testOnReceive()
    {
        String data = "abc\r\n";
        ReceiveEvt evt = new ReceiveEvt( data.getBytes(), null );

        MyProc ircDataProc = new MyProc();

        LineParserBuffer ircReceiver = new LineParserBuffer();

        ircReceiver.setReceiver( ircDataProc );

        ircReceiver.onReceive( evt );

        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );
    }

    @Test
    public void testOnReceive_multi()
    {
        String data = "abc\r\ndef\r\n";
        ReceiveEvt evt = new ReceiveEvt( data.getBytes(), null );

        MyProc ircDataProc = new MyProc();

        LineParserBuffer ircReceiver = new LineParserBuffer();

        ircReceiver.setReceiver( ircDataProc );

        ircReceiver.onReceive( evt );

        assertEquals( 2, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );
        assertEquals( "def", ircDataProc.lines.get( 1 ) );
    }

    @Test
    public void testOnReceive_partial()
    {
        String data = "ab";
        ReceiveEvt evt = new ReceiveEvt( data.getBytes(), null );

        MyProc ircDataProc = new MyProc();

        LineParserBuffer ircReceiver = new LineParserBuffer();

        ircReceiver.setReceiver( ircDataProc );

        ircReceiver.onReceive( evt );

        assertEquals( 0, ircDataProc.lines.size() );
    }

    @Test
    public void testOnReceive_multiAndPartial()
    {
        String data = "abc\r\ndef\r\ngh";
        ReceiveEvt evt = new ReceiveEvt( data.getBytes(), null );

        MyProc ircDataProc = new MyProc();

        LineParserBuffer ircReceiver = new LineParserBuffer();

        ircReceiver.setReceiver( ircDataProc );

        ircReceiver.onReceive( evt );

        assertEquals( 2, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );
        assertEquals( "def", ircDataProc.lines.get( 1 ) );

        evt = new ReceiveEvt( "i\r\n".getBytes(), null );

        ircReceiver.onReceive( evt );

        assertEquals( 3, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );
        assertEquals( "def", ircDataProc.lines.get( 1 ) );
        assertEquals( "ghi", ircDataProc.lines.get( 2 ) );
    }

    @Test
    public void testOnReceive_singleBytes()
    {
        MyProc ircDataProc = new MyProc();

        LineParserBuffer ircReceiver = new LineParserBuffer();

        ircReceiver.setReceiver( ircDataProc );

        ircReceiver.onReceive( new ReceiveEvt( "a".getBytes(), null ) );
        assertEquals( 0, ircDataProc.lines.size() );

        ircReceiver.onReceive( new ReceiveEvt( "b".getBytes(), null ) );
        assertEquals( 0, ircDataProc.lines.size() );

        ircReceiver.onReceive( new ReceiveEvt( "c".getBytes(), null ) );
        assertEquals( 0, ircDataProc.lines.size() );

        ircReceiver.onReceive( new ReceiveEvt( "\r".getBytes(), null ) );
        assertEquals( 0, ircDataProc.lines.size() );

        ircReceiver.onReceive( new ReceiveEvt( "\n".getBytes(), null ) );
        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );


        ircReceiver.onReceive( new ReceiveEvt( "d".getBytes(), null ) );
        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );

        ircReceiver.onReceive( new ReceiveEvt( "e".getBytes(), null ) );
        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );


        ircReceiver.onReceive( new ReceiveEvt( "f".getBytes(), null ) );
        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );


        ircReceiver.onReceive( new ReceiveEvt( "\r".getBytes(), null ) );
        assertEquals( 1, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );

        ircReceiver.onReceive( new ReceiveEvt( "\n".getBytes(), null ) );
        assertEquals( 2, ircDataProc.lines.size() );
        assertEquals( "abc", ircDataProc.lines.get( 0 ) );
        assertEquals( "def", ircDataProc.lines.get( 1 ) );

    }

    static private class MyProc implements SocketListenerI
    {

        private List<String> lines = new ArrayList<String>();

        @Override
        public void onLostConnection( LostConnectionEvt evt )
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException( "OGTE TODO!" );
        }

        @Override
        public void onReceive( ReceiveEvt evt )
        {
                //TODO: Make sure that this uses the correct encoding.
            String line = new String( evt.getData(), evt.getOffset(), evt.getLength() );
            lines.add( line );
        }

    }

}

package com.sibilantsolutions.iptools.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.sibilantsolutions.iptools.cli.CommandAggregator;
import com.sibilantsolutions.iptools.cli.CommandHttp;
import com.sibilantsolutions.iptools.cli.CommandIrc;
import com.sibilantsolutions.iptools.cli.CommandRedir;
import com.sibilantsolutions.iptools.cli.CommandSocketTwoPane;
import com.sibilantsolutions.iptools.event.ConnectEvent;
import com.sibilantsolutions.iptools.event.ConnectionListenerI;
import com.sibilantsolutions.iptools.gui.SocketTwoPane;
import com.sibilantsolutions.iptools.redir.Redirector;

public class IpToolsTester
{
    final static private Logger log = LoggerFactory.getLogger( IpToolsTester.class );

    static private String[] args;

    static public void main( String[] args )
    {
        try
        {
            long startMs = System.currentTimeMillis();

            log.info( "main() started." );

            IpToolsTester.args = args;

            new SocketTwoPane().buildUi();
            //new IpToolsTester().test();
    //        new IpToolsTester().ircTest();
    //        new IpToolsTester().jCommanderTest( args );
    //        new IpToolsTester().args4jTest( args );

            long endMs = System.currentTimeMillis();

            log.info( "main() finished; duration={} ms.", endMs - startMs );
        }
        catch( Exception e )
        {
            log.error( "Trouble:", new RuntimeException( e ) );
        }
    }

    private void args4jTest( String[] args )
    {
        CommandAggregator bean = new CommandAggregator();
        CmdLineParser parser = new CmdLineParser( bean );
        try
        {
            log.info( "Printing usage." );
            parser.printUsage( System.out );
            log.info( "Done printing usage." );
            parser.parseArgument( args );
            log.info( "Printing usage." );
            parser.printSingleLineUsage( System.out );
            log.info( "Done printing usage." );
            log.info( "cmd={}", bean.cmd );
            CommandIrc irc = (CommandIrc)bean.cmd;
            log.info( "file={}", irc.getFilename() );
        }
        catch ( CmdLineException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }
    }

    private void jCommanderTest( String[] args )
    {
        JCommander jc = new JCommander();

        jc.addCommand( CommandIrc.COMMAND_NAME, CommandIrc.INSTANCE, CommandIrc.ALIASES );
        jc.addCommand( CommandRedir.COMMAND_NAME, CommandRedir.INSTANCE, CommandRedir.ALIASES );
        jc.addCommand( CommandSocketTwoPane.COMMAND_NAME, CommandSocketTwoPane.INSTANCE, CommandSocketTwoPane.ALIASES );
        jc.addCommand( CommandHttp.COMMAND_NAME, CommandHttp.INSTANCE, CommandHttp.ALIASES );

        jc.usage();

        jc.parse( args );

        String parsedCommand = jc.getParsedCommand();

        switch ( parsedCommand )
        {
            case CommandIrc.COMMAND_NAME:
                String filename = CommandIrc.INSTANCE.getFilename();
                log.info( "IRC filename={}.", filename );
                break;

            case CommandRedir.COMMAND_NAME:
                log.info( "Redir." );
                break;

            default:
                throw new RuntimeException( "OGTE TODO" );
        }
    }

    private void test()
    {
        ServerSocketFactory ssf = ServerSocketFactory.getDefault();

        try
        {
            InetAddress loopback = InetAddress.getByName( null );
            final ServerSocket serverSocket = ssf.createServerSocket( 8888, 50, loopback );
            log.info( "Created server socket={}.", serverSocket );
            boolean isRunning = true;
            while ( isRunning )
            {
                log.info( "Waiting to accept connection={}.", serverSocket );
                final Socket socket = serverSocket.accept();
                log.info( "Accepted connection={} from server={}.", socket, serverSocket );
                Runnable r = new Runnable() {

                    @Override
                    public void run()
                    {
                        log.info( "Started onConnect thread={} for socket={}.", Thread.currentThread(), socket );

                        //ConnectionListenerI connListener = httpConnectionListener();
                        ConnectionListenerI connListener = redirConnectionListener();
                        connListener.onConnect( new ConnectEvent( socket, serverSocket ) );

                        log.info( "Finished onConnect thread={} for socket={}.", Thread.currentThread(), socket );
                    }
                };

                    //Start the onConnect thread immediately after accept, so that the server
                    //can accept another connection right away.
                new Thread( r ).start();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Trouble with socketry:", e );
        }
    }

    private ConnectionListenerI redirConnectionListener()
    {
        Redirector redirector = new Redirector();
        redirector.setTargetHost( args[0] );
        redirector.setTargetPort( Integer.parseInt( args[1] ) );
        redirector.setTargetSsl( Boolean.parseBoolean( args[2] ) );

        return redirector;
    }

}

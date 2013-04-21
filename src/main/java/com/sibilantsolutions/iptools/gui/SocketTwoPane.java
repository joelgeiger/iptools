package com.sibilantsolutions.iptools.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sibilantsolutions.iptools.util.HexDump;
import com.sibilantsolutions.iptools.util.Socker;
import com.sibilantsolutions.iptools.util.StringEscape;

public class SocketTwoPane
{
    final static private Logger log = LoggerFactory.getLogger( SocketTwoPane.class );
    
    private void doUi()
    {
        JFrame frame = new JFrame( "Socketry" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //LayoutManager rootLM = new GridLayout( 2, 1 );
        //LayoutManager rootLM = new FlowLayout();
        //frame.getRootPane();
        
        final int rows = 20;
        final int cols = 80;
        final JTextArea taTop = new JTextArea( rows, cols );
        taTop.setEnabled( false );
        taTop.setFont( new Font( "Monospaced", Font.PLAIN, 15 ) );
        //taTop.setText( "Chumba." );
        final JTextArea taBot = new JTextArea( rows, cols );
        taBot.requestFocusInWindow();
//        cp.add( taTop, BorderLayout.NORTH );
//        cp.add( taBot, BorderLayout.SOUTH );
        JSplitPane jsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        jsp.add( taTop );
        jsp.add( taBot );

        Container cp = frame.getContentPane();
        //cp.setLayout( rootLM );
        final JTextField txtHost = new JTextField();
        //final JTextField txtPort = new JFormattedTextField( new NumberFormatter() );
        final JTextField txtPort = new JTextField();
        JButton btnGo = new JButton( "Go!" );
        btnGo.addActionListener( new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                try
                {
                    final String host = txtHost.getText();
                    final int port = Integer.parseInt( txtPort.getText() );
                    Runnable r = new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            try
                            {
                                log.info( "Connecting to {}:{}.", host, port );
                                final Socket socket = new Socket( host, port );
                                log.info( "Connected socket={}.", socket );
                                taBot.addKeyListener( new KeyAdapter() {

                                    @Override
                                    public void keyPressed( KeyEvent evt )
                                    {
                                        if ( evt.getKeyCode() == KeyEvent.VK_ENTER )
                                        {
                                            String text = taBot.getText();
                                            text = StringEscape.escape( text );
                                            taBot.setText( "" );
                                            String dump = HexDump.prettyDump( text );
                                            taTop.append( dump + '\n' );
                                            Socker.send( text, socket );
                                        }
                                    }
                                } );
                                InputStream sin = socket.getInputStream();
                                byte[] buf = new byte[1024];
                                int numRead;
                                while ( ( numRead = sin.read( buf ) ) != -1 )
                                {
                                    String dump = HexDump.prettyDump( buf, numRead );
                                    taTop.append( dump + '\n' );
                                }
                            }
                            catch ( Exception e )
                            {
                                throw new RuntimeException( e );
                            }
                        }
                    };
                    
                    new Thread( r ).start();
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );
        JPanel destPanel = new JPanel();
        destPanel.setLayout( new GridLayout( 1, 3 ) );
        destPanel.add( txtHost );
        destPanel.add( txtPort );
        destPanel.add( btnGo );
        cp.setLayout( new BorderLayout() );
        cp.add( destPanel, BorderLayout.NORTH );
        cp.add( jsp, BorderLayout.SOUTH );
        
        frame.pack();
        frame.setVisible( true );
    }
    
    public void buildUi()
    {
        SwingUtilities.invokeLater( new Runnable() {
            
            @Override
            public void run()
            {
                doUi();
            }
        } );
    }
    
}

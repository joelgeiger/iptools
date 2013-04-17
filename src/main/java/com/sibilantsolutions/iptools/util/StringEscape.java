package com.sibilantsolutions.iptools.util;

public abstract class StringEscape
{
    
    private StringEscape() {}   //Prevent instantiation.
    
    static public String escape( String str )
    {
        if ( str == null )
            return null;
        
        StringBuilder buf = new StringBuilder();
        
        for ( int i = 0; i < str.length(); i++ )
        {
            char c = str.charAt( i );
            
            if ( c == '\\' )
            {
                char hi = str.charAt( ++i );
                if ( hi == '\\' )
                    buf.append( '\\' );
                else
                {
                    char lo = str.charAt( ++i );
                    
                    int intHi = hexCharToVal( hi );
                    int intLo = hexCharToVal( lo );
                    
                    char val = (char)( intHi * 16 + intLo );
                    buf.append( val );
                }
            }
            else
                buf.append( c );
        }
        
        return buf.toString();
    }

    /*package*/ static int hexCharToVal( char ch )
    {
        int x = Character.toUpperCase( ch );
        
        if ( x >= '0' && x <= '9' )
            x = (char)( x - '0' );
        else if ( x >= 'A' && x <= 'F' )
            x = (char)( x - 'A' + 10 );
        else
            throw new IllegalArgumentException( "What ('" + ch + "')?" );
        
        return x;
    }
    
}

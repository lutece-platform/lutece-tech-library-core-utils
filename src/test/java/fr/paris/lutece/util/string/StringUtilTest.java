/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.util.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * StringUtil Test Class
 *
 */
public class StringUtilTest
{
    /**
     * Test of substitute method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testSubstitute( )
    {
        System.out.println( "substitute" );

        String strSource = "Hello, @name@";
        String strValue = "World";
        String strBookmark = "@name@";

        String expResult = "Hello, World";
        String result = fr.paris.lutece.util.string.StringUtil.substitute( strSource, strValue, strBookmark );
        assertEquals( expResult, result );
    }

    /**
     * Test of replaceAccent method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testReplaceAccent( )
    {
        System.out.println( "replaceAccent" );

        String strInit = "Hello, World";

        String expResult = "Hello, World";
        String result = fr.paris.lutece.util.string.StringUtil.replaceAccent( strInit );
        assertEquals( expResult, result );
    }

    /**
     * Test of containsHtmlSpecialCharacters method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testContainsHtmlSpecialCharacters( )
    {
        System.out.println( "containsHtmlSpecialCharacters" );

        String strValue = "<";

        boolean expResult = true;
        boolean result = fr.paris.lutece.util.string.StringUtil.containsHtmlSpecialCharacters( strValue );
        assertEquals( expResult, result );
    }

    /**
     * Test of containsXssCharacters method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testContainsXssCharacters( )
    {
        System.out.println( "containsXssCharacters" );

        String strValue = "<";

        boolean expResult = true;
        boolean result = fr.paris.lutece.util.string.StringUtil.containsXssCharacters( strValue );
        assertEquals( expResult, result );
    }

    /**
     * Test of getXssCharactersAsString method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testGetXssCharactersAsString( )
    {
        System.out.println( "getXssCharactersAsString" );

        String result = fr.paris.lutece.util.string.StringUtil.getXssCharactersAsString( );
        // The result should not be empty
        assertTrue( result.length( ) > 0 );
    }

    /**
     * Test of compress & decompress method, of class fr.paris.lutece.util.string.StringUtil.
     */
	@Test
    public void testCompress( )
    {
    	String source = "test compress [ é$âë  \"€ \r\n 123 ]";
    	System.out.println( "Compress" );

        byte[] bytes;
		try {
			bytes = fr.paris.lutece.util.string.StringUtil.compress( source );
			assertEquals( source, fr.paris.lutece.util.string.StringUtil.decompress( bytes ) );
		} catch (IOException e) {
			fail( e.getLocalizedMessage( ) );
		}
    }

    /**
     * getIntValue returns the default value of a value that is not an integer (documented behaviour): it must not log this
     * expected case as an error with a stack trace (« For input string: "" » at each move of an admin dashboard without column).
     */
    @Test
    public void testGetIntValueOfANonIntegerLogsNoError( )
    {
        java.util.List<String> listErrors = new java.util.concurrent.CopyOnWriteArrayList<>( );
        org.apache.logging.log4j.core.appender.AbstractAppender appender = new org.apache.logging.log4j.core.appender.AbstractAppender( "getIntValueCapture",
                null, null, true, org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY )
        {
            @Override
            public void append( org.apache.logging.log4j.core.LogEvent event )
            {
                if ( event.getLevel( ).isMoreSpecificThan( org.apache.logging.log4j.Level.ERROR ) )
                {
                    listErrors.add( event.getMessage( ).getFormattedMessage( ) );
                }
            }
        };
        appender.start( );
        org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager
                .getContext( false );
        org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = context.getConfiguration( ).getLoggerConfig( "lutece.util.string" );
        loggerConfig.addAppender( appender, org.apache.logging.log4j.Level.ALL, null );
        context.updateLoggers( );
        try
        {
            assertEquals( 7, StringUtil.getIntValue( "", 7 ) );
            assertEquals( 7, StringUtil.getIntValue( null, 7 ) );
            assertEquals( 7, StringUtil.getIntValue( "abc", 7 ) );
            assertEquals( 3, StringUtil.getIntValue( "3", 7 ) );
            assertTrue( listErrors.isEmpty( ), "errors logged: " + listErrors );
        }
        finally
        {
            loggerConfig.removeAppender( "getIntValueCapture" );
            context.updateLoggers( );
            appender.stop( );
        }
    }
}

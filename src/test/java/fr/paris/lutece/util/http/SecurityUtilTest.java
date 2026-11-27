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
package fr.paris.lutece.util.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 *
 */
public class SecurityUtilTest
{

    /**
     * Test of containsCleanParameters method, of class SecurityUtil.
     */
    @Test
    public void testContainsCleanParameters( )
    {
        System.out.println( "containsCleanParameters" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "param1", "AZ" );
        request.setParameter( "param2", "09" );
        request.setParameter( "param3", "az" );
        request.setParameter( "param4", "/" );

        assertTrue( SecurityUtil.containsCleanParameters( request ) );

        request.setParameter( "param4", "%" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", ">" );
        assertFalse( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "<" );
        assertFalse( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "#" );
        assertFalse( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "\"" );
        assertFalse( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", ";" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "&" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "[" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "]" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", ";" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "{" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
        request.setParameter( "param4", "}" );
        assertTrue( SecurityUtil.containsCleanParameters( request ) );
    }

    /**
     * Test of isRedirectUrlSafe method, of class SecurityUtil, to avoid open redirect
     */
    @Test
    public void testIsRedirectUrlSafe( )
    {
        System.out.println( "isRedirectUrlSafe" );

        MockHttpServletRequest request = new MockHttpServletRequest( );

        // Assert False
        String strUrl = "http://anothersite.com";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "//anothersite.com";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "file://my.txt";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "javascript:alert('hello');";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "opera-http://anothersite.com";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );
        
        strUrl = "/\\\\redirect.ywh.at";
        request.setParameter( "url", strUrl );
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "http://another.subdomain.mylutece.com";
        request.setParameter( "url", strUrl );
        String strUrlPatterns = "http://**.lutece.com,https://**.lutece.com";
        assertFalse( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request, strUrlPatterns ) );
        
        
        // Assert True
        strUrl = null;
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "";
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "/jsp/site/Portal.jsp";
        request.setParameter( "url", strUrl );
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "Another.jsp";
        request.setParameter( "url", strUrl );
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "http://localhost/myapp/jsp/site/Portal.jsp";
        request.setParameter( "url", strUrl );
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request ) );

        strUrl = "http://another.subdomain.lutece.com";
        request.setParameter( "url", strUrl );
        strUrlPatterns = "http://**.lutece.com/**,https://**.lutece.com/**";
        assertTrue( SecurityUtil.isInternalRedirectUrlSafe( strUrl, request, strUrlPatterns ) );

    }

    /**
     * Test that logForgingProtect neutralizes a lone carriage return and other control characters, which could
     * otherwise forge or rewrite a log line.
     */
    @Test
    public void testLogForgingProtect( )
    {
        String strResult = SecurityUtil.logForgingProtect( "normal\rFORGED\u001b[31mx\u0008y" );
        assertFalse( strResult.contains( "\r" ), "a lone CR must not pass through" );
        assertFalse( strResult.contains( "\u001b" ), "an ANSI escape must not pass through" );
        assertFalse( strResult.contains( "\u0008" ), "a backspace must not pass through" );
        assertTrue( strResult.contains( "\n** FORGED" ), "a forged line must be prefixed" );
    }

    private static MockHttpServletRequest requestFrom( String strRemoteAddress, String strForwardedFor )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( )
        {
            @Override
            public String getRemoteAddr( )
            {
                return strRemoteAddress;
            }
        };
        if ( strForwardedFor != null )
        {
            request.addHeader( "X-Forwarded-For", strForwardedFor );
        }
        return request;
    }

    /**
     * No trusted proxy configured: a forged X-Forwarded-For header is ignored, the client address is the address the request comes from
     */
    @Test
    public void testGetRealIpWithoutTrustedProxiesIgnoresTheHeader( )
    {
        assertEquals( "203.0.113.9", SecurityUtil.getRealIp( requestFrom( "203.0.113.9", "1.2.3.4" ) ) );
        assertEquals( "203.0.113.9", SecurityUtil.getRealIp( requestFrom( "203.0.113.9", "1.2.3.4" ), "" ) );
        assertEquals( "203.0.113.9", SecurityUtil.getRealIp( requestFrom( "203.0.113.9", "1.2.3.4, 10.0.0.2" ), null ) );
        assertEquals( "203.0.113.9", SecurityUtil.getRealIp( requestFrom( "203.0.113.9", null ), null ) );
    }

    /**
     * Request from a trusted proxy: the right-most address of the header that is not a trusted proxy
     */
    @Test
    public void testGetRealIpFromTrustedProxy( )
    {
        String strTrusted = "10.0.0.0/8, 192.168.1.10, fd00::/8";

        // the client forged "6.6.6.6", the first proxy appended the real client address
        assertEquals( "198.51.100.7", SecurityUtil.getRealIp( requestFrom( "10.1.2.3", "6.6.6.6, 198.51.100.7, 192.168.1.10" ), strTrusted ) );
        assertEquals( "198.51.100.7", SecurityUtil.getRealIp( requestFrom( "192.168.1.10", "198.51.100.7" ), strTrusted ) );
        // IPv6 proxy and client
        assertEquals( "2001:db8::1", SecurityUtil.getRealIp( requestFrom( "fd12::5", "2001:db8::1" ), strTrusted ) );
        // no header: the proxy itself
        assertEquals( "10.1.2.3", SecurityUtil.getRealIp( requestFrom( "10.1.2.3", null ), strTrusted ) );
    }

    /**
     * Request from an address that is not a trusted proxy: its own address, the header is ignored
     */
    @Test
    public void testGetRealIpFromUntrustedSender( )
    {
        String strTrusted = "10.0.0.0/8";

        assertEquals( "203.0.113.9", SecurityUtil.getRealIp( requestFrom( "203.0.113.9", "1.2.3.4" ), strTrusted ) );
        assertEquals( "11.0.0.1", SecurityUtil.getRealIp( requestFrom( "11.0.0.1", "10.0.0.5" ), strTrusted ) );
    }

    /**
     * A header made only of trusted proxies: the left-most one
     */
    @Test
    public void testGetRealIpHeaderOfTrustedProxiesOnly( )
    {
        assertEquals( "10.0.0.5", SecurityUtil.getRealIp( requestFrom( "10.1.2.3", "10.0.0.5, 10.0.0.6" ), "10.0.0.0/8" ) );
    }
}

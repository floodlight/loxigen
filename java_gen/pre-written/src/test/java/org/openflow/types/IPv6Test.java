package org.openflow.types;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.openflow.exceptions.OFParseError;
import org.openflow.exceptions.OFShortRead;

public class IPv6Test {

    String[] testStrings = {
            "::",
            "::1",
            "ffe0::",
            "1:2:3:4:5:6:7:8"
    };

    @Test
    public void testOfString() throws UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            IPv6 ip = IPv6.of(testStrings[i]);
            InetAddress inetAddress = InetAddress.getByName(testStrings[i]);

            assertArrayEquals(ip.getBytes(), inetAddress.getAddress());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOfByteArray() throws UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            byte[] bytes = Inet6Address.getByName(testStrings[i]).getAddress();
            IPv6 ip = IPv6.of(bytes);
            assertEquals(testStrings[i], ip.toString());
            assertArrayEquals(bytes, ip.getBytes());
        }
    }

    @Test
    public void testReadFrom() throws OFParseError, OFShortRead, UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            byte[] bytes = Inet6Address.getByName(testStrings[i]).getAddress();
            IPv6 ip = IPv6.readFrom(ChannelBuffers.copiedBuffer(bytes));
            assertEquals(testStrings[i], ip.toString());
            assertArrayEquals(bytes, ip.getBytes());
        }
    }

    String[] invalidIPs = {
            "",
            ":",
            "1:2:3:4:5:6:7:8:9",
            "1:2:3:4:5:6:7:8:",
            "1:2:3:4:5:6:7:8g",
            "1:2:3:",
            "12345::",
            "1::3::8",
            "::3::"
    };

    @Test
    public void testInvalidIPs() throws OFParseError, OFShortRead {
        for(String invalid : invalidIPs) {
            try {
                IPv6.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    @Test
    public void testZeroCompression() throws OFParseError, OFShortRead {
        assertEquals("::", IPv6.of("::").toString(true, false));
        assertEquals("0:0:0:0:0:0:0:0", IPv6.of("::").toString(false, false));
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0000", IPv6.of("::").toString(false, true));
        assertEquals("1::4:5:6:0:8", IPv6.of("1:0:0:4:5:6:0:8").toString(true, false));
        assertEquals("1:0:0:4::8", IPv6.of("1:0:0:4:0:0:0:8").toString(true, false));
    }
}

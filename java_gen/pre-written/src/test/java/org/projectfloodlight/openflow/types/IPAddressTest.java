package org.projectfloodlight.openflow.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Most tests are in IPv4AddressTest and IPv6AddressTest
 * Just exception testing here
 * @author gregor
 *
 */
public class IPAddressTest {
    @Test
    public void testOfException() {
        try {
            IPAddress.of("Foobar");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IPAddressWithMask.of("Foobar");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IPAddress.of((String) null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPAddressWithMask.of(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPAddress.of((String) null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPAddressWithMask.of(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testOfString() {
        IPAddress<?> ip0 = IPAddress.of("1.2.3.4");
        IPAddress<?> ip1 = IPAddress.of("abcd::1234");
        assertTrue(ip0 instanceof IPv4Address);
        assertTrue(ip1 instanceof IPv6Address);
        assertEquals(ip0, IPv4Address.of("1.2.3.4"));
        assertEquals(ip1, IPv6Address.of("abcd::1234"));
    }

    @Test
    public void testOfInetAddress() throws Exception {
        InetAddress ia0 = InetAddress.getByName("192.168.1.123");
        InetAddress ia1 = InetAddress.getByName("fd00::4321");
        IPAddress<?> ip0 = IPAddress.of(ia0);
        IPAddress<?> ip1 = IPAddress.of(ia1);
        assertTrue(ip0 instanceof IPv4Address);
        assertTrue(ip1 instanceof IPv6Address);
        assertEquals(ip0, IPv4Address.of(ia0));
        assertEquals(ip1, IPv6Address.of(ia1));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFromInetAddressException() throws UnknownHostException {
        try {
            IPAddress.fromInetAddress(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testToInetAddress() throws Exception {
        IPAddress<?> ip0 = IPAddress.of("201.202.3.4");
        IPAddress<?> ip1 = IPAddress.of("2001:db8:abcd::1:2:3:4");
        InetAddress ia0 = ip0.toInetAddress();
        InetAddress ia1 = ip1.toInetAddress();
        assertEquals(ia0, InetAddress.getByName("201.202.3.4"));
        assertEquals(ia1, InetAddress.getByName("2001:db8:abcd:0:1:2:3:4"));
    }

    @Test
    public void testContains() {

        // Test IPv4 Mask
        IPAddressWithMask<?> mask = IPAddressWithMask.of("1.2.3.4/24");

        IPAddress<?> validIp = IPAddress.of("1.2.3.5");
        assertTrue(mask.contains(validIp));

        IPAddress<?> invalidIp = IPAddress.of("1.2.5.5");
        assertFalse(mask.contains(invalidIp));

        IPAddress<?> invalidIpv6 = IPAddress.of("10:10::ffff");
        assertFalse(mask.contains(invalidIpv6));

        // Test IPv6 Mask
        mask = IPAddressWithMask.of("10:10::1/112");

        validIp = IPAddress.of("10:10::f");
        assertTrue(mask.contains(validIp));

        invalidIp = IPAddress.of("11:10::f");
        assertFalse(mask.contains(invalidIp));

        IPAddress<?> invalidIpv4 = IPAddress.of("10.0.0.1");
        assertFalse(mask.contains(invalidIpv4));
    }

    @Test 
    public void testContainsException() {
        try {
            IPAddressWithMask<?> mask = IPAddressWithMask.of("1.2.3.4/24");
            mask.contains(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testIsUnspecified() {
        IPAddress<?> unspecifiedV4 = IPAddress.of("0.0.0.0");
        IPAddress<?> unspecifiedV6 = IPAddress.of("::");
        assertThat(unspecifiedV4.isUnspecified(), is(true));
        assertThat(unspecifiedV6.isUnspecified(), is(true));
        List<String> others = ImmutableList.of(
                "0.0.0.1",
                "1.2.3.4",
                "10.0.0.0",
                "127.0.0.1",
                "255.255.255.255",
                "::1",
                "2001:db8:1:2::5:6",
                "fc00::4:5:6:7",
                "fe80::1234",
                "ff02::1");
        for (String other : others) {
            assertThat(IPAddress.of(other).isUnspecified(), is(false));
        }
    }

    @Test
    public void testIsLoopback() {
        List<String> loopbacks = ImmutableList.of(
                "127.0.0.0",
                "127.0.0.1",
                "127.0.0.2",
                "127.0.0.255",
                "127.1.2.3",
                "127.101.102.103",
                "127.201.202.203",
                "127.255.255.255",
                "::1");
        for (String loopback : loopbacks) {
            assertThat(IPAddress.of(loopback).isLoopback(), is(true));
        }
        List<String> others = ImmutableList.of(
                "0.0.0.0",
                "0.0.0.1",
                "10.0.0.1",
                "126.255.255.255",
                "128.0.0.0",
                "255.255.255.255",
                "::",
                "::2",
                "2001:db8::1:2:3:4",
                "fe80:7:6:5:4:3:2:1",
                "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
        for (String other : others) {
            assertThat(IPAddress.of(other).isLoopback(), is(false));
        }
    }

    @Test
    public void testIsLinkLocal() {
        List<String> linkLocals = ImmutableList.of(
                "169.254.0.0",
                "169.254.0.1",
                "169.254.1.2",
                "169.254.101.102",
                "169.254.201.202",
                "169.254.255.255",
                "fe80::",
                "fe80::1",
                "fe80::1:2:3:4:5:6:7",
                "fe80:aaaa:bbbb:cccc:dddd:eeee:ffff:1234",
                "febf::",
                "febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
        for (String linkLocal : linkLocals) {
            assertThat(IPAddress.of(linkLocal).isLinkLocal(), is(true));
        }
        List<String> others = ImmutableList.of(
                "0.0.0.0",
                "1.2.3.4",
                "169.253.255.255",
                "169.255.0.0",
                "255.255.255.255",
                "::",
                "fe7f:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
                "fec0::",
                "ff02::1");
        for (String other : others) {
            assertThat(IPAddress.of(other).isLinkLocal(), is(false));
        }
    }

    @Test
    public void testMulticastIp() {
        IPAddress<?> ip0 = IPAddress.of("240.2.3.4");
        IPAddress<?> ip1 = IPAddress.of("224.0.1.1");
        IPAddress<?> ip2 = IPAddress.of("239.0.0.0");
        IPAddress<?> ip3 = IPAddress.of("feff::1");
        IPAddress<?> ip4 = IPAddress.of("ff00::1");
        assertTrue(!ip0.isMulticast());
        assertTrue(ip1.isMulticast());
        assertTrue(ip2.isMulticast());
        assertTrue(!ip3.isMulticast());
        assertTrue(ip4.isMulticast());
    }
}

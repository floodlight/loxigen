package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.junit.Test;

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
            IPAddress.of(null);
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
            IPAddress.of(null);
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
    public void testFromInetAddressException() throws UnknownHostException {
        try {
            IPAddress.fromInetAddress(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
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

}

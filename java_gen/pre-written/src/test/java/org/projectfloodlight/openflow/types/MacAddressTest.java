package org.projectfloodlight.openflow.types;

import io.netty.buffer.Unpooled;

import java.util.Arrays;

import org.junit.Test;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MacAddressTest {
    byte[][] testAddresses = new byte[][] {
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06 },
            {(byte) 0x80, 0x0, 0x0, 0x0, 0x0, 0x01},
            {(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 }
    };

    String[] testColonStrings = {
            "01:02:03:04:05:06",
            "80:00:00:00:00:01",
            "ff:ff:ff:ff:ff:ff",
    };

    String[] testHyphenStrings = {
             "01-02-03-04-05-06",
             "80-00-00-00-00-01",
             "ff-ff-ff-ff-ff-ff",
     };

    long[] testInts = {
            0x00010203040506L,
            0x00800000000001L,
            0x00ffffffffffffL
    };

    String[] invalidMacStrings = {
            "",
            "1.2.3.4",
            "0T:00:01:02:03:04",
            "00:01:02:03:04:05:06",
            "00:ff:ef:12:12:ff:",
            "00:fff:ef:12:12:ff",
            "01:02:03:04:05;06",
            "0:1:2:3:4:5:6",
            "01:02:03:04",
            "01-02-03:04-05-06",
    };

    byte[][] invalidMacBytes = {
            new byte[]{0x01, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06},
            new byte[]{0x01, 0x01, 0x02, 0x03, 0x04}
    };

    @Test
    public void testOfString() {
        testOfStringForArray(testColonStrings);
        testOfStringForArray(testHyphenStrings);
    }

    private void testOfStringForArray(String [] strings) {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.of(strings[i]);
            assertEquals(testInts[i], ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
        }
    }

    @Test
    public void testOfByteArray() {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.of(testAddresses[i]);
            assertEquals("error checking long representation of "+Arrays.toString(testAddresses[i]) + "(should be "+Long.toHexString(testInts[i]) +")", testInts[i],  ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testColonStrings[i], ip.toString());
        }
    }

    @Test
    public void testReadFrom() throws OFParseError {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.read6Bytes(Unpooled.copiedBuffer(testAddresses[i]));
            assertEquals(testInts[i], ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testColonStrings[i], ip.toString());
        }
    }


    @Test
    public void testInvalidMacStrings() throws OFParseError {
        for(String invalid : invalidMacStrings) {
            try {
                MacAddress.of(invalid);
                fail("Invalid MAC address "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    @Test
    public void testInvalidMacBytes() throws OFParseError {
        for(byte[] invalid : invalidMacBytes) {
            try {
                MacAddress.of(invalid);
                fail("Invalid MAC address bytes "+ Arrays.toString(invalid) + " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    //  Test data is imported from org.projectfloodlight.packet.EthernetTest
    @Test
    public void testToLong() {
        assertEquals(
                281474976710655L,
                MacAddress.of(new byte[]{(byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}).getLong());

        assertEquals(
                1103823438081L,
                MacAddress.of(new byte[] { (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01 }).getLong());

        assertEquals(
                141289400074368L,
                MacAddress.of(new byte[] { (byte) 0x80, (byte) 0x80,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80 }).getLong());

    }

    @Test
    public void testIsBroadcast() {
        assertTrue(MacAddress.of("FF:FF:FF:FF:FF:FF").isBroadcast());
        assertTrue(MacAddress.of(-1).isBroadcast());
        assertTrue(MacAddress.of(0x05FFFFFFFFFFFFL).isBroadcast());
        assertFalse(MacAddress.of("11:22:33:44:55:66").isBroadcast());
    }

    @Test
    public void testIsMulticast() {
        assertTrue(MacAddress.of("01:80:C2:00:00:00").isMulticast());
        assertFalse(MacAddress.of("00:80:C2:00:00:00").isMulticast());
        assertFalse(MacAddress.of("FE:80:C2:00:00:00").isMulticast());
        assertFalse(MacAddress.of(-1).isMulticast());
        assertFalse(MacAddress.of(0x05FFFFFFFFFFFFL).isMulticast());
        assertFalse(MacAddress.of("FF:FF:FF:FF:FF:FF").isMulticast());
    }

    @Test
    public void testIsLLDPAddress() {
        assertTrue(MacAddress.of("01:80:C2:00:00:00").isLLDPAddress());
        assertTrue(MacAddress.of("01:80:C2:00:00:0f").isLLDPAddress());
        assertFalse(MacAddress.of("01:80:C2:00:00:50").isLLDPAddress());
        assertFalse(MacAddress.of("01:80:C2:00:10:00").isLLDPAddress());
        assertFalse(MacAddress.of("01:80:C2:40:00:01").isLLDPAddress());
        assertFalse(MacAddress.of("00:80:C2:f0:00:00").isLLDPAddress());
        assertFalse(MacAddress.of("FE:80:C2:00:00:00").isLLDPAddress());
    }

    @Test

    public void testForIPv4MulticastAddress() {
        IPv4Address ip = IPv4Address.of("224.1.1.1");
        MacAddress mac = MacAddress.forIPv4MulticastAddress(ip);
        MacAddress expectedMac = MacAddress.of("01:00:5E:01:01:01");
        assertTrue(mac.equals(expectedMac));

        ip = IPv4Address.of("224.129.1.1");
        mac = MacAddress.forIPv4MulticastAddress(ip);
        expectedMac = MacAddress.of("01:00:5E:01:01:01");
        assertTrue(mac.equals(expectedMac));

        ip = IPv4Address.of("225.1.1.1");
        mac = MacAddress.forIPv4MulticastAddress(ip);
        expectedMac = MacAddress.of("01:00:5E:01:01:01");
        assertTrue(mac.equals(expectedMac));

        ip = IPv4Address.of("239.129.1.1");
        mac = MacAddress.forIPv4MulticastAddress(ip);
        expectedMac = MacAddress.of("01:00:5E:01:01:01");
        assertTrue(mac.equals(expectedMac));

        ip = IPv4Address.of("224.1.2.3");
        mac = MacAddress.forIPv4MulticastAddress(ip);
        expectedMac = MacAddress.of("01:00:5E:01:02:03");
        assertTrue(mac.equals(expectedMac));

        ip = IPv4Address.of("239.129.2.3");
        mac = MacAddress.forIPv4MulticastAddress(ip);
        expectedMac = MacAddress.of("01:00:5E:01:02:03");
        assertTrue(mac.equals(expectedMac));
    }

    public void testOfDatapathid() {
        MacAddress mac = MacAddress.of(DatapathId.NONE);
        assertThat(mac, is(MacAddress.NONE));

        for (String s : testColonStrings) {
            DatapathId dpid = DatapathId.of("00:00:" + s);
            mac = MacAddress.of(dpid);
            assertThat(mac, is(MacAddress.of(s)));
        }
    }

    @Test
    public void forIPv6MulticastAddr() {
        IPv6Address ip = IPv6Address.of("ff02::1:ff00:0");
        MacAddress mac = MacAddress.forIPv6MulticastAddr(ip);
        MacAddress expectedMac = MacAddress.of("33:33:ff:00:00:00");
        assertTrue(mac.equals(expectedMac));

        ip = IPv6Address.of("ff02::1:ff01:0203");
        mac = MacAddress.forIPv6MulticastAddr(ip);
        expectedMac = MacAddress.of("33:33:ff:01:02:03");
        assertTrue(mac.equals(expectedMac));

        ip = IPv6Address.of("ff02::1:0102:0304");
        mac = MacAddress.forIPv6MulticastAddr(ip);
        expectedMac = MacAddress.of("33:33:01:02:03:04");
        assertTrue(mac.equals(expectedMac));

        ip = IPv6Address.of("2001::1:0102:0304");
        try {
            mac = MacAddress.forIPv6MulticastAddr(ip);
        } catch(IllegalArgumentException e) {
                // ok
        }
    }
}

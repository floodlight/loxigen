package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.projectfloodlight.openflow.exceptions.OFParseError;

public class MacAddressTest {
    byte[][] testAddresses = new byte[][] {
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06 },
            {(byte) 0x80, 0x0, 0x0, 0x0, 0x0, 0x01},
            {(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 }
    };

    String[] testStrings = {
            "01:02:03:04:05:06",
            "80:00:00:00:00:01",
            "ff:ff:ff:ff:ff:ff"
    };

    long[] testInts = {
            0x00010203040506L,
            0x00800000000001L,
            0x00ffffffffffffL
    };

    String[] invalidMacs = {
            "",
            "1.2.3.4",
            "00:ff:ef:12:12:ff:",
            "00:fff:ef:12:12:ff",
            "01:02:03:04:05;06",
            "0:1:2:3:4:5:6",
            "01:02:03:04"
    };


    @Test
    public void testOfString() {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.of(testStrings[i]);
            assertEquals(testInts[i], ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOfByteArray() {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.of(testAddresses[i]);
            assertEquals("error checking long representation of "+Arrays.toString(testAddresses[i]) + "(should be "+Long.toHexString(testInts[i]) +")", testInts[i],  ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testReadFrom() throws OFParseError {
        for(int i=0; i < testAddresses.length; i++ ) {
            MacAddress ip = MacAddress.read6Bytes(ChannelBuffers.copiedBuffer(testAddresses[i]));
            assertEquals(testInts[i], ip.getLong());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }


    @Test
    public void testInvalidMacss() throws OFParseError {
        for(String invalid : invalidMacs) {
            try {
                MacAddress.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }
}

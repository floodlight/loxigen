package org.projectfloodlight.openflow.types;

import java.util.Arrays;

import org.junit.Test;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DatapathIdTest {

    byte[][] testDpids = new byte[][] {
            {0x0, 0x0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 },
            {0x0, 0x0, (byte) 0x80, 0x0, 0x0, 0x0, 0x0, 0x01},
            {0x0, 0x0, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 }
    };

    String[] testStrings = {
            "00:00:01:02:03:04:05:06",
            "00:00:80:00:00:00:00:01",
            "00:00:ff:ff:ff:ff:ff:ff"
    };

    long[] testInts = {
            0x000000010203040506L,
            0x000000800000000001L,
            0x000000ffffffffffffL
    };

    @Test
    public void testOfString() {
        for(int i=0; i < testDpids.length; i++ ) {
            DatapathId dpid = DatapathId.of(testStrings[i]);
            assertEquals(testInts[i], dpid.getLong());
            assertArrayEquals(testDpids[i], dpid.getBytes());
            assertEquals(testStrings[i], dpid.toString());
        }
    }

    @Test
    public void testOfByteArray() {
        for(int i=0; i < testDpids.length; i++ ) {
            DatapathId dpid = DatapathId.of(testDpids[i]);
            assertEquals("error checking long representation of "+Arrays.toString(testDpids[i]) + "(should be "+Long.toHexString(testInts[i]) +")", testInts[i],  dpid.getLong());
            assertArrayEquals(testDpids[i], dpid.getBytes());
            assertEquals(testStrings[i], dpid.toString());
        }
    }

    @Test
    public void testOfMacAddress() {

        for (String s : testStrings) {

            // Generate mac addresses by stripping off the front two bytes
            MacAddress mac = MacAddress.of(s.replaceFirst("00:00:", ""));

            // Create dpid from mac address
            DatapathId candidateDpid = DatapathId.of(mac);

            // Create dpid from string
            DatapathId actualDpid = DatapathId.of(s);

            assertThat(candidateDpid.equals(actualDpid), is(true));
        }
    }
}

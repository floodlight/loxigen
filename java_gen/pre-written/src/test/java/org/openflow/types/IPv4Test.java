package org.openflow.types;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.openflow.exceptions.OFParseError;
import org.openflow.exceptions.OFShortRead;

public class IPv4Test {
    byte[][] testAddresses = new byte[][] {
            {0x01, 0x02, 0x03, 0x04 },
            {127, 0, 0, 1},
            {(byte) 192, (byte) 168, 0, 100 },
            {(byte) 255, (byte) 255, (byte) 255, (byte) 255 }
    };

    String[] testStrings = {
            "1.2.3.4",
            "127.0.0.1",
            "192.168.0.100",
            "255.255.255.255"
    };

    int[] testInts = {
            0x01020304,
            0x7f000001,
            (192 << 24) | (168 << 16) | 100,
            0xffffffff
    };

    String[] invalidIPs = {
            "",
            ".",
            "1.2..3.4",
            "1.2.3.4.",
            "257.11.225.1",
            "-1.2.3.4",
            "1.2.3.4.5",
            "1.x.3.4",
            "1.2x.3.4"
    };
    
    String[] ipsWithMask = {
                            "1.2.3.4/24",
                            "192.168.130.140/255.255.192.0",
                            "127.0.0.1/8",
                            "8.8.8.8",
    };
    
    boolean[] hasMask = {
                         true,
                         true,
                         true,
                         false
    };
    
    byte[][][] ipsWithMaskValues = {
                             new byte[][] { new byte[] { (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04 }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0xC0, (byte)0xA8, (byte)0x82, (byte)0x8C }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xC0, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x01 }, new byte[] { (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0x08, (byte)0x08, (byte)0x08, (byte)0x08 }, null }
    };


    @Test
    public void testOfString() {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4 ip = IPv4.of(testStrings[i]);
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOfByteArray() {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4 ip = IPv4.of(testAddresses[i]);
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testReadFrom() throws OFParseError, OFShortRead {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4 ip = IPv4.read4Bytes(ChannelBuffers.copiedBuffer(testAddresses[i]));
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }


    @Test
    public void testInvalidIPs() throws OFParseError, OFShortRead {
        for(String invalid : invalidIPs) {
            try {
                IPv4.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testOfPossiblyMasked() throws OFParseError, OFShortRead {
        for (int i = 0; i < ipsWithMask.length; i++) {
            OFValueType value = IPv4WithMask.ofPossiblyMasked(ipsWithMask[i]);
            if (value instanceof IPv4 && !hasMask[i]) {
                // Types OK, check values
                IPv4 ip = (IPv4)value;
                assertArrayEquals(ipsWithMaskValues[i][0], ip.getBytes());
            } else if (value instanceof Masked && hasMask[i]) {
                Masked<IPv4> ip = null;
                try {
                    ip = (Masked<IPv4>)value;
                } catch (ClassCastException e) {
                    fail("Invalid Masked<T> type.");
                }
                // Types OK, check values
                assertArrayEquals(ipsWithMaskValues[i][0], ip.getValue().getBytes());
                assertArrayEquals(ipsWithMaskValues[i][1], ip.getMask().getBytes());
            } else if (value instanceof IPv4) {
                fail("Expected masked IPv4, got unmasked IPv4.");
            } else {
                fail("Expected unmasked IPv4, got masked IPv4.");
            }
        }
    }
}

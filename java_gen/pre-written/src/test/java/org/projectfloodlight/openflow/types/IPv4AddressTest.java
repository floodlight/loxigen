package org.projectfloodlight.openflow.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.netty.buffer.Unpooled;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.projectfloodlight.openflow.exceptions.OFParseError;

public class IPv4AddressTest {
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
            "256.11.225.1",
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
                            "8.8.8.8/32",
                            "0.0.0.0/0",
                            "192.168.130.140/255.0.255.0",
                            "1.2.3.4/0.127.0.255"
    };

    boolean[] hasMask = {
                         true,
                         true,
                         true,
                         false,
                         false,
                         true,
                         true,
                         true
    };

    byte[][][] ipsWithMaskValues = {
                             new byte[][] { new byte[] { (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04 }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0xC0, (byte)0xA8, (byte)0x82, (byte)0x8C }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xC0, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x01 }, new byte[] { (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0x08, (byte)0x08, (byte)0x08, (byte)0x08 }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF } },
                             new byte[][] { new byte[] { (byte)0x08, (byte)0x08, (byte)0x08, (byte)0x08 }, new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF } },
                             new byte[][] { new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 }, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0xC0, (byte)0xA8, (byte)0x82, (byte)0x8C }, new byte[] { (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00 } },
                             new byte[][] { new byte[] { (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04 }, new byte[] { (byte)0x00, (byte)0x7F, (byte)0x00, (byte)0xFF } }
    };

    int[] ipsWithMaskLengths = {
                                24,
                                18,
                                8,
                                32,
                                32,
                                0,
                                -1,
                                -1
    };

    String[] invalidIpsWithMask = {
                                   "asdf",
                                   "1.2.3.4/33",
                                   "1.2.3.4/34",
                                   "1.2.3.4/-1",
                                   "1.2.3.4/256.0.0.0",
                                   "1.256.3.4/255.255.0.0",
                                   "1.2.3.4/255.255.0.0.0",
    };

    @Test
    public void testLogicalOperatorsBroadcast() {
        assertTrue(IPv4Address.NO_MASK.not().equals(IPv4Address.FULL_MASK));
        assertTrue(IPv4Address.NO_MASK.or(IPv4Address.FULL_MASK).
                   equals(IPv4Address.NO_MASK));
        assertTrue(IPv4Address.NO_MASK.and(IPv4Address.FULL_MASK).
                   equals(IPv4Address.FULL_MASK));

        assertTrue(IPv4Address.NO_MASK.isBroadcast());
        assertTrue(!IPv4Address.FULL_MASK.isBroadcast());
    }

    @Test
    public void testMaskedSubnetBroadcast() {
        assertTrue(IPv4AddressWithMask.of("10.10.10.1/24")
                   .getSubnetBroadcastAddress()
                   .equals(IPv4Address.of("10.10.10.255")));
        assertTrue(IPv4AddressWithMask.of("10.10.10.1/24")
                   .isSubnetBroadcastAddress(IPv4Address.of("10.10.10.255")));
        assertTrue(!IPv4AddressWithMask.of("10.10.10.1/24")
                   .isSubnetBroadcastAddress(IPv4Address.of("10.10.10.254")));
    }

    @Test
    public void testMaskedMatchesCidr() {
        IPv4AddressWithMask slash28 = IPv4AddressWithMask.of("10.0.42.16/28");

        String[] notContained = {"0.0.0.0", "11.0.42.16", "10.0.41.1", "10.0.42.0", "10.0.42.15",
                                 "10.0.42.32", "255.255.255.255" };

        for(String n: notContained) {
            assertThat(String.format("slash 28 %s should not contain address %s",
                                     slash28, n),
                    slash28.matches(IPv4Address.of(n)), equalTo(false));
        }
        for(int i=16; i < 32; i++) {
            IPv4Address c = IPv4Address.of(String.format("10.0.42.%d", i));
            assertThat(String.format("slash 28 %s should contain address %s",
                                     slash28, c),
                       slash28.matches(c), equalTo(true));
        }
    }

    @Test
    public void testMaskedMatchesArbitrary() {
        // irregular octect on the 3rd bitmask requires '1'bit to be set
        // 4 bit unset, all others arbitrary
        IPv4AddressWithMask slash28 = IPv4AddressWithMask.of("1.2.1.4/255.255.5.255");

        String[] notContained = {"0.0.0.0", "1.2.3.5", "1.2.3.3",
                                 "1.2.0.4", "1.2.2.4", "1.2.4.4", "1.2.5.4", "1.2.6.4", "1.2.7.4",
                                 "1.2.8.4", "1.2.12.4", "1.2.13.4"
                                 };
        String[] contained = {"1.2.1.4", "1.2.3.4", "1.2.9.4", "1.2.11.4", "1.2.251.4",
                };

        for(String n: notContained) {
            assertThat(String.format("slash 28 %s should not contain address %s",
                                     slash28, n),
                    slash28.matches(IPv4Address.of(n)), equalTo(false));
        }
        for(String c: contained) {
            IPv4Address addr = IPv4Address.of(c);
            assertThat(String.format("slash 28 %s should contain address %s",
                                     slash28, addr),
                       slash28.matches(addr), equalTo(true));
        }

    }


    @Test
    public void testConstants() {
        byte[] zeros = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
        byte[] ones =  { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
        // Make sure class initializtation and static assignment don't get
        // messed up. Test everything twice for cached values
        assertTrue(IPv4Address.NONE.isCidrMask());
        assertEquals(0, IPv4Address.NONE.asCidrMaskLength());
        assertArrayEquals(zeros, IPv4Address.NONE.getBytes());
        assertTrue(IPv4Address.NONE.isCidrMask());
        assertEquals(0, IPv4Address.NONE.asCidrMaskLength());
        assertArrayEquals(zeros, IPv4Address.NONE.getBytes());

        assertTrue(IPv4Address.NO_MASK.isCidrMask());
        assertEquals(32, IPv4Address.NO_MASK.asCidrMaskLength());
        assertArrayEquals(ones, IPv4Address.NO_MASK.getBytes());
        assertTrue(IPv4Address.NO_MASK.isCidrMask());
        assertEquals(32, IPv4Address.NO_MASK.asCidrMaskLength());
        assertArrayEquals(ones, IPv4Address.NO_MASK.getBytes());

        assertTrue(IPv4Address.FULL_MASK.isCidrMask());
        assertEquals(0, IPv4Address.FULL_MASK.asCidrMaskLength());
        assertArrayEquals(zeros, IPv4Address.FULL_MASK.getBytes());
        assertTrue(IPv4Address.FULL_MASK.isCidrMask());
        assertEquals(0, IPv4Address.FULL_MASK.asCidrMaskLength());
        assertArrayEquals(zeros, IPv4Address.FULL_MASK.getBytes());
    }


    @Test
    public void testOfString() {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4Address ip = IPv4Address.of(testStrings[i]);
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOfByteArray() {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4Address ip = IPv4Address.of(testAddresses[i]);
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOf4Integers() {
        Map<IPv4Address, IPv4Address> map = new HashMap<>();
        map.put(IPv4Address.of(0, 0, 0, 0), IPv4Address.of(0));
        map.put(IPv4Address.of(1, 2, 3, 4), IPv4Address.of("1.2.3.4"));
        map.put(IPv4Address.of(6, 7, 8, 9), IPv4Address.of("6.7.8.9"));
        map.put(IPv4Address.of(10, 1, 2, 3), IPv4Address.of("10.1.2.3"));
        map.put(IPv4Address.of(10, 201, 202, 203), IPv4Address.of("10.201.202.203"));
        map.put(IPv4Address.of(192, 168, 0, 101), IPv4Address.of("192.168.0.101"));
        map.put(IPv4Address.of(211, 212, 213, 214), IPv4Address.of("211.212.213.214"));
        map.put(IPv4Address.of(255, 255, 255, 255), IPv4Address.of(0xFF_FF_FF_FF));
        for (Entry<IPv4Address, IPv4Address> entry : map.entrySet()) {
            assertThat(entry.getKey(), equalTo(entry.getValue()));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative1() {
        IPv4Address.of(-3, 4, 5, 6);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative2() {
        IPv4Address.of(3, -4, 5, 6);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative3() {
        IPv4Address.of(3, 4, -5, 6);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative4() {
        IPv4Address.of(3, 4, 5, -6);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative5() {
        // ((byte) 128) is actually -128
        IPv4Address.of(101, 102, 103, (byte) 128);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative6() {
        // ((byte) 255) is actually -1
        IPv4Address.of(101, 102, 103, (byte) 255);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionNegative7() {
        IPv4Address.of(-1, -1, -1, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionTooBig1() {
        IPv4Address.of(1000, 2, 3, 4);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionTooBig2() {
        IPv4Address.of(1, 20000, 3, 4);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionTooBig3() {
        IPv4Address.of(1, 2, 300000, 4);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionTooBig4() {
        IPv4Address.of(1, 2, 3, 4000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOf4IntegersExceptionTooBig5() {
        IPv4Address.of(256, 256, 256, 256);
    }

    @Test
    public void testOfCidrMaskLength() {
        for (int i = 0; i <= 32; i++) {
            assertEquals(IPv4Address.ofCidrMaskLength(i).asCidrMaskLength(), i);
        }

        assertEquals(IPv4Address.ofCidrMaskLength(0).getInt(), 0x0000_0000);

        assertEquals(IPv4Address.ofCidrMaskLength(1).getInt(), 0x8000_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(2).getInt(), 0xC000_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(3).getInt(), 0xE000_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(4).getInt(), 0xF000_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(5).getInt(), 0xF800_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(6).getInt(), 0xFC00_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(7).getInt(), 0xFE00_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(8).getInt(), 0xFF00_0000);

        assertEquals(IPv4Address.ofCidrMaskLength(9).getInt(), 0xFF80_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(10).getInt(), 0xFFC0_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(11).getInt(), 0xFFE0_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(12).getInt(), 0xFFF0_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(13).getInt(), 0xFFF8_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(14).getInt(), 0xFFFC_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(15).getInt(), 0xFFFE_0000);
        assertEquals(IPv4Address.ofCidrMaskLength(16).getInt(), 0xFFFF_0000);

        assertEquals(IPv4Address.ofCidrMaskLength(17).getInt(), 0xFFFF_8000);
        assertEquals(IPv4Address.ofCidrMaskLength(18).getInt(), 0xFFFF_C000);
        assertEquals(IPv4Address.ofCidrMaskLength(19).getInt(), 0xFFFF_E000);
        assertEquals(IPv4Address.ofCidrMaskLength(20).getInt(), 0xFFFF_F000);
        assertEquals(IPv4Address.ofCidrMaskLength(21).getInt(), 0xFFFF_F800);
        assertEquals(IPv4Address.ofCidrMaskLength(22).getInt(), 0xFFFF_FC00);
        assertEquals(IPv4Address.ofCidrMaskLength(23).getInt(), 0xFFFF_FE00);
        assertEquals(IPv4Address.ofCidrMaskLength(24).getInt(), 0xFFFF_FF00);

        assertEquals(IPv4Address.ofCidrMaskLength(25).getInt(), 0xFFFF_FF80);
        assertEquals(IPv4Address.ofCidrMaskLength(26).getInt(), 0xFFFF_FFC0);
        assertEquals(IPv4Address.ofCidrMaskLength(27).getInt(), 0xFFFF_FFE0);
        assertEquals(IPv4Address.ofCidrMaskLength(28).getInt(), 0xFFFF_FFF0);
        assertEquals(IPv4Address.ofCidrMaskLength(29).getInt(), 0xFFFF_FFF8);
        assertEquals(IPv4Address.ofCidrMaskLength(30).getInt(), 0xFFFF_FFFC);
        assertEquals(IPv4Address.ofCidrMaskLength(31).getInt(), 0xFFFF_FFFE);
        assertEquals(IPv4Address.ofCidrMaskLength(32).getInt(), 0xFFFF_FFFF);
    }

    @Test
    public void testWithMask() throws Exception {
        // Sanity tests for the withMask*() syntactic sugars

        IPv4Address original = IPv4Address.of("192.168.1.101");
        IPv4Address expectedValue = IPv4Address.of("192.168.1.0");
        IPv4Address expectedMask = IPv4Address.of("255.255.255.0");

        IPv4AddressWithMask v;

        v = original.withMask(IPv4Address.of(new byte[] {-1, -1, -1, 0}));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMask(IPv4Address.of(0xFFFF_FF00));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMask(IPv4Address.of("255.255.255.0"));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        Inet4Address i4a = (Inet4Address) InetAddress.getByName("255.255.255.0");
        v = original.withMask(IPv4Address.of(i4a));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMaskOfLength(24);
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);
    }

    @Test
    public void testReadFrom() throws OFParseError {
        for(int i=0; i < testAddresses.length; i++ ) {
            IPv4Address ip = IPv4Address.read4Bytes(Unpooled.copiedBuffer(testAddresses[i]));
            assertEquals(testInts[i], ip.getInt());
            assertArrayEquals(testAddresses[i], ip.getBytes());
            assertEquals(testStrings[i], ip.toString());
        }
    }


    @Test
    public void testInvalidIPs() throws OFParseError {
        for(String invalid : invalidIPs) {
            try {
                IPv4Address.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    @Test
    public void testOfMasked() throws OFParseError {
        for (int i = 0; i < ipsWithMask.length; i++) {
            IPv4AddressWithMask value = IPv4AddressWithMask.of(ipsWithMask[i]);
            if (!hasMask[i]) {
                IPv4Address ip = value.getValue();
                assertArrayEquals(ipsWithMaskValues[i][0], ip.getBytes());
            }
            IPv4Address mask = value.getMask();
            if (ipsWithMaskLengths[i] == -1) {
                assertFalse(mask.isCidrMask());
                try {
                    mask.asCidrMaskLength();
                    fail("Expected IllegalStateException not thrown");
                } catch(IllegalStateException e) {
                    //expected
                }
            } else {
                assertTrue(mask.isCidrMask());
                assertEquals(ipsWithMaskLengths[i], mask.asCidrMaskLength());
            }
            assertArrayEquals(ipsWithMaskValues[i][1], mask.getBytes());
            byte[] ipBytes = new byte[4];
            System.arraycopy(ipsWithMaskValues[i][0], 0, ipBytes, 0, 4);
            assertEquals(ipBytes.length, value.getValue().getBytes().length);
            for (int j = 0; j < ipBytes.length; j++) {
                ipBytes[j] &= ipsWithMaskValues[i][1][j];
            }

            assertArrayEquals(ipBytes, value.getValue().getBytes());
            assertThat(String.format("Byte comparison for mask of %s (%s)", ipsWithMask[i], value),
                    value.getMask().getBytes(), CoreMatchers.equalTo(ipsWithMaskValues[i][1]));
        }
    }

    @Test
    public void testOfMaskedInvalid() throws Exception {
        for(String invalid : invalidIpsWithMask) {
            try {
                IPv4Address.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    @Test
    public void testSuperclass() throws Exception {
        for(String ipString: testStrings) {
            IPAddress<?> superIp = IPAddress.of(ipString);
            assertEquals(IPVersion.IPv4, superIp.getIpVersion());
            assertEquals(IPv4Address.of(ipString), superIp);
        }

        for(String ipMaskedString: ipsWithMask) {
            IPAddressWithMask<?> superIp = IPAddressWithMask.of(ipMaskedString);
            assertEquals(IPVersion.IPv4, superIp.getIpVersion());
            assertEquals(IPv4AddressWithMask.of(ipMaskedString), superIp);
        }
    }

    @Test
    public void testCompareTo() {
        assertThat(
                IPv4Address.of("1.0.0.1").compareTo(IPv4Address.of("1.0.0.2")),
                Matchers.lessThan(0));
        assertThat(
                IPv4Address.of("1.0.0.3").compareTo(IPv4Address.of("3.0.0.1")),
                Matchers.lessThan(0));

        // Make sure that unsigned comparison is used
        assertThat(
                IPv4Address.of("201.0.0.1").compareTo(IPv4Address.NONE),
                Matchers.greaterThan(0));
    }

    @Test
    public void testOfExceptions() {
        // We check if the message of a caught NPE is set to a useful message
        // as a hacky way of verifying that we got an NPE thrown by use rather
        // than one the JVM created for a null access.
        try {
            String s = null;
            IPv4Address.of(s);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            byte[] b = null;
            IPv4Address.of(b);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            byte[] b = new byte[3];
            IPv4Address.of(b);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            byte[] b = new byte[5];
            IPv4Address.of(b);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IPv4AddressWithMask.of(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv4AddressWithMask.of(IPv4Address.of("1.2.3.4"), null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv4AddressWithMask.of(null, IPv4Address.of("255.0.0.0"));
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv4AddressWithMask.of(IPv4Address.of("10.10.10.0"),
                                   IPv4Address.of("255.0.255.0"))
                                   .getSubnetBroadcastAddress();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv4Address.ofCidrMaskLength(-1);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv4Address.ofCidrMaskLength(33);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}

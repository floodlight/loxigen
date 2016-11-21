package org.projectfloodlight.openflow.types;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.netty.buffer.Unpooled;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.io.BaseEncoding;

public class IPv6AddressTest {

    String[] testStrings = {
            "::",
            "::1",
            "ffe0::",
            "1:2:3:4:5:6:7:8",
            "8091:a2b3:c4d5:e6f7:8495:a6b7:c1d2:e3d4",
    };


    private final BaseEncoding hex = BaseEncoding.base16().omitPadding().lowerCase();

    private class WithMaskTaskCase {
        final String input;
        boolean hasMask;
        int expectedMaskLength = 128;
        byte[] expectedMask = hex.decode("ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff".replaceAll(" ", ""));

        public WithMaskTaskCase(String input) {
            super();
            this.input = input;
        }

        public WithMaskTaskCase maskHex(String string) {
            string = string.replaceAll(" ", "");
            this.hasMask = true;
            expectedMask = hex.decode(string);
            return this;
        }

        public WithMaskTaskCase expectedMaskLength(int expectedLength) {
            this.expectedMaskLength = expectedLength;
            return this;
        }

    }

    WithMaskTaskCase[] withMasks = new WithMaskTaskCase[] {
            new WithMaskTaskCase("1::1/80")
                .maskHex("ff ff ff ff ff ff ff ff ff ff 00 00 00 00 00 00")
                .expectedMaskLength(80),

            new WithMaskTaskCase("ffff:ffee:1::/ff00:ff00:ff00:ff00::")
                .maskHex("ff 00 ff 00 ff 00 ff 00 00 00 00 00 00 00 00 00")
                .expectedMaskLength(-1),
            new WithMaskTaskCase("1:2:3:4:5:6:7:8/1::ff00:ff00")
                .maskHex("00 01 00 00 00 00 00 00 00 00 00 00 ff 00 ff 00")
                .expectedMaskLength(-1),
            new WithMaskTaskCase("1:2:3:4:5:6:7:8/::ff00:ff00")
                .maskHex("00 00 00 00 00 00 00 00 00 00 00 00 ff 00 ff 00")
                .expectedMaskLength(-1),
            new WithMaskTaskCase("1:2:3:4:5:6:7:8/ffff:ffff:ffff:ffff:ffff::ff00:ff00")
                .maskHex("ff ff ff ff ff ff ff ff ff ff 00 00 ff 00 ff 00")
                .expectedMaskLength(-1),
            new WithMaskTaskCase("8:8:8:8:8:8:8:8"),
            new WithMaskTaskCase("8:8:8:8:8:8:8:8"),
            new WithMaskTaskCase("1:2:3:4:5:6:7:8/128"),
            new WithMaskTaskCase("::/0")
                .maskHex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00")
                .expectedMaskLength(0),
    };

    @Test
    public void testLogicalOperatorsBroadcast() {
        assertTrue(IPv6Address.NO_MASK.not().equals(IPv6Address.FULL_MASK));
        assertTrue(IPv6Address.NO_MASK.or(IPv6Address.FULL_MASK).
                   equals(IPv6Address.NO_MASK));
        assertTrue(IPv6Address.NO_MASK.and(IPv6Address.FULL_MASK).
                   equals(IPv6Address.FULL_MASK));

        assertTrue(IPv6Address.NO_MASK.isBroadcast());
        assertTrue(!IPv6Address.FULL_MASK.isBroadcast());
    }

    @Test
    public void testMaskedSubnetBroadcast() {
        assertTrue(IPv6AddressWithMask.of("10:10::1/112")
                   .getSubnetBroadcastAddress()
                   .equals(IPv6Address.of("10:10::ffff")));
        assertTrue(IPv6AddressWithMask.of("10:10::1/112")
                   .isSubnetBroadcastAddress(IPv6Address.of("10:10::ffff")));
        assertTrue(!IPv6AddressWithMask.of("10:10::1/112")
                   .isSubnetBroadcastAddress(IPv6Address.of("10:10::fffd")));
    }

    @Test
    public void testConstants() {
        byte[] zeros = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                         (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                         (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                         (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
        byte[] ones = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
        // Make sure class initializtation and static assignment don't get
        // messed up. Test everything twice for cached values
        assertTrue(IPv6Address.NONE.isCidrMask());
        assertEquals(0, IPv6Address.NONE.asCidrMaskLength());
        assertArrayEquals(zeros, IPv6Address.NONE.getBytes());
        assertTrue(IPv6Address.NONE.isCidrMask());
        assertEquals(0, IPv6Address.NONE.asCidrMaskLength());
        assertArrayEquals(zeros, IPv6Address.NONE.getBytes());

        assertTrue(IPv6Address.NO_MASK.isCidrMask());
        assertEquals(128, IPv6Address.NO_MASK.asCidrMaskLength());
        assertArrayEquals(ones, IPv6Address.NO_MASK.getBytes());
        assertTrue(IPv6Address.NO_MASK.isCidrMask());
        assertEquals(128, IPv6Address.NO_MASK.asCidrMaskLength());
        assertArrayEquals(ones, IPv6Address.NO_MASK.getBytes());

        assertTrue(IPv6Address.FULL_MASK.isCidrMask());
        assertEquals(0, IPv6Address.FULL_MASK.asCidrMaskLength());
        assertArrayEquals(zeros, IPv6Address.FULL_MASK.getBytes());
        assertTrue(IPv6Address.FULL_MASK.isCidrMask());
        assertEquals(0, IPv6Address.FULL_MASK.asCidrMaskLength());
        assertArrayEquals(zeros, IPv6Address.FULL_MASK.getBytes());
    }

    @Test
    public void testMasked() throws UnknownHostException {
        for(WithMaskTaskCase w: withMasks) {
            IPv6AddressWithMask value = IPv6AddressWithMask.of(w.input);
            if (!w.hasMask) {
                IPv6Address ip = value.getValue();
                InetAddress inetAddress = InetAddress.getByName(w.input.split("/")[0]);

                assertArrayEquals(ip.getBytes(), inetAddress.getAddress());
                assertEquals(w.input.split("/")[0], ip.toString());
            }
            InetAddress inetAddress = InetAddress.getByName(w.input.split("/")[0]);

            if (w.expectedMaskLength == -1) {
                assertFalse(value.getMask().isCidrMask());
                try {
                    value.getMask().asCidrMaskLength();
                    fail("Expected IllegalStateException not thrown");
                } catch(IllegalStateException e) {
                    //expected
                }
            } else {
                assertTrue(value.getMask().isCidrMask());
                assertEquals("Input " + w.input, w.expectedMaskLength,
                             value.getMask().asCidrMaskLength());
            }

            byte[] address = inetAddress.getAddress();
            assertEquals(address.length, value.getValue().getBytes().length);

            for (int j = 0; j < address.length; j++) {
                address[j] &= w.expectedMask[j];
            }

            assertThat("Address bytes for input " + w.input + ", value=" + value, value.getValue().getBytes(), CoreMatchers.equalTo(address));
            assertThat("mask check for input " + w.input + ", value=" + value, value.getMask().getBytes(), CoreMatchers.equalTo(w.expectedMask));
        }
        for (int i = 0; i <= 128; i++) {
            String ipString = String.format("8001:2::1/%d", i);
            IPv6AddressWithMask value = IPv6AddressWithMask.of(ipString);
            assertEquals("Input " + ipString, i, value.getMask().asCidrMaskLength());
        }
    }


    @Test
    public void testOfString() throws UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            IPv6Address ip = IPv6Address.of(testStrings[i]);
            InetAddress inetAddress = InetAddress.getByName(testStrings[i]);

            assertArrayEquals(ip.getBytes(), inetAddress.getAddress());
            assertEquals(testStrings[i], ip.toString());
        }
    }

    @Test
    public void testOfByteArray() throws UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            byte[] bytes = Inet6Address.getByName(testStrings[i]).getAddress();
            IPv6Address ip = IPv6Address.of(bytes);
            assertEquals(testStrings[i], ip.toString());
            assertArrayEquals(bytes, ip.getBytes());
        }
    }

    private static void testOfCidrMaskLengthHelper(
            int cidrMaskLength, String ipStr) throws UnknownHostException {
        byte[] ba0 = IPv6Address.ofCidrMaskLength(cidrMaskLength).getBytes();
        byte[] ba1 = Inet6Address.getByName(ipStr).getAddress();
        assertArrayEquals(ba0, ba1);
    }

    @Test
    public void testOfCidrMaskLength() throws UnknownHostException {
        for (int i = 0; i <= 128; i++) {
            assertTrue(IPv6Address.ofCidrMaskLength(i).isCidrMask());
            assertEquals(IPv6Address.ofCidrMaskLength(i).asCidrMaskLength(), i);
        }
        testOfCidrMaskLengthHelper(0, "::");
        testOfCidrMaskLengthHelper(1, "8000::");
        testOfCidrMaskLengthHelper(2, "c000::");
        testOfCidrMaskLengthHelper(8, "ff00::");
        testOfCidrMaskLengthHelper(16, "ffff::");
        testOfCidrMaskLengthHelper(17, "ffff:8000::");
        testOfCidrMaskLengthHelper(31, "ffff:fffe::");
        testOfCidrMaskLengthHelper(32, "ffff:ffff::");
        testOfCidrMaskLengthHelper(33, "ffff:ffff:8000::");
        testOfCidrMaskLengthHelper(46, "ffff:ffff:fffc::");
        testOfCidrMaskLengthHelper(48, "ffff:ffff:ffff::");
        testOfCidrMaskLengthHelper(55, "ffff:ffff:ffff:fe00::");
        testOfCidrMaskLengthHelper(56, "ffff:ffff:ffff:ff00::");
        testOfCidrMaskLengthHelper(59, "ffff:ffff:ffff:ffe0::");
        testOfCidrMaskLengthHelper(63, "ffff:ffff:ffff:fffe::");
        testOfCidrMaskLengthHelper(64, "ffff:ffff:ffff:ffff::");
        testOfCidrMaskLengthHelper(65, "ffff:ffff:ffff:ffff:8000::");
        testOfCidrMaskLengthHelper(67, "ffff:ffff:ffff:ffff:e000::");
        testOfCidrMaskLengthHelper(100, "ffff:ffff:ffff:ffff:ffff:ffff:f000::");
        testOfCidrMaskLengthHelper(101, "ffff:ffff:ffff:ffff:ffff:ffff:f800::");
        testOfCidrMaskLengthHelper(126, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffc");
        testOfCidrMaskLengthHelper(127, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe");
        testOfCidrMaskLengthHelper(128, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    }

    @Test
    public void testWithMask() throws Exception {
        // Sanity tests for the withMask*() syntactic sugars

        IPv6Address original = IPv6Address.of("fd12:3456:ABCD:7890::1");
        IPv6Address expectedValue = IPv6Address.of("fd12:3456:ABCD::");
        IPv6Address expectedMask = IPv6Address.of("ffff:ffff:ffff::");

        IPv6AddressWithMask v;

        v = original.withMask(IPv6Address.of(new byte[] {
                -1, -1, -1, -1, -1, -1, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0 }));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMask(IPv6Address.of(
                0xFFFF_FFFF_FFFF_0000L, 0x0000_0000_0000_0000L));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMask(IPv6Address.of("ffff:ffff:ffff::"));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        Inet6Address i6a = (Inet6Address) InetAddress.getByName("ffff:ffff:ffff::");
        v = original.withMask(IPv6Address.of(i6a));
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);

        v = original.withMaskOfLength(48);
        assertEquals(v.getValue(), expectedValue);
        assertEquals(v.getMask(), expectedMask);
    }

    @Test
    public void testReadFrom() throws OFParseError, UnknownHostException {
        for(int i=0; i < testStrings.length; i++ ) {
            byte[] bytes = Inet6Address.getByName(testStrings[i]).getAddress();
            IPv6Address ip = IPv6Address.read16Bytes(Unpooled.copiedBuffer(bytes));
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
    public void testInvalidIPs() throws OFParseError {
        for(String invalid : invalidIPs) {
            try {
                IPv6Address.of(invalid);
                fail("Invalid IP "+invalid+ " should have raised IllegalArgumentException");
            } catch(IllegalArgumentException e) {
                // ok
            }
        }
    }

    @Test
    public void testZeroCompression() throws OFParseError {
        assertEquals("::", IPv6Address.of("::").toString(true, false));
        assertEquals("0:0:0:0:0:0:0:0", IPv6Address.of("::").toString(false, false));
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0000", IPv6Address.of("::").toString(false, true));
        assertEquals("1::4:5:6:0:8", IPv6Address.of("1:0:0:4:5:6:0:8").toString(true, false));
        assertEquals("1:0:0:4::8", IPv6Address.of("1:0:0:4:0:0:0:8").toString(true, false));
        // Two equal length zero runs; should zero compress the first instance
        assertEquals("1::4:2:0:0:8", IPv6Address.of("1:0:0:4:2:0:0:8").toString(true, false));
        // Shouldn't zero compress a single zero
        assertEquals("1:0:2:4:3:1:0:8", IPv6Address.of("1:0:2:4:3:1:0:8").toString(true, false));
        // Test zero runs at the end of the address since that's a different code path in toString
        assertEquals("1::4:2:8:0:0", IPv6Address.of("1:0:0:4:2:8:0:0").toString(true, false));
        assertEquals("1:3:2:4:3:1:5:0", IPv6Address.of("1:3:2:4:3:1:5:0").toString(true, false));
    }

    @Test
    public void testSuperclass() throws Exception {
        for(String ipString: testStrings) {
            IPAddress<?> superIp = IPAddress.of(ipString);
            assertEquals(IPVersion.IPv6, superIp.getIpVersion());
            assertEquals(IPv6Address.of(ipString), superIp);
        }

        for(WithMaskTaskCase w: withMasks) {
            String ipMaskedString = w.input;
            IPAddressWithMask<?> superIp = IPAddressWithMask.of(ipMaskedString);
            assertEquals(IPVersion.IPv6, superIp.getIpVersion());
            assertEquals(IPv6AddressWithMask.of(ipMaskedString), superIp);
        }
    }

    @Test
    public void testCompareTo() {
        assertThat(
                IPv6Address.of("fc00::1").compareTo(IPv6Address.of("fc00::2")),
                Matchers.lessThan(0));
        assertThat(
                IPv6Address.of("::1").compareTo(IPv6Address.of("fc00::")),
                Matchers.lessThan(0));

        // Make sure that unsigned comparison is used on the first 64 bits
        assertThat(
                IPv6Address.of("fc00::1").compareTo(IPv6Address.of("1234::3")),
                Matchers.greaterThan(0));

        // Make sure that unsigned comparison is used on the next 64 bits
        assertThat(
                IPv6Address.of("::8000:0:0:1").compareTo(IPv6Address.of("::1")),
                Matchers.greaterThan(0));
    }

    @Test
    public void testOfExceptions() throws Exception {
        try {
            IPv6AddressWithMask.of(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            String s = null;
            IPv6Address.of(s);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            byte[] b = null;
            IPv6Address.of(b);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            byte[] b = new byte[7];
            IPv6Address.of(b);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            byte[] b = new byte[9];
            IPv6Address.of(b);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IPv6AddressWithMask.of(IPv6Address.of("1::"), null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv6AddressWithMask.of(null, IPv6Address.of("255::"));
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv6AddressWithMask.of(IPv6Address.of("10:10::0"),
                                   IPv6Address.of("ffff:0:ffff::"))
                                   .getSubnetBroadcastAddress();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv6Address.ofCidrMaskLength(-1);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            IPv6Address.ofCidrMaskLength(129);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testZoneId() throws OFParseError {
        assertEquals("::", IPv6Address.of("::%eth0").toString(true, false));
        assertEquals("1:0:0:4::8", IPv6Address.of("1:0:0:4:0:0:0:8%2").toString(true, false));
    }

    private void testModifiedEui64(
            String network, String macAddress, String expectedIpAddress) {

        IPv6AddressWithMask networkObj = IPv6AddressWithMask.of(network);
        MacAddress macAddressObj = MacAddress.of(macAddress);
        MacAddress macAddressAdd1 = MacAddress.of(
                macAddressObj.getLong() + 1 & 0xFFFF_FFFF_FFFFL);
        MacAddress macAddressSub1 = MacAddress.of(
                macAddressObj.getLong() - 1 & 0xFFFF_FFFF_FFFFL);
        IPv6Address ipAddressObj = IPv6Address.of(networkObj, macAddressObj);
        IPv6Address expectedObj = IPv6Address.of(expectedIpAddress);

        assertThat(ipAddressObj, is(expectedObj));
        assertThat(ipAddressObj.isModifiedEui64Derived(macAddressObj), is(true));
        assertThat(ipAddressObj.isModifiedEui64Derived(macAddressAdd1), is(false));
        assertThat(ipAddressObj.isModifiedEui64Derived(macAddressSub1), is(false));
    }

    @Test
    public void testModifiedEui64() throws Exception {
        testModifiedEui64(
                "fe80::/64", "00:00:00:00:00:00", "fe80::0200:ff:fe00:0");
        testModifiedEui64(
                "fe80::/64", "12:34:56:78:9a:bc", "fe80::1034:56ff:fe78:9abc");
        testModifiedEui64(
                "fe80::/64", "ff:ff:ff:ff:ff:ff", "fe80::fdff:ffff:feff:ffff");
        testModifiedEui64(
                "fe80::/10", "5c:16:c7:12:34:56", "fe80::5e16:c7ff:fe12:3456");
        testModifiedEui64(
                "2001:db8:9876:5400::/56", "00:0c:29:ab:cd:ef",
                "2001:db8:9876:5400:20c:29ff:feab:cdef");
        testModifiedEui64(
                "fd00:9999:8888::/48", "52:54:00:78:56:34",
                "fd00:9999:8888::5054:00ff:fe78:5634");
        testModifiedEui64(
                "2001:db8:7777:aabb::/64", "14:10:9f:00:00:cd",
                "2001:db8:7777:aabb:1610:9fff:fe00:00cd");
    }

    @Test(expected=NullPointerException.class)
    public void testModifiedEui64ExceptionNullNetwork() {
        IPv6Address.of(
                (IPv6AddressWithMask) null,
                MacAddress.of("00:50:56:12:34:56"));
    }

    @Test(expected=NullPointerException.class)
    public void testModifiedEui64ExceptionNullMacAddress() {
        IPv6Address.of(
                IPv6AddressWithMask.LINK_LOCAL_NETWORK,
                (MacAddress) null);
    }

    @Test(expected=NullPointerException.class)
    public void testModifiedEui64ExceptionNullDerivedMacAddress() {
        IPv6Address.NONE.isModifiedEui64Derived(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testModifiedEui64ExceptionPrefixLengthNo() {
        IPv6Address.of(
                IPv6Address.of("fef::").withMask(IPv6Address.of("f0f0:1234::")),
                MacAddress.of("5c:16:c7:00:00:00"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testModifiedEui64ExceptionPrefixLengthTooLong() {
        IPv6Address.of(
                IPv6AddressWithMask.of("fe80::/65"),
                MacAddress.of("5c:16:c7:00:00:00"));
    }
}

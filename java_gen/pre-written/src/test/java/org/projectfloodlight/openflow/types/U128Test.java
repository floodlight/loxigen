package org.projectfloodlight.openflow.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class U128Test {
    @Test
    public void testPositiveRaws() {
        assertThat(U128.of(0, 0).getMsb(), equalTo(0L));
        assertThat(U128.of(0, 0).getLsb(), equalTo(0L));

        assertThat(U128.of(1, 2).getMsb(), equalTo(1L));
        assertThat(U128.of(1, 2).getLsb(), equalTo(2L));
    }

    @Test
    public void testPutTo() {
        U128 h         = U128.of(0x1234_5678_90ab_cdefL,0xdeafbeefdeadbeefL);
        U128 hSame     = U128.of(0x1234_5678_90ab_cdefL,0xdeafbeefdeadbeefL);

        U128 hBothDiff = U128.of(0x1234_5678_90ab_cdefL,0x1234_5678_90ab_cdefL);
        U128 hMsbDiff  = U128.of(0x0234_5678_90ab_cdefL,0xdeafbeefdeadbeefL);
        U128 hLsbDiff  = U128.of(0x1234_5678_90ab_cdefL,0xdeafbeefdeadbeeeL);

        assertThat(hash(h), equalTo(hash(hSame)));
        assertThat(hash(h), not(hash(hBothDiff)));
        assertThat(hash(h), not(hash(hMsbDiff)));
        assertThat(hash(h), not(hash(hLsbDiff)));
    }

    private HashCode hash(U128 f) {
        Hasher hash = Hashing.murmur3_128().newHasher();
        f.putTo(hash);
        return hash.hash();

    }

    @Test
    public void testEqualHashCode() {
        U128 h1 = U128.of(0xdeafbeefdeadbeefL, 0xdeafbeefdeadbeefL);
        U128 h2 = U128.of(0xdeafbeefdeadbeefL, 0xdeafbeefdeadbeefL);
        U128 h3 = U128.of(0xeeafbeefdeadbeefL, 0xdeafbeefdeadbeefL);
        U128 h3_2 = U128.of(0xdeafbeefdeadbeefL, 0xeeafbeefdeadbeefL);

        assertTrue(h1.equals(h1));
        assertTrue(h1.equals(h2));
        assertFalse(h1.equals(h3));
        assertFalse(h1.equals(h3_2));
        assertTrue(h2.equals(h1));

        assertEquals(h1.hashCode(), h2.hashCode());
        assertNotEquals(h1.hashCode(), h3.hashCode()); // not technically a requirement, but we'll hopefully be lucky.
        assertNotEquals(h1.hashCode(), h3_2.hashCode()); // not technically a requirement, but we'll hopefully be lucky.
    }

    @Test
    public void testXor() {
        U128 hNull = U128.of(0, 0);
        U128 hDeadBeef = U128.of(0xdeafbeefdeadbeefL, 0xdeafbeefdeadbeefL);
        assertThat(hNull.xor(hNull), equalTo(hNull));
        assertThat(hNull.xor(hDeadBeef), equalTo(hDeadBeef));
        assertThat(hDeadBeef.xor(hNull), equalTo(hDeadBeef));
        assertThat(hDeadBeef.xor(hDeadBeef), equalTo(hNull));


        U128 h1_0 = U128.of(1L, 0);
        U128 h8_0 = U128.of(0x8000000000000000L, 0);
        U128 h81_0 = U128.of(0x8000000000000001L, 0);
        assertThat(h1_0.xor(h8_0), equalTo(h81_0));

        U128 h0_1 = U128.of(0, 1L);
        U128 h0_8 = U128.of(0, 0x8000000000000000L);
        U128 h0_81 = U128.of(0, 0x8000000000000001L);
        assertThat(h0_1.xor(h0_8), equalTo(h0_81));
    }

    @Test
    public void testCombine() {
        long key = 0x1234567890abcdefL;
        long val = 0xdeafbeefdeadbeefL;
        U128 hkey = U128.of(key, key*2);
        U128 hVal = U128.of(val, val/2);

        assertThat(hkey.combineWithValue(hVal, 0), equalTo(hkey.xor(hVal)));
        assertThat(hkey.combineWithValue(hVal, 64), equalTo(U128.of(hkey.getMsb(), hkey.getLsb() ^ hVal.getLsb())));
        assertThat(hkey.combineWithValue(hVal, 128), equalTo(hkey));

        long mask8 = 0xFF00_0000_0000_0000L;

        assertThat(hkey.combineWithValue(hVal, 8), equalTo(U128.of(hkey.getMsb() & mask8 |  hkey.getMsb() ^ hVal.getMsb() & ~mask8,
                                                                   hkey.getLsb() ^ hVal.getLsb() )));
    }

}

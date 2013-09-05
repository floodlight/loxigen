package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class U64Test {

    @Test
    public void testPositiveRaws() {
        for(long positive: new long[] { 0, 1, 100, Long.MAX_VALUE }) {
            assertEquals(positive, U64.ofRaw(positive).getValue());
            assertEquals(BigInteger.valueOf(positive), U64.ofRaw(positive).getBigInteger());
        }
    }

    @Test
    public void testNegativeRaws() {
        long minus_1 = 0xFFffFFffFFffFFffL;
        assertEquals(minus_1, U64.ofRaw(minus_1).getValue());
        assertEquals(new BigInteger("FFffFFffFFffFFff", 16),  U64.ofRaw(minus_1).getBigInteger());
        assertEquals(new BigInteger("18446744073709551615"),  U64.ofRaw(minus_1).getBigInteger());
    }
}

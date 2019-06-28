package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class U32Test {

    @Test
    public void normalize() {
        assertEquals(0,           U32.normalize((short) 0));
        assertEquals(1,           U32.normalize((short) 1));
        assertEquals(2147483647L, U32.normalize(2147483647L));
        assertEquals(2147483648L, U32.normalize(2147483648L));
        assertEquals(4294967295L, U32.normalize((short) 4294967295L));
        assertEquals(0L,          U32.normalize((short) 4294967296L));
        assertEquals(0xFFFF_FFFFL, U32.normalize(-1));
    }
}

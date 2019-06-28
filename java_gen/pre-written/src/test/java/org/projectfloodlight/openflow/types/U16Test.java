package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class U16Test {

    @Test
    public void normalize() {
        assertEquals(0,     U16.normalize((short) 0));
        assertEquals(1,     U16.normalize((short) 1));
        assertEquals(32767, U16.normalize((short) 32767));
        assertEquals(32768, U16.normalize((short) 32768));
        assertEquals(65535, U16.normalize((short) 65535));
        assertEquals(0,     U16.normalize((short) 65536));
        assertEquals(65535, U16.normalize((short) -1));
    }

}

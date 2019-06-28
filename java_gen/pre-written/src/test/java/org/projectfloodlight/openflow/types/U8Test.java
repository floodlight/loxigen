package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class U8Test {

    @Test
    public void normalize() {
        assertEquals((short)0, U8.normalize((short) 0));
        assertEquals((short)1, U8.normalize((short) 1));
        assertEquals((short)127, U8.normalize((short) 127));
        assertEquals((short)128, U8.normalize((short) 128));
        assertEquals((short)254, U8.normalize((short) 254));
        assertEquals((short)255, U8.normalize((short) 255));
        assertEquals((short)255, U8.normalize((short) -1));
    }
}

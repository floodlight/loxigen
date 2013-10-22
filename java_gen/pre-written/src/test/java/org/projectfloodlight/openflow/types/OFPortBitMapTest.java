package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertArrayEquals;
import junit.framework.TestCase;

import org.junit.Test;

public class OFPortBitMapTest extends TestCase {

    @Test
    public void testOFPortBitMap() {
        Boolean[] on = new Boolean[127];
        for (int i = 0; i < 127; i++) {
            on[i] = false;
        }

        OFPortBitMap.Builder builder = new OFPortBitMap.Builder();

        for (int i = 0; i < 127; i += 3) {
            OFPort p = OFPort.of(i);
            builder.set(p);
            on[p.getPortNumber()] = true;
        }

        // Test that all ports that were added are actually on, and all other ports are off
        OFPortBitMap portmap = builder.build();
        //System.out.println(portmap);
        Boolean[] actual = new Boolean[127];
        for (int i = 0; i < 127; i++) {
            actual[i] = false;
        }
        for (int i = 0; i < 127; i++) {
            actual[i] = portmap.isOn(OFPort.of(i));
        }
        assertArrayEquals(on, actual);

        // Turn some ports off
        for (int i = 0; i < 127; i += 7) {
            on[i] = false;
            builder.unset(OFPort.of(i));
        }

        // Test again
        portmap = builder.build();
        actual = new Boolean[127];
        for (int i = 0; i < 127; i++) {
            actual[i] = false;
        }
        for (int i = 0; i < 127; i++) {
            actual[i] = portmap.isOn(OFPort.of(i));
        }
        assertArrayEquals(on, actual);
    }
}

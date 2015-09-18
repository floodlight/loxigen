package org.projectfloodlight.openflow.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


public class VxlanNITest {

    @Test
    public void testValidMaxVni() {
        VxlanNI.ofVni(0x00ffffff);
    }

    @Test
    public void testValidMinVni() {
        VxlanNI.ofVni(0x0000001);
    }

    @Test
    public void testVniEquals() {
        assertEquals((int)VxlanNI.ofVni(0x01).getVni(), 0x01);
        assertEquals((int)VxlanNI.ofVni(0x00ffFFff).getVni(), 0x00ffFFff);
    }

    @Test
    public void testVniObjects() {
        VxlanNI o = VxlanNI.ofVni(0x00ffFFff);
        VxlanNI m = VxlanNI.ofVni(0x00ffFFff);
        assertEquals(o.compareTo(m), 0);
        assertEquals(o.getVni(), m.getVni());
        assertFalse(!o.equals(m));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVni1() {
        VxlanNI.ofVni(0xFFffFFff);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVni2() {
        VxlanNI.ofVni(0x0FffFFff);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVni3() {
        VxlanNI.ofVni(0x01FffFFff);
    }
}

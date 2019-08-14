package org.projectfloodlight.protocol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFVersion;

import io.netty.buffer.Unpooled;

public class OFMessageTest {

    private OFFactory factory;

    @Before
    public void setup() {
        factory = OFFactories.getFactory(OFVersion.OF_14);
    }

    @Test
    public void messageTooLong() {
        byte[] tooLong = new byte[0xFFE8];
        // results in a packet that's one byte too long - 65536
        OFPacketOut packetOut = factory.buildPacketOut().setData(tooLong).build();
        try {
            packetOut.writeTo(Unpooled.buffer());
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString(" message length (65536) exceeds maximum (0xFFFF)"));
        }
    }

}

package org.projectfloodlight.openflow.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TransportPortTest {

    @Test
    public void testOf() {
        assertThat(TransportPort.of(1).getPort(), equalTo(1));
        assertThat(TransportPort.of(2).getPort(), equalTo(2));
        assertThat(TransportPort.of(0xffee).getPort(), equalTo(0xffee));
        assertThat(TransportPort.of(0).getPort(), equalTo(0));
        assertThat(TransportPort.of(0), equalTo(TransportPort.FULL_MASK));
        assertThat(TransportPort.of(0xFFFF), equalTo(TransportPort.NO_MASK));
        // -1 (0xFFFF_FFFF) gets normalized to NO_MASK
        assertThat(TransportPort.of(-1), equalTo(TransportPort.NO_MASK));
    }

    @Test
    public void testEquals() {
        assertThat(TransportPort.of(1), equalTo(TransportPort.of(1)));
        assertThat(TransportPort.of(1), not(equalTo(TransportPort.of(2))));
        assertThat(TransportPort.of(2).getPort(), equalTo(2));
        assertThat(TransportPort.FULL_MASK, not(equalTo(TransportPort.NO_MASK)));

        assertThat(TransportPort.of(1).hashCode(), equalTo(TransportPort.of(1).hashCode()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfInvalidNegative() {
        TransportPort.of(-2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfInvalidTooHigh() {
        TransportPort.of(1 << 16);
    }

    @Test
    public void testReadWriteTwoBytes() throws Exception {
        readWriteTwoBytes(1, 0x00, 0x01);
        readWriteTwoBytes(0xFF, 0x00, 0xFF);
        readWriteTwoBytes(0xFFFE, 0xFF, 0xFE);
        readWriteTwoBytes(0xFFFF, 0xFF, 0xFF);
        readWriteTwoBytes(-1, 0xFF, 0xFF);
    }

     private void readWriteTwoBytes(int port, int... bytes) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        TransportPort.of(port).write2Bytes(buffer);
        assertThat(buffer.readableBytes(),equalTo(2));
        for(int i=0; i<bytes.length;i++) {
            assertThat(buffer.readByte(), equalTo((byte) bytes[i]));
        }
        assertThat(buffer.isReadable(), is(false));

        buffer.clear();
        for(int b: bytes) {
            buffer.writeByte(b);
        }
        assertThat(TransportPort.read2Bytes(buffer), equalTo(TransportPort.of(port)));
     }

     @Test
     public void testCompare() {
         assertThat(TransportPort.of(1), lessThan(TransportPort.of(2)));
         assertThat(TransportPort.of(0x7FFF), lessThan(TransportPort.of(0x8000)));
         assertThat(TransportPort.of(1), lessThan(TransportPort.of(0xFFFF)));
     }

 }

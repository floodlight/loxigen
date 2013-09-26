package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class OFPortMap implements OFValueType<OFPortMap> {

    static final int LENGTH = 16;

    private final long raw1;
    private final long raw2;

    public static final OFPortMap NONE = of(0, 0);

    private OFPortMap(long raw1, long raw2) {
        this.raw1 = raw1;
        this.raw2 = raw2;

    }

    public static OFPortMap of(long raw1, long raw2) {
        if (raw1 == 0 && raw2 == 0)
            return NONE;
        return new OFPortMap(raw1, raw2);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public OFPortMap applyMask(OFPortMap mask) {
        return of(this.raw1 & mask.raw1, this.raw2 & mask.raw2);
    }

    public void write16Bytes(ChannelBuffer cb) {
        cb.writeLong(raw1);
        cb.writeLong(raw2);
    }

    public static OFPortMap read16Bytes(ChannelBuffer cb) {
        long raw1 = cb.readLong();
        long raw2 = cb.readLong();
        return of(raw1, raw2);
    }

    private static boolean isBitOn(long raw1, long raw2, int bit) {
        if (bit < 0 || bit >= 128)
            throw new IndexOutOfBoundsException("Port number is out of bounds");
        long word;
        if (bit < 64) {
            word = raw1;
        } else {
            word = raw2;
            bit -= 64;
        }
        return (word & ((long)1 << bit)) != 0;
    }

    public boolean isOn(OFPort port) {
        return isBitOn(raw1, raw2, port.getPortNumber());
    }

    public static OFPortMap ofPorts(OFPort... ports) {
        Builder builder = new Builder();
        for (OFPort port: ports) {
            builder.set(port);
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return (String.format("%64s", Long.toBinaryString(raw2)) + String.format("%64s", Long.toBinaryString(raw1))).replaceAll(" ", "0");
    }

    public static class Builder {
        private long raw1, raw2;

        public Builder() {

        }

        public boolean isOn(OFPort port) {
            return isBitOn(raw1, raw2, port.getPortNumber());
        }

        public Builder set(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 128)
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw1 |= ((long)1 << bit);
            } else {
                raw2 |= ((long)1 << (bit - 64));
            }
            return this;
        }

        public Builder unset(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 128)
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw1 &= ~((long)1 << bit);
            } else {
                raw2 &= ~((long)1 << (bit - 64));
            }
            return this;
        }

        public OFPortMap build() {
            return OFPortMap.of(raw1, raw2);
        }
    }

}

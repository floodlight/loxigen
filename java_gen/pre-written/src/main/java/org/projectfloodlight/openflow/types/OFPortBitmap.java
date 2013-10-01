package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class OFPortBitmap implements OFValueType<OFPortBitmap> {

    static final int LENGTH = 16;

    private final long raw1; // MSBs (ports 64-127)
    private final long raw2; // LSBs (ports 0-63)

    public static final OFPortBitmap ALL = new OFPortBitmap(-1, -1);
    public static final OFPortBitmap NONE = new OFPortBitmap(0, 0);

    private OFPortBitmap(long raw1, long raw2) {
        this.raw1 = raw1;
        this.raw2 = raw2;
    }

    static OFPortBitmap of(long raw1, long raw2) {
        if (raw1 == -1 && raw2 == -1)
            return ALL;
        if (raw1 == 0 && raw2 == 0)
            return NONE;
        return new OFPortBitmap(raw1, raw2);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public OFPortBitmap applyMask(OFPortBitmap mask) {
        return of(this.raw1 & mask.raw1, this.raw2 & mask.raw2);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OFPortBitmap))
            return false;
        OFPortBitmap other = (OFPortBitmap)obj;
        return (other.raw1 == this.raw1 && other.raw2 == this.raw2);
    }

    @Override
    public int hashCode() {
        return (int)(31 * raw1 + raw2);
    }

    protected static boolean isBitOn(long raw1, long raw2, int bit) {
        if (bit < 0 || bit >= 128)
            throw new IndexOutOfBoundsException("Port number is out of bounds");
        long word;
        if (bit < 64) {
            word = raw2; // ports 0-63
        } else {
            word = raw1; // ports 64-127
            bit -= 64;
        }
        return (word & ((long)1 << bit)) != 0;
    }


    public void write16Bytes(ChannelBuffer cb) {
        cb.writeLong(raw1);
        cb.writeLong(raw2);
    }

    public static OFPortBitmap read16Bytes(ChannelBuffer cb) {
        long raw1 = cb.readLong();
        long raw2 = cb.readLong();
        return of(raw1, raw2);
    }

    public boolean isOn(OFPort port) {
        return isBitOn(raw1, raw2, port.getPortNumber());
    }

    @Override
    public String toString() {
        return (String.format("%64s", Long.toBinaryString(raw2)) + String.format("%64s", Long.toBinaryString(raw1))).replaceAll(" ", "0");
    }

}

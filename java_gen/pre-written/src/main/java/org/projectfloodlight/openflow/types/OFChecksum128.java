package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

import com.google.common.hash.PrimitiveSink;

public class OFChecksum128 implements OFValueType<OFChecksum128> {

    static final int LENGTH = 16;

    private final long raw1; // MSBs
    private final long raw2; // LSBs

    public static final OFChecksum128 ZERO = new OFChecksum128(0, 0);

    private OFChecksum128(long raw1, long raw2) {
        this.raw1 = raw1;
        this.raw2 = raw2;
    }

    public static OFChecksum128 of(long raw1, long raw2) {
        if (raw1 == 0 && raw2 == 0)
            return ZERO;
        return new OFChecksum128(raw1, raw2);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public OFChecksum128 applyMask(OFChecksum128 mask) {
        return of(this.raw1 & mask.raw1, this.raw2 & mask.raw2);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OFChecksum128))
            return false;
        OFChecksum128 other = (OFChecksum128)obj;
        return (other.raw1 == this.raw1 && other.raw2 == this.raw2);
    }

    @Override
    public int hashCode() {
        return (int)(31 * raw1 + raw2);
    }

    public void write16Bytes(ChannelBuffer cb) {
        cb.writeLong(raw1);
        cb.writeLong(raw2);
    }

    public static OFChecksum128 read16Bytes(ChannelBuffer cb) {
        long raw1 = cb.readLong();
        long raw2 = cb.readLong();
        return of(raw1, raw2);
    }

    @Override
    public String toString() {
        return String.format("0x%016x%016x", raw1, raw2);
    }

    @Override
    public int compareTo(OFChecksum128 o) {
        long c = this.raw1 - o.raw1;
        if (c != 0)
            return Long.signum(c);
        return Long.signum(this.raw2 - o.raw2);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putLong(raw1);
        sink.putLong(raw2);
    }

}

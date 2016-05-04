package org.projectfloodlight.openflow.types;

import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class BundleId implements OFValueType<BundleId> {
    static final int LENGTH = 4;

    private final static int NONE_VAL = 0;
    public final static BundleId NONE = new BundleId(NONE_VAL);

    private final static int NO_MASK_VAL = 0xFFFFFFFF;
    public final static BundleId NO_MASK = new BundleId(NO_MASK_VAL);
    public final static BundleId FULL_MASK = NONE;

    private final int rawValue;

    private BundleId(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static BundleId of(final int raw) {
        if(raw == NONE_VAL)
            return NONE;
        else if(raw == NO_MASK_VAL)
            return NO_MASK;
        return new BundleId(raw);
    }

    public int getInt() {
        return rawValue;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public String toString() {
        return UnsignedInts.toString(rawValue);
    }

    @Override
    public BundleId applyMask(BundleId mask) {
        return BundleId.of(rawValue & mask.rawValue);    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rawValue;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BundleId other = (BundleId) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    public void write4Bytes(ByteBuf c) {
        c.writeInt(rawValue);
    }

    public static BundleId read4Bytes(ByteBuf c) {
        return BundleId.of(c.readInt());
    }

    @Override
    public int compareTo(BundleId o) {
        return UnsignedInts.compare(rawValue, rawValue);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(rawValue);
    }
}

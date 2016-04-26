package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.annotations.Immutable;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

import io.netty.buffer.ByteBuf;

@Immutable
public class OFConnectionIndex implements OFValueType<OFConnectionIndex>  {

    static final int LENGTH = 4;
    private final int connectionIndex;

    private OFConnectionIndex(final int connectionIndex) {
        this.connectionIndex = connectionIndex;
    }

    public static OFConnectionIndex of(final int connectionIndex) {
        return new OFConnectionIndex(connectionIndex);
    }

    public void write4Bytes(ByteBuf c) {
        c.writeInt(connectionIndex);
    }

    public static OFConnectionIndex read4Bytes(ByteBuf c) {
        return OFConnectionIndex.of(c.readInt());
    }

    @Override
    public OFConnectionIndex applyMask(OFConnectionIndex mask) {
        return OFConnectionIndex.of(this.connectionIndex & mask.connectionIndex);
    }

    @Override
    public int compareTo(OFConnectionIndex o) {
        return UnsignedInts.compare(this.connectionIndex,o.connectionIndex);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(connectionIndex);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public String toString() {
        return UnsignedInts.toString(connectionIndex);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + connectionIndex;
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
        OFConnectionIndex other = (OFConnectionIndex) obj;
        if (connectionIndex != other.connectionIndex)
            return false;
        return true;
    }
}
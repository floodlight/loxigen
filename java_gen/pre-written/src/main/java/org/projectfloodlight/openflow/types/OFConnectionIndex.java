package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

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
}
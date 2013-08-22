package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class OFMetadata extends U64 implements OFValueType<OFMetadata> {
    
    private static int LENGTH = 8;

    public static final OFMetadata NO_MASK = OFMetadata.of(0xFFFFFFFFFFFFFFFFl);
    public static final OFMetadata FULL_MASK = OFMetadata.of(0x0);

    protected OFMetadata(long raw) {
        super(raw);
    }

    public static OFMetadata of(long raw) {
        return new OFMetadata(raw);
    }
    
    public static OFMetadata read8Bytes(ChannelBuffer cb) {
        return OFMetadata.of(cb.readLong());
    }
    
    public void write8Bytes(ChannelBuffer cb) {
        cb.writeLong(super.getValue());
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public OFMetadata applyMask(OFMetadata mask) {
        return OFMetadata.of(this.getValue() & mask.getValue());
    }
}

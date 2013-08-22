package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class OFMetadata implements OFValueType<OFMetadata> {

    private static int LENGTH = 8;

    private final U64 u64;

    public static final OFMetadata NO_MASK = OFMetadata.of(U64.ofRaw(0xFFFFFFFFFFFFFFFFl));
    public static final OFMetadata FULL_MASK = OFMetadata.of(U64.ofRaw(0x0));

    public OFMetadata(U64 ofRaw) {
        u64 = ofRaw;
    }

    public static OFMetadata of(U64 u64) {
        return new OFMetadata(u64);
    }

    public static OFMetadata ofRaw(long raw) {
        return new OFMetadata(U64.ofRaw(raw));
    }

    public static OFMetadata read8Bytes(ChannelBuffer cb) {
        return OFMetadata.ofRaw(cb.readLong());
    }

    public void write8Bytes(ChannelBuffer cb) {
        u64.writeTo(cb);
    }

    @Override
    public int getLength() {
        return u64.getLength();
    }

    @Override
    public OFMetadata applyMask(OFMetadata mask) {
        return OFMetadata.of(this.u64.applyMask(mask.u64));
    }

}

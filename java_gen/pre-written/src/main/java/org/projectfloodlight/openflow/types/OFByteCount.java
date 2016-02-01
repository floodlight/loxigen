package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFByteCount implements OFValueType<OFByteCount> {
	
    private final int byteCount;
    
    private OFByteCount(final int byteCount) {
        this.byteCount = byteCount;
    }

    public static OFByteCount of(final int byteCount) {
        return new OFByteCount(byteCount);
    }
    
    public void write8Bytes(ByteBuf c) {
        c.writeInt(byteCount);
    }

    public static OFByteCount read8Bytes(ByteBuf c) {
        return OFByteCount.of(c.readInt());
    }
	
    @Override
    public OFByteCount applyMask(OFByteCount mask) {
        return OFByteCount.of(this.byteCount & mask.byteCount);
    }

    @Override
    public int compareTo(OFByteCount o) {
        return UnsignedInts.compare(this.byteCount, o.byteCount);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(byteCount);
    }   
    
	@Override
	public int getLength() {
		// 8 byte
		return 8;
	}  
  
}
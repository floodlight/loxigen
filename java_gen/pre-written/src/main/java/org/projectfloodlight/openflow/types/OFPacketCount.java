package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFPacketCount implements OFValueType<OFPacketCount> {
	
    private final int packetCount;
    
    private OFPacketCount(final int packetCount) {
        this.packetCount = packetCount;
    }

    public static OFPacketCount of(final int packetCount) {
        return new OFPacketCount(packetCount);
    }
    
    public void write8Bytes(ByteBuf c) {
        c.writeInt(packetCount);
    }

    public static OFPacketCount read8Bytes(ByteBuf c) {
        return OFPacketCount.of(c.readInt());
    }
	
    @Override
    public OFPacketCount applyMask(OFPacketCount mask) {
        return OFPacketCount.of(this.packetCount & mask.packetCount);
    }

    @Override
    public int compareTo(OFPacketCount o) {
        return UnsignedInts.compare(this.packetCount, o.packetCount);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(packetCount);
    }   
    
	@Override
	public int getLength() {
		// 64 bit
		return 8;
	}   
  
}
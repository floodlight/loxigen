package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFDuration implements OFValueType<OFDuration> {
	
	//64 bit - first 32 bit value duration sec / second 32 bit value is duration_nsec
	
	
    private final int duration;
    
    private OFDuration(final int duration) {
        this.duration = duration;
    }

    public static OFDuration of(final int duration) {
        return new OFDuration(duration);
    }
    
    public void write8Bytes(ByteBuf c) {
        c.writeInt(duration);
    }

    public static OFDuration read8Bytes(ByteBuf c) {
        return OFDuration.of(c.readInt());
    }
	
    @Override
    public OFDuration applyMask(OFDuration mask) {
        return OFDuration.of(this.duration & mask.duration);
    }

    @Override
    public int compareTo(OFDuration o) {
        return UnsignedInts.compare(this.duration, o.duration);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(duration);
    }   
    
	@Override
	public int getLength() {
		//8 byte
		return 8;
	}   
  
}
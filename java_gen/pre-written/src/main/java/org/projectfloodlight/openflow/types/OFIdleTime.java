package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFIdleTime implements OFValueType<OFIdleTime> {
	
	//encoding of this time value is identical to OFDuration
	
    private final int idleTime;
    
    private OFIdleTime(final int idleTime) {
        this.idleTime = idleTime;
    }

    public static OFIdleTime of(final int idleTime) {
        return new OFIdleTime(idleTime);
    }
    
    public void write8Bytes(ByteBuf c) {
        c.writeInt(idleTime);
    }

    public static OFIdleTime read8Bytes(ByteBuf c) {
        return OFIdleTime.of(c.readInt());
    }
	
    @Override
    public OFIdleTime applyMask(OFIdleTime mask) {
        return OFIdleTime.of(this.idleTime & mask.idleTime);
    }

    @Override
    public int compareTo(OFIdleTime o) {
        return UnsignedInts.compare(this.idleTime, o.idleTime);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(idleTime);
    }   
  
	@Override
	public int getLength() {
		//32 bit
		return 4;
	}   
}
package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFFlowCount implements OFValueType<OFFlowCount> {
	
	//only used in OFPMP_AGGREGATE replies
	
	
    private final int flowCount;
    
    private OFFlowCount(final int flowCount) {
        this.flowCount = flowCount;
    }

    public static OFFlowCount of(final int flowCount) {
        return new OFFlowCount(flowCount);
    }
    
    public void write8Bytes(ByteBuf c) {
        c.writeInt(flowCount);
    }

    public static OFFlowCount read8Bytes(ByteBuf c) {
        return OFFlowCount.of(c.readInt());
    }
	
    @Override
    public OFFlowCount applyMask(OFFlowCount mask) {
        return OFFlowCount.of(this.flowCount & mask.flowCount);
    }

    @Override
    public int compareTo(OFFlowCount o) {
        return UnsignedInts.compare(this.flowCount, o.flowCount);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(flowCount);
    }   
    
	@Override
	public int getLength() {
		//32 bit
		return 4;
	}  
  
}
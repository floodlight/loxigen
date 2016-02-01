package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

@Immutable
public class OFConnectionURI implements OFValueType<OFConnectionURI>  {
	
    private final int connectionURI;
    
    private OFConnectionURI(final int connectionURI) {
        this.connectionURI = connectionURI;
    }

    public static OFConnectionURI of(final int connectionURI) {
        return new OFConnectionURI(connectionURI);
    }
    
    public void write4Bytes(ByteBuf c) {
        c.writeInt(connectionURI);
    }

    public static OFConnectionURI read4Bytes(ByteBuf c) {
        return OFConnectionURI.of(c.readInt());
    }
    
    @Override
    public OFConnectionURI applyMask(OFConnectionURI mask) {
    	//TODO
        return null;
    }

    @Override
    public int compareTo(OFConnectionURI o) {
    	// TODO
       return 0;
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(connectionURI);
    }   
    
	@Override
	public int getLength() {
		// TODO
		return 0;
	}  
	
}
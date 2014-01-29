package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.primitives.Shorts;

public class AuxId implements Comparable<AuxId> {
    
    private static final short VALIDATION_MASK = 0xFF;
    
    private static final short MAIN_VAL = 0x0000;
    
    public static final AuxId MAIN = new AuxId(MAIN_VAL);
            
    private final short id;

    private AuxId(short id) {
        this.id = id;
    }

    public static AuxId of(short id) {
        switch(id) {
            case MAIN_VAL:
                return MAIN;
            default:
                if ((id & VALIDATION_MASK) != id)
                    throw new IllegalArgumentException("Illegal Aux id value: " + id);
                return new AuxId(id);
        }
    }

    public static AuxId of(int id) {
        if((id & VALIDATION_MASK) != id)
            throw new IllegalArgumentException("Illegal Aux id value: "+id);
        return of((short) id);
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(id);
    }

    public short getValue() {
        return id;
    }

    public void writeByte(ChannelBuffer c) {
        c.writeByte(this.id);
    }

    public static AuxId readByte(ChannelBuffer c) throws OFParseError {
        return AuxId.of(c.readUnsignedByte());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableId))
            return false;
        AuxId other = (AuxId)obj;
        if (other.id != this.id)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 13873;
        return this.id * prime;
    }

    @Override
    public int compareTo(AuxId other) {
        return Shorts.compare(this.id, other.id);
    }

}

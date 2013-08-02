package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

public class VlanPcp implements OFValueType<VlanPcp> {
    
    private static final byte VALIDATION_MASK = 0x07;
    static final int LENGTH = 1; 
    
    private final byte pcp;
    
    private VlanPcp(byte pcp) {
        this.pcp = pcp;
    }
    
    public static VlanPcp of(byte pcp) {
        if ((pcp & VALIDATION_MASK) != pcp)
            throw new IllegalArgumentException("Illegal VLAN PCP value: " + pcp);
        return new VlanPcp(pcp);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VlanPcp))
            return false;
        VlanPcp other = (VlanPcp)obj;
        if (other.pcp != this.pcp)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 20173;
        return this.pcp * prime;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(pcp);
    }

    public byte getValue() {
        return pcp;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }
    
    public void writeByte(ChannelBuffer c) {
        c.writeShort(this.pcp);
    }

    public static VlanPcp readByte(ChannelBuffer c) throws OFParseError {
        return VlanPcp.of((byte)(c.readUnsignedByte() & 0xFF));
    }

    @Override
    public VlanPcp applyMask(VlanPcp mask) {
        return VlanPcp.of((byte)(this.pcp & mask.pcp));
    }
    
}

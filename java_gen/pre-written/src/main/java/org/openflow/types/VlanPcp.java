package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

public class VlanPcp implements OFValueType {
    
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

    volatile byte[] bytesCache = null;

    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache =
                            new byte[] { pcp };
                }
            }
        }
        return bytesCache;
    }
    
    public static final Serializer<VlanPcp> SERIALIZER_V10 = new SerializerV10();
    public static final Serializer<VlanPcp> SERIALIZER_V11 = SERIALIZER_V10;
    public static final Serializer<VlanPcp> SERIALIZER_V12 = SERIALIZER_V10;
    public static final Serializer<VlanPcp> SERIALIZER_V13 = SERIALIZER_V10;
    
    private static class SerializerV10 implements OFValueType.Serializer<VlanPcp> {

        @Override
        public void writeTo(VlanPcp value, ChannelBuffer c) {
            c.writeShort(value.pcp);
        }

        @Override
        public VlanPcp readFrom(ChannelBuffer c) throws OFParseError {
            return VlanPcp.of((byte)(c.readUnsignedByte() & 0xFF));
        }
        
    }
    
}

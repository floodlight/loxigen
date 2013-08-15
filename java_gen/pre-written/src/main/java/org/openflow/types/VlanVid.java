package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

public class VlanVid implements OFValueType<VlanVid> {
    
    private static final short VALIDATION_MASK = 0x0FFF;
    final static int LENGTH = 2;
    
    private final short vid;
    
    private VlanVid(short vid) {
        this.vid = vid;
    }
    
    public static VlanVid of(short vid) {
        if ((vid & VALIDATION_MASK) != vid)
            throw new IllegalArgumentException("Illegal VLAN VID value: " + vid);
        return new VlanVid(vid);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VlanVid))
            return false;
        VlanVid other = (VlanVid)obj;
        if (other.vid != this.vid)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 13873;
        return this.vid * prime;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(vid);
    }
    
    public short getValue() {
        return vid;
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
                            new byte[] { (byte) ((vid >>> 8) & 0xFF),
                                         (byte) ((vid >>> 0) & 0xFF) };
                }
            }
        }
        return bytesCache;
    }
    
    public void write2Bytes(ChannelBuffer c) {
        c.writeShort(this.vid);
    }

    public static VlanVid read2Bytes(ChannelBuffer c) throws OFParseError {
        return VlanVid.of(c.readShort());
    }

    @Override
    public VlanVid applyMask(VlanVid mask) {
        return VlanVid.of((short)(this.vid & mask.vid));
    }
    
}

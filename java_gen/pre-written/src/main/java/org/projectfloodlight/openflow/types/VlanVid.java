package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.primitives.Shorts;

/** Represents an OpenFlow Vlan VID, as specified by the OpenFlow 1.3 spec.
 *
 *  <b> Note: this is not just the 12-bit vlan tag. OpenFlow defines
 *      the additional mask bits 0x1000 to represent the presence of a vlan
 *      tag. This additional bit will be stripped when writing a OF1.0 value
 *      tag.
 *  </b>
 *
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 *
 */
public class VlanVid implements OFValueType<VlanVid> {

    private static final short VALIDATION_MASK = 0x1FFF;
    private static final short PRESENT_VAL = 0x1000;
    private static final short VLAN_MASK = 0x0FFF;
    private static final short NONE_VAL = 0x0000;
    private static final short UNTAGGED_VAL_OF13 = (short) 0x0000;
    private static final short UNTAGGED_VAL_OF10 = (short) 0xFFFF;
    final static int LENGTH = 2;

    /** presence of a VLAN tag is idicated by the presence of bit 0x1000 */
    public static final VlanVid PRESENT = new VlanVid(PRESENT_VAL);

    /** this value means 'not set' in OF1.0 (e.g., in a match). not used elsewhere */
    public static final VlanVid NONE = new VlanVid(NONE_VAL);

    /** for use with masking operations */
    public static final VlanVid NO_MASK = new VlanVid((short)0xFFFF);
    public static final VlanVid FULL_MASK = NONE;

    /** an untagged packet is specified as 0000 in OF 1.0, but 0xFFFF in OF1.0. Special case that. */
    public static final VlanVid UNTAGGED = new VlanVid(NONE_VAL) {
        @Override
        public void write2BytesOF10(ChannelBuffer c) {
            c.writeShort(UNTAGGED_VAL_OF10);
        }
    };

    private final short vid;

    private VlanVid(short vid) {
        this.vid = vid;
    }

    public static VlanVid ofRawVid(short vid) {
        if(vid == UNTAGGED_VAL_OF13)
            return UNTAGGED;
        else if(vid == PRESENT_VAL)
            return PRESENT;
        else if ((vid & VALIDATION_MASK) != vid)
            throw new IllegalArgumentException(String.format("Illegal VLAN value: %x", vid));
        return new VlanVid(vid);
    }

    public static VlanVid ofVlan(int vlan) {
        if( (vlan & VLAN_MASK) != vlan)
            throw new IllegalArgumentException(String.format("Illegal VLAN value: %x", vlan));
        return ofRawVid( (short) (vlan | PRESENT_VAL));
    }

    public static VlanVid ofVlanOF10(short of10vlan) {
        if(of10vlan == NONE_VAL) {
            return NONE;
        } else if(of10vlan == UNTAGGED_VAL_OF10) {
            return UNTAGGED;
        } else {
            return ofVlan(of10vlan);
        }
    }

    /** @return whether or not this VlanId has the present (0x1000) bit set */
    public boolean isPresentBitSet() {
       return (vid & PRESENT_VAL) != 0;
    }

    /** @return the actual VLAN tag this vid identifies */
    public short getVlan() {
        return (short) (vid & VLAN_MASK);
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

    public short getRawVid() {
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

    public void write2BytesOF10(ChannelBuffer c) {
        c.writeShort(this.getVlan());
    }

    public static VlanVid read2Bytes(ChannelBuffer c) throws OFParseError {
        return VlanVid.ofRawVid(c.readShort());
    }

    public static VlanVid read2BytesOF10(ChannelBuffer c) throws OFParseError {
        return VlanVid.ofVlanOF10(c.readShort());
    }

    @Override
    public VlanVid applyMask(VlanVid mask) {
        return VlanVid.ofRawVid((short)(this.vid & mask.vid));
    }

    @Override
    public int compareTo(VlanVid o) {
        return Shorts.compare(vid, o.vid);
    }

}

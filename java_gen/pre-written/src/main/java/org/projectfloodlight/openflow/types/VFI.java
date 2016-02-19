package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;

/** Represents a two byte virtual forwarding instance.
*
* @author Sudeep Modi {@literal <}sudeep.modi@bigswitch.com{@literal >}
*
*/public class VFI implements OFValueType<VFI> {
    private static final short ZERO_VAL = 0x0000;
    final static int LENGTH = 2;

    public static final VFI ZERO = new VFI(ZERO_VAL);

    /** for use with masking operations */
    public static final VFI NO_MASK = new VFI((short)0xFFFF);
    public static final VFI FULL_MASK = ZERO;

    private final short vfi;

    private VFI(short vfi) {
        this.vfi = vfi;
    }

    public static VFI ofVfi(int vfi) {
        if (vfi == NO_MASK.vfi)
            return NO_MASK;
        if (vfi == ZERO_VAL)
            return ZERO;
        return new VFI((short) vfi);
    }

    /** @return the actual VFI value */
    public short getVfi() {
        return vfi;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + vfi;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VFI other = (VFI) obj;
        if (vfi != other.vfi) return false;
        return true;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(vfi);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    public void write2Bytes(ByteBuf c) {
        c.writeShort(this.vfi);
    }

    public static VFI read2Bytes(ByteBuf c) throws OFParseError {
        return VFI.ofVfi(c.readShort());
    }

    @Override
    public VFI applyMask(VFI mask) {
        return VFI.ofVfi(this.vfi & mask.vfi);
    }

    @Override
    public int compareTo(VFI o) {
        return Integer.compare(vfi & 0xffff, o.vfi & 0xffff);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putShort(vfi);
    }

}

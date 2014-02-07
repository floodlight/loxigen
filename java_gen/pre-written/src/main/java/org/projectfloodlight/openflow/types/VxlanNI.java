package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

import io.netty.buffer.ByteBuf;

/** Represents the VXLAN Network Identifier (24 bits).
 *
 * @author Sarath Kumar Sankaran Kutty
 * {@literal <}sarath.kutty@bigswitch.com{@literal >}
 */
public class VxlanNI implements OFValueType<VxlanNI> {

    private static final int VALIDATION_MASK = 0x00FFffFF;
    final static int LENGTH = 4;

    private static final int ZERO_VAL = 0x0;
    public static final VxlanNI ZERO = new VxlanNI(ZERO_VAL);

    private static final int NO_MASK_VAL = 0xFFffFFff;
    public final static VxlanNI NO_MASK = new VxlanNI(NO_MASK_VAL);
    public static final VxlanNI FULL_MASK = ZERO;

    private final int vni;

    private VxlanNI(int vni) {
        this.vni = vni;
    }

    public static VxlanNI ofVni(int vni) {
        if (vni == ZERO_VAL)
            return ZERO;
        if ((vni & VALIDATION_MASK) != vni) {
            throw new IllegalArgumentException(String.format("Illegal VNI value: %x", vni));
        }
        return new VxlanNI(vni);
    }

    /** @return the VxLAN Network ID */
    public int getVni() {
        return vni;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VxlanNI other = (VxlanNI) obj;
        if (vni != other.vni) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + vni;
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(vni);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }


    public void write4Bytes(ByteBuf c) {
        c.writeInt(this.vni);
    }

    public static VxlanNI read4Bytes(ByteBuf c) throws OFParseError {
        return VxlanNI.ofVni(c.readInt());
    }

    @Override
    public VxlanNI applyMask(VxlanNI mask) {
        return VxlanNI.ofVni(this.vni & mask.vni);
    }

    @Override
    public int compareTo(VxlanNI o) {
        return UnsignedInts.compare(vni, o.vni);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(vni);
    }
}

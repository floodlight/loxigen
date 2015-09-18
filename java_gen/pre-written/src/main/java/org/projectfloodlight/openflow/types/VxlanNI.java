package org.projectfloodlight.openflow.types;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

/** Represents the VXLAN Network Identifier (24 bits).
 *
 * @author Sarath Kumar Sankaran Kutty
 * {@literal <}sarath.kutty@bigswitch.com{@literal >}
 */
public class VxlanNI implements OFValueType<VxlanNI> {

    private static final int VALIDATION_MASK = 0x00FFffFF;
    private static final int ZERO_VALUE = 0x0;
    final static int LENGTH = 3;

    public static final VxlanNI ZERO_INVALID = new VxlanNI(ZERO_VALUE);

    private final int vni;

    private VxlanNI(int vni) {
        this.vni = vni;
    }

    public static VxlanNI ofVni(int vni) {
        if (vni == ZERO_INVALID.vni || (vni & VALIDATION_MASK) != vni) {
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
        return "VxlanNI [vni=" + vni + "]";
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    private volatile byte[] bytesCache = null;

    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache =
                            new byte[] { (byte) ((vni >> 16) & 0xFF),
                                         (byte) ((vni >> 8) & 0xFF),
                                         (byte) ((vni >> 0) & 0xFF) };
                }
            }
        }
        return Arrays.copyOf(bytesCache, bytesCache.length);
    }

    public void write3Bytes(ChannelBuffer c) {
        c.writeInt(this.vni);
    }

    public static VxlanNI read3Bytes(ChannelBuffer c) throws OFParseError {
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

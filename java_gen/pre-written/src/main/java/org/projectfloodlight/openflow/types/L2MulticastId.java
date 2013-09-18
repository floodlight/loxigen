package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.annotations.Immutable;

@Immutable
public class L2MulticastId implements OFValueType<L2MulticastId> {
    static final int LENGTH = 4;
    private final int rawValue;

    public static final L2MulticastId NO_ClassId = L2MulticastId.of(0xFFFFFFFF);
    public static final L2MulticastId FULL_MASK = L2MulticastId.of(0x00000000);

    private L2MulticastId(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static L2MulticastId of(final int raw) {
        return new L2MulticastId(raw);
    }

    public int getInt() {
        return rawValue;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rawValue;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        L2MulticastId other = (L2MulticastId) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(rawValue);
    }

    @Override
    public L2MulticastId applyMask(L2MulticastId mask) {
        return L2MulticastId.of(this.rawValue & mask.rawValue);
    }
}

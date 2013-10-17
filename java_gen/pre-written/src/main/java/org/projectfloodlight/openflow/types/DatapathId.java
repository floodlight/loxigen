package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.util.HexString;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedLongs;

/**
 * Abstraction of a datapath ID that can be set and/or accessed as either a
 * long value or a colon-separated string.
 *
 * @author Rob Vaterlaus <rob.vaterlaus@bigswitch.com>
 */
public class DatapathId implements PrimitiveSinkable, Comparable<DatapathId> {

    public static final DatapathId NONE = new DatapathId(0);

    private final long rawValue;

    private DatapathId(long rawValue) {
        this.rawValue = rawValue;
    }

    public static DatapathId of(long rawValue) {
        return new DatapathId(rawValue);
    }

    public static DatapathId of(String s) {
        return new DatapathId(HexString.toLong(s));
    }

    public long getLong() {
        return rawValue;
    }

    public U64 getUnsignedLong() {
        return U64.of(rawValue);
    }

    @Override
    public String toString() {
        return HexString.toHexString(rawValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (rawValue ^ (rawValue >>> 32));
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
        DatapathId other = (DatapathId) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putLong(rawValue);
    }

    public int compareTo(DatapathId o) {
        return UnsignedLongs.compare(rawValue, o.rawValue);
    }
}

package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.annotations.Immutable;
import org.projectfloodlight.openflow.exceptions.OFParseError;

/**
 * Abstraction of a buffer id in OpenFlow. Immutable.
 *
 * @author Rob Vaterlaus <rob.vaterlaus@bigswitch.com>
 */
@Immutable
public class OFBufferId {

    public static final OFBufferId NO_BUFFER = new OFBufferId(0xFFFFFFFF);

    private final int rawValue;

    private OFBufferId(int rawValue) {
        this.rawValue = rawValue;
    }

    public static OFBufferId of(final int rawValue) {
        if (rawValue == NO_BUFFER.getInt())
            return NO_BUFFER;
        return new OFBufferId(rawValue);
    }

    public int getInt() {
        return rawValue;
    }

    @Override
    public String toString() {
        return Long.toString(U32.f(rawValue));
    }

    public void write4Bytes(ChannelBuffer c) {
        c.writeInt(this.rawValue);
    }

    public static OFBufferId read4Bytes(ChannelBuffer c) throws OFParseError {
        return OFBufferId.of(c.readInt());
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
        OFBufferId other = (OFBufferId) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }
}

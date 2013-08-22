package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;
import org.openflow.protocol.OFMessageReader;
import org.openflow.protocol.Writeable;

/**
 * A wrapper around the OpenFlow physical port description. The interfaces to
 * this object are version agnostic.
 *
 * @author capveg
 */

public class OFPhysicalPort implements OFValueType<OFPhysicalPort>, Writeable {

    static final int LENGTH = 4;

    private final int port;

    public static final OFPhysicalPort NO_MASK = OFPhysicalPort.of(0xFFFFFFFF);
    public static final OFPhysicalPort FULL_MASK = OFPhysicalPort.of(0x0);

    private OFPhysicalPort(int port) {
        this.port = port;
    }

    public static OFPhysicalPort of(int port) {
        return new OFPhysicalPort(port);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OFPhysicalPort))
            return false;
        OFPhysicalPort other = (OFPhysicalPort)obj;
        if (other.port != this.port)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 59;
        int result = 1;
        result = prime * result + port;
        return result;
    }

    @Override
    public String toString() {
        return Integer.toHexString(port);
    }

    public void write4Bytes(ChannelBuffer c) {
        c.writeInt(this.port);
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        write4Bytes(bb);
    }

    public static OFPhysicalPort read4Bytes(ChannelBuffer c) throws OFParseError {
        return OFPhysicalPort.of((int)(c.readUnsignedInt() & 0xFFFFFFFF));
    }

    @Override
    public OFPhysicalPort applyMask(OFPhysicalPort mask) {
        return OFPhysicalPort.of(this.port & mask.port);
    }

    public int getPortNumber() {
        return port;
    }

    public final static Reader READER = new Reader();
    private static class Reader implements OFMessageReader<OFPhysicalPort> {
        @Override
        public OFPhysicalPort readFrom(ChannelBuffer bb) throws OFParseError {
            return OFPhysicalPort.read4Bytes(bb);
        }

    }
}

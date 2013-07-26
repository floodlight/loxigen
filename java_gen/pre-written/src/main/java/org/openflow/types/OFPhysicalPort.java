package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

/**
 * A wrapper around the OpenFlow physical port description. The interfaces to
 * this object are version agnostic.
 *
 * @author capveg
 */

public class OFPhysicalPort implements OFValueType {

    static final int LENGTH = 4;
    
    private final int port;
    
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
    
    volatile byte[] bytesCache;
    
    @Override
    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache = new byte[] { (byte)(port & 0xFF),
                                              (byte)((port >>> 8) & 0xFF),
                                              (byte)((port >>> 16) & 0xFF),
                                              (byte)((port >>> 24) & 0xFF)};
                }
            }
        }
        return bytesCache;
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

    public static final Serializer<OFPhysicalPort> SERIALIZER_V11 = new SerializerV11();
    public static final Serializer<OFPhysicalPort> SERIALIZER_V12 = SERIALIZER_V11;
    public static final Serializer<OFPhysicalPort> SERIALIZER_V13 = SERIALIZER_V11;

    private static class SerializerV11 implements OFValueType.Serializer<OFPhysicalPort> {

        @Override
        public void writeTo(OFPhysicalPort value, ChannelBuffer c) {
            c.writeInt(value.port);
        }

        @Override
        public OFPhysicalPort readFrom(ChannelBuffer c) throws OFParseError {
            return OFPhysicalPort.of((int)(c.readUnsignedInt() & 0xFFFFFFFF));
        }

    }

}

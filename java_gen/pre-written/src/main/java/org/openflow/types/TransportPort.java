package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

/**
 * Represents L4 (Transport Layer) port (TCP, UDP, etc.)
 * 
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 */
public class TransportPort implements OFValueType {
    
    static final int LENGTH = 2;
    static final int MAX_PORT = 0xFFFF;
    static final int MIN_PORT = 0;
    
    private final int port;
    
    private TransportPort(int port) {
        this.port = port;
    }
    
    public static TransportPort of(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException("Illegal transport layer port number: " + port);
        }
        return new TransportPort(port);
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
                                              (byte)((port >>> 8) & 0xFF)};
                }
            }
        }
        return bytesCache;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransportPort))
            return false;
        TransportPort other = (TransportPort)obj;
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
        return Integer.toString(port);
    }

    public static final Serializer<TransportPort> SERIALIZER_V10 = new SerializerV10();
    public static final Serializer<TransportPort> SERIALIZER_V11 = SERIALIZER_V10;
    public static final Serializer<TransportPort> SERIALIZER_V12 = SERIALIZER_V10;
    public static final Serializer<TransportPort> SERIALIZER_V13 = SERIALIZER_V10;

    private static class SerializerV10 implements OFValueType.Serializer<TransportPort> {

        @Override
        public void writeTo(TransportPort value, ChannelBuffer c) {
            c.writeShort(value.port);
        }

        @Override
        public TransportPort readFrom(ChannelBuffer c) throws OFParseError {
            return TransportPort.of((c.readUnsignedShort() & 0x0FFFF));
        }

    }

}

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

    public void write2Bytes(ChannelBuffer c) {
        c.writeShort(this.port);
    }

    public static TransportPort read2Bytes(ChannelBuffer c) throws OFParseError {
        return TransportPort.of((c.readUnsignedShort() & 0x0FFFF));
    }

}

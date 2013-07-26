package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;



public interface OFValueType {

    public int getLength();
    public byte[] getBytes();
    
    public interface Serializer<T extends OFValueType> {
        public void writeTo(T value, ChannelBuffer c);
        public T readFrom(ChannelBuffer c) throws OFParseError; 
    }
    
}

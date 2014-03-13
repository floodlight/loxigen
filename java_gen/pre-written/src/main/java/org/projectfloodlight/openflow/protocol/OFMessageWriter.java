package org.projectfloodlight.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.ByteBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

public interface OFMessageWriter<T> {
    public void write(ChannelBuffer bb, T message) throws OFParseError;
    public void write(ByteBuffer bb, T message) throws OFParseError;
}

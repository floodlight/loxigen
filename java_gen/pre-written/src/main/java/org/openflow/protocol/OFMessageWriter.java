package org.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

public interface OFMessageWriter<T> {
    public int write(ChannelBuffer bb, T message) throws OFParseError;
}

package org.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

public interface OFMessageReader<T extends OFMessage> {
    T readFrom(ChannelBuffer bb) throws OFParseError;
}

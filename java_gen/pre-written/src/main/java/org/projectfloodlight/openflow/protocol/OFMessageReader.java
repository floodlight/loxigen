package org.projectfloodlight.openflow.protocol;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.exceptions.OFParseError;

public interface OFMessageReader<T> {
    T readFrom(ByteBuf bb) throws OFParseError;
}

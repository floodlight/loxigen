package org.projectfloodlight.openflow.protocol;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.exceptions.OFParseError;

public interface OFMessageWriter<T> {
    public void write(ByteBuf bb, T message) throws OFParseError;
}

package org.projectfloodlight.openflow.protocol;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import io.netty.buffer.ByteBuf;

public interface OFMessageWriter<T> {
    public void write(ByteBuf bb, T message) throws OFParseError;
}

package org.projectfloodlight.openflow.protocol;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import io.netty.buffer.ByteBuf;

public abstract class AbstractOFMessageReader<T> implements OFMessageReader<T> {

    @Override
    public final T readFrom(ByteBuf bb) throws OFParseError {
        return readFrom(OFMessageReaderContexts.DEFAULT, bb);
    }

}

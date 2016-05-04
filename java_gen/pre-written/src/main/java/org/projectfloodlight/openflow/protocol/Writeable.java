package org.projectfloodlight.openflow.protocol;

import io.netty.buffer.ByteBuf;

public interface Writeable {
    void writeTo(ByteBuf bb);
}

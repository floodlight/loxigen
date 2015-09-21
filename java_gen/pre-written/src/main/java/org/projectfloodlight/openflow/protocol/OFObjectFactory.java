package org.projectfloodlight.openflow.protocol;

import io.netty.buffer.ByteBuf;

public interface OFObjectFactory<T extends OFObject> {
    T read(ByteBuf buffer);
}

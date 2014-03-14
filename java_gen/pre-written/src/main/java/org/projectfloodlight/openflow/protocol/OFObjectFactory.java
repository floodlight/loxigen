package org.projectfloodlight.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.ByteBuffer;

public interface OFObjectFactory<T extends OFObject> {
    T read(ChannelBuffer buffer);
    T read(ByteBuffer buffer);
}

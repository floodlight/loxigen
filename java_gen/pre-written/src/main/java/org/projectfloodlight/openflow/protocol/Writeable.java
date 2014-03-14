package org.projectfloodlight.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.ByteBuffer;

public interface Writeable {
    void writeTo(ChannelBuffer bb);
    void writeTo(ByteBuffer bb);
}

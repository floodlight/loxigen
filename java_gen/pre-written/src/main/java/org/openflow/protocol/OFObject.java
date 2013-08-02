package org.openflow.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base interface of all OpenFlow objects (e.g., messages, actions, stats, etc.)
 *
 * All objects have a length and can be read and written from a buffer. When
 * writing, the length field is dynamically updated, so it need not be managed
 * manually. However, you can override the auto calculated length with
 * overrideLength() call, if, for example, you want to intentionally create
 * malformed packets, for example, for negative testing.
 */

public interface OFObject {
    void writeTo(ChannelBuffer bb);
}

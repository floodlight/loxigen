package org.openflow.protocol;

/**
 *  Base interface of all OpenFlow objects (e.g., messages, actions, stats, etc.)
 *
 *  All objects have a length and can be read and written from a buffer.
 *  When writing, the length field is dynamically updated, so it need not be
 *  managed manually.  However, you can override the auto calculated length with
 *  overrideLength() call, if, for example, you want to intentionally create
 *  malformed packets, for example, for negative testing.
 */

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;
import org.openflow.exceptions.OFShortWrite;

public interface OFObject {
    /**
     * Return a number equal or greater than zero (and currently in OF less than
     * 65536)
     *
     * @return the number of bytes this object will represent on the wire
     */
    public int getLength();

    /**
     * Automatically calculate any lengths and write an openflow object into the
     * byte buffer.
     *
     * @param bb
     *            A valid byte buffer with sufficient capacity to hold this
     *            object/
     */
    public void writeTo(ChannelBuffer bb) throws OFParseError, OFShortWrite;
}

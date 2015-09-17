package org.projectfloodlight.openflow.protocol;

import javax.annotation.Nullable;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import io.netty.buffer.ByteBuf;

/**
 * Generic OpenFlow object reader.
 *
 * Contract for Readers that parse a certain object type out of a {@link ByteBuf}.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 * @param <T> - the type of the object to be read.
 */
public interface OFMessageReader<T> {
    /**
     * Read a message from the ByteBuf, with specified context.
     *
     * @param context - provides context to the read operation. will be passed on
     *    to delegate reader.
     * @param bb the byte buffer to read from
     * @return the read message. null if no full message is available in the buffer.
     * @throws OFParseError if the message in the buffer could not be parsed. No assumptions
     *   should be made on the state of the buffer in this case.
     */
    @Nullable
    T readFrom(OFMessageReaderContext context, ByteBuf bb) throws OFParseError;

    /**
     * Read a message from a ByteBuf, with default context.
     *
     * Convenience method for clients who do not want to specify a context.
     *
     * Delegates to readFrom(OFMessageReaderContexts.DEFAULT, bb);
     * @param bb the byte buffer to read from
     * @return the parsed message, or null if no full message is available in the buffer.
     * @throws OFParseError if the message in the buffer could not be parsed. No assumptions
     *   should be made on the state of the buffer after a parse error.
     */
    @Nullable
    T readFrom(ByteBuf bb) throws OFParseError;
}

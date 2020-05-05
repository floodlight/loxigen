package org.projectfloodlight.openflow.protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import io.netty.buffer.ByteBuf;

public interface OFMessageReader<T> {

    /** Read the next available openflow message from the given byte buffer.
     *
     *  Returns null if not enough data is available on the byte buffer. Throws OFParseError if a protocol error occurs.
     *
     *  <b>Byte Buffer post-conditions</b>:
     *  <ul>
     *      <li>After a successful read, the byte buffer read position is positioned after the last-read message</li>
     *      <li>After the method has returned null (indicating an incomplete message), the buffer read position is
     *          unchanged from the previous attempt (i.e., they next read will attempt to re-read
     *          from the same position)</li>
     *      <li>After OFParseError is thrown, the read position of the byte buffer is undefined.</li>
     *  </ul>
     *
     * @param bb input byte buffer
     * @return the parsed of message or null if not enough data is available in the buffer
     * @throws OFParseError if a protocol error occurs.
     */
    @Nullable
    T readFrom(@Nonnull ByteBuf bb) throws OFParseError;
}

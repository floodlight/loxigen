package org.projectfloodlight.openflow.handler;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.OFMessageReader;

/**
 * Contract for handlers that deal with unparsed messages.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public interface UnparsedHandler {
    /**
     * Unknown message encountered
     *
     * invoked when an {@link OFMessageReader} encounters a discriminator value that it does
     * not recognize, e.g., an OFMessage with an unknown type, or a TLV with an unknown TLV.
     *
     * @param parentClass the virtual class that the reader was trying to discriminate, e.g., OFMessage
     * @param discriminatorName the name of the discriminator field, e.g., "type"
     * @param value the unknown value.
     *
     * @throws OFParseError if the handler decides to abort the parsing of this message.
     */
    public void unparsedMessage(Class<?> parentClass, String discriminatorName, Object value) throws OFParseError;
}

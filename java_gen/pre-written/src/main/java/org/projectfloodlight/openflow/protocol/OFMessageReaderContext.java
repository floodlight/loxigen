package org.projectfloodlight.openflow.protocol;

import org.projectfloodlight.openflow.handler.UnparsedHandler;

/**
 * Context for a {@link OFMessageReader} read operation.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class OFMessageReaderContext {
    private final UnparsedHandler unparsedHandler;

    private OFMessageReaderContext(UnparsedHandler unparsedHandler) {
        this.unparsedHandler = unparsedHandler;
    }

    /**
     * Retrieve the {@link UnparsedHandler}
     *
     * @return the {@link UnparsedHandler} to be invoked if a message on the wire is unknown.
     */
    public UnparsedHandler getUnparsedHandler() {
        return unparsedHandler;
    }

    public static OFMessageReaderContext of(UnparsedHandler unparsedHandler) {
        return new OFMessageReaderContext(unparsedHandler);
    }
}

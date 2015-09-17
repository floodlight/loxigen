package org.projectfloodlight.openflow.protocol;

import org.projectfloodlight.openflow.handler.UnparsedHandlers;

public class OFMessageReaderContexts {

    public static final OFMessageReaderContext DEFAULT =
            OFMessageReaderContext.of(UnparsedHandlers.getDefaultHandler());

}

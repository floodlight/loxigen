package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;
import org.openflow.exceptions.OFShortWrite;
import org.openflow.protocol.OFObject;

public class OFBucket implements OFObject {

    @Override
    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeTo(final ChannelBuffer bb) throws OFParseError, OFShortWrite {
        // TODO Auto-generated method stub

    }

}

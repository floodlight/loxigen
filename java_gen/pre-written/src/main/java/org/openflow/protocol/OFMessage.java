package org.openflow.protocol;

import org.openflow.types.OFType;

public interface OFMessage {
    int getXid();

    boolean isXidSet();

    OFType getType();

    OFVersion getVersion();
}

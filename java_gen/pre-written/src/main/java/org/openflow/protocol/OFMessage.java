package org.openflow.protocol;

public interface OFMessage {
    int getXid();

    OFType getType();

    OFVersion getVersion();

    interface Builder {

    }
}

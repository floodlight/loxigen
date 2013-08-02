package org.openflow.protocol;

public interface OFMessage extends OFObject {
    int getXid();

    OFType getType();

    OFVersion getVersion();

    interface Builder {

    }
}

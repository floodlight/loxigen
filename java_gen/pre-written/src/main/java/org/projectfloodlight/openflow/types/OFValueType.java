package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.protocol.Writeable;



public interface OFValueType<T extends OFValueType<T>> extends Comparable<T>, Writeable, PrimitiveSinkable {
    public int getLength();

    public T applyMask(T mask);

}

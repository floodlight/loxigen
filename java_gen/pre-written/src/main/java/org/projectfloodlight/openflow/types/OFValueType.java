package org.projectfloodlight.openflow.types;




public interface OFValueType<T extends OFValueType<T>> {

    public int getLength();
    
    public T applyMask(T mask);

}

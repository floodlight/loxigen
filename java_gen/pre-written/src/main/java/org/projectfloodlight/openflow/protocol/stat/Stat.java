package org.projectfloodlight.openflow.protocol.stat;

import org.projectfloodlight.openflow.protocol.OFObject;
import org.projectfloodlight.openflow.types.OFValueType;

public interface Stat extends OFObject {
    public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException;
    public boolean supports(StatField<?> field);
    public Iterable<StatField<?>> getStatFields();
    public Builder createBuilder();

    interface Builder {
        public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException;
        public boolean supports(StatField<?> field);
        public Stat build();
    }
}
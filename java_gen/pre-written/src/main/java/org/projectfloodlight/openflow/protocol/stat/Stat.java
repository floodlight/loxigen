package org.projectfloodlight.openflow.protocol.stat;

import org.projectfloodlight.openflow.protocol.OFObject;
import org.projectfloodlight.openflow.types.Masked;
import org.projectfloodlight.openflow.types.OFValueType;

public interface Stat extends OFObject {


    public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException;
    public <F extends OFValueType<F>> Masked<F> getMasked(StatField<F> field) throws UnsupportedOperationException;
    public boolean supports(StatField<?> field);
    public boolean supportsMasked(StatField<?> field) throws UnsupportedOperationException;
    public Iterable<StatField<?>> getStatFields();
    public boolean isPartiallyMasked(StatField<?> field) throws UnsupportedOperationException;
    public Builder createBuilder();

    interface Builder {
        public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException;
        public <F extends OFValueType<F>> Masked<F> getMasked(StatField<F> field) throws UnsupportedOperationException;
        public boolean supports(StatField<?> field);
        public boolean supportsMasked(StatField<?> field) throws UnsupportedOperationException;
        public boolean isPartiallyMasked(StatField<?> field) throws UnsupportedOperationException;
        public Stat build();
    }
}
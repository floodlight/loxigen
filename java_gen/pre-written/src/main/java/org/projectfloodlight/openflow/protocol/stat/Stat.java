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
        /**
         * Sets a specific value for a stat field.
         *
         * @param <F> StatField type
         * @param field Stat field to set.
         * @param value Value of stat field.
         * @return the Builder instance used.
         * @throws UnsupportedOperationException If field is not supported.
         */
        public <F extends OFValueType<F>> Builder set(StatField<F> field, F value) throws UnsupportedOperationException;

        public boolean supports(StatField<?> field);
        public Stat build();
    }
}

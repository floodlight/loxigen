package org.openflow.protocol.match;

import org.openflow.types.Masked;
import org.openflow.types.OFValueType;

public interface MatchBuilder {
    /**
     * Returns the value for the given field from this match.
     *
     * @param field Match field to retrieve
     * @return Value of match field
     */
    public <F extends OFValueType<F>> F get(MatchField<F> field) throws UnsupportedOperationException;

    /**
     * Returns the masked value for the given field from this match.
     * Precondition: field is partially wildcarded.
     *
     * @param field Match field to retrieve
     * @return Masked value of match field or null if no mask
     */
    public <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field) throws UnsupportedOperationException;

    /**
     * Returns true if this match object supports the given match field.
     *
     * @param field Match field
     * @return
     */
    public boolean supports(MatchField<?> field);

    /**
     * true iff field supports a bitmask mask that wildcards part of the field
     * (note: not all possible values of this bitmask have to be acceptable)
     *
     * @param field Match field
     * @return
     */
    public boolean supportsMasked(MatchField<?> field);

    /**
     * True iff this field is currently fully specified in the match, i.e., the
     * match will only select packets that match the exact value of getField(field).
     *
     * @param field Match field
     * @return
     */
    public boolean isExact(MatchField<?> field);

    /**
     * True if this field is currently logically unspecified in the match, i.e, the
     * value returned by getValue(f) has no impact on whether a packet will be selected
     * by the match or not.
     *
     * @param field
     * @return
     */
    public boolean isFullyWildcarded(MatchField<?> field);

    /**
     * True if this field is currently partially specified in the match, i.e, the
     * match will select packets that match (p.value & getMask(field)) == getValue(field).
     *
     * @param field
     * @return
     */
    public boolean isPartiallyMasked(MatchField<?> field);


    public <F extends OFValueType<F>> MatchBuilder setExact(MatchField<F> field, F value);

    public <F extends OFValueType<F>> MatchBuilder setMasked(MatchField<F> field, F value, F mask);

    public <F extends OFValueType<F>> MatchBuilder setMasked(MatchField<F> field, Masked<F> valueWithMask);

    public <F extends OFValueType<F>> MatchBuilder wildcard(MatchField<F> field);

    public Match getMatch();
}

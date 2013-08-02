package org.openflow.protocol.match;

import org.openflow.protocol.OFObject;
import org.openflow.types.Masked;
import org.openflow.types.OFValueType;

public interface Match extends OFObject {

    /*
     * Preconditions
     * On preconditions (from the OF1.1 spec, page 28, the OF1.0 spec failed to explicitly
     * specify this, but it is the behavior of Of1.0 switches):
     * Protocol-specific fields within ofp_match will be ignored within a single table when
     * the corresponding protocol is not specified in the match. The MPLS match fields will
     * be ignored unless the Ethertype is specified as MPLS. Likewise, the IP header and
     * transport header fields will be ignored unless the Ethertype is specified as either
     * IPv4 or ARP. The tp_src and tp_dst fields will be ignored unless the network protocol
     * specified is as TCP, UDP or SCTP. Fields that are ignored don�t need to be wildcarded
     * and should be set to 0.
     */

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

    /**
     * Returns a builder to build new instances of this type of match object.
     * @return Match builder
     */
    public MatchBuilder createBuilder();

    interface Builder extends MatchBuilder {
    }
}

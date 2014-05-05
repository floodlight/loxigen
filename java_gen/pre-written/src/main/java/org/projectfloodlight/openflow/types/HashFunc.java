package org.projectfloodlight.openflow.types;

/** A hash function that can hash a {@link PrimitiveSinkable}.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 * @param <H> - the hash.
 */
public interface HashFunc<H extends HashValue<H>> {
    /** Hash the PrimitivateSinkable with this hash function and return the result */
    public H hash(PrimitiveSinkable v);

    /** Provide a defined null-hash */
    public H nullHash();
}
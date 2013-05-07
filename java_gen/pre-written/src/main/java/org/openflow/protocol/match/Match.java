package org.openflow.protocol.match;

import org.openflow.protocol.OFObject;

public interface Match extends OFObject {
    public <F> F get(MatchField<F, ?> match);

    public <M> M getMasked(MatchField<?, M> match);

    public boolean supports(MatchField<?, ?> field);

    public boolean supportsMasked(MatchField<?, ?> field);

    public boolean isExact(MatchField<?, ?> field);

    public boolean isFullyWildcarded(MatchField<?, ?> field);

    public boolean isPartiallyMasked(MatchField<?, ?> field);

    public MatchBuilder getBuilder();
}

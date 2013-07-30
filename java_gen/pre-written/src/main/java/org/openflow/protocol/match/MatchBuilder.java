package org.openflow.protocol.match;

import org.openflow.types.OFValueType;

public interface MatchBuilder extends Match {
    public <F extends OFValueType> MatchBuilder set(MatchField<F> match, F value);

    //public <M> void setMasked(MatchField<?, M> match, M value);

    public Match getMatch();
}

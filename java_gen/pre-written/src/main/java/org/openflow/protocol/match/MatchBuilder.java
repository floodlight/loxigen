package org.openflow.protocol.match;

import org.openflow.types.OFValueType;

public interface MatchBuilder extends Match {
    public <F extends OFValueType<F>> MatchBuilder set(MatchField<F> field, F value);

    public <F extends OFValueType<F>> MatchBuilder unset(MatchField<F> field);
    
    //public <M> void setMasked(MatchField<?, M> match, M value);

    public Match getMatch();
}

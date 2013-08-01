package org.openflow.protocol.match;

import org.openflow.types.Masked;
import org.openflow.types.OFValueType;

public interface MatchBuilder extends Match {
    public <F extends OFValueType<F>> MatchBuilder setExact(MatchField<F> field, F value);
    
    public <F extends OFValueType<F>> MatchBuilder setMasked(MatchField<F> field, F value, F mask);    

    public <F extends OFValueType<F>> MatchBuilder setMasked(MatchField<F> field, Masked<F> valueWithMask);    

    public <F extends OFValueType<F>> MatchBuilder wildcard(MatchField<F> field);
    
    public Match getMatch();
}

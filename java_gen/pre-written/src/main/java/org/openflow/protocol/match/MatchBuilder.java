package org.openflow.protocol.match;

public interface MatchBuilder extends Match {
    public <F> MatchBuilder set(MatchField<F, ?> match, F value);

    public <M> void setMasked(MatchField<?, M> match, M value);

    public Match getMatch();
}

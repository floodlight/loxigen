package org.projectfloodlight.openflow.protocol.match;

import java.util.Set;
import com.google.common.collect.ImmutableSet;

import org.projectfloodlight.openflow.types.OFValueType;

public class Prerequisite<T extends OFValueType<T>> {
    private final MatchField<T> field;
    private final Set<OFValueType<T>> values;
    private boolean any;

    @SafeVarargs
    public Prerequisite(MatchField<T> field, OFValueType<T>... values) {
        this.field = field;
        /* possible null values, since public constructor */
        if (values == null || values.length == 0) {
            this.any = true;
            this.values = ImmutableSet.of();
        } else {
            this.any = false;
            this.values = ImmutableSet.copyOf(values);
        }
    }

    /**
     * Returns true if this prerequisite is satisfied by the given match object.
     *
     * @param match Match object
     * @return true iff prerequisite is satisfied.
     */
    public boolean isSatisfied(Match match) {
        OFValueType<T> res = match.get(this.field);
        if (res == null)
            return false;
        if (this.any)
            return true;
        if (this.values.contains(res)) {
            return true;
        }
        return false;
    }

    /**
     * Get valid/possible values for this prerequisite match.
     *
     * @return unmodifiable set of possible values
     */
    public Set<OFValueType<T>> getValues() {
        return this.values;   
    }

    /**
     * Get the MatchField of this prerequisite.
     *
     * @return the MatchField that is required
     */
    public MatchField<T> getMatchField() {
        return this.field; /* immutable */
    }

}

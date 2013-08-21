package org.openflow.protocol.match;

import java.util.HashSet;
import java.util.Set;

import org.openflow.types.OFValueType;

public class Prerequisite<T extends OFValueType<T>> {
    private MatchField<T> field;
    private Set<OFValueType<T>> values;
    private boolean any;
    
    public Prerequisite(MatchField<T> field, OFValueType<T>... values) {
        this.values = new HashSet<OFValueType<T>>();
        this.field = field;
        if (values == null || values.length == 0) {
            this.any = true;
        } else {
            this.any = false;
            for (OFValueType<T> value : values) {
                this.values.add(value);
            }
        }
    }
    
    /**
     * Returns true if this prerequisite is satisfied by the given match object.
     * 
     * @param match Match object
     * @return true iff prerequisite is satisfied.
     */
    public boolean isStaisfied(Match match) {
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

}

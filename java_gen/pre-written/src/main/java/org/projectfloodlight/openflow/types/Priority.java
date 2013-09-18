package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.annotations.Immutable;

@Immutable
public class Priority implements OFValueType<Priority> {
    static final int LENGTH = 4;
    private final int rawValue;

    public static final Priority NO_ClassId = Priority.of(0xFFFFFFFF);
    public static final Priority FULL_MASK = Priority.of(0x00000000);

    private Priority(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static Priority of(final int raw) {
        return new Priority(raw);
    }

    public int getInt() {
        return rawValue;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rawValue;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Priority other = (Priority) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(rawValue);
    }

    @Override
    public Priority applyMask(Priority mask) {
        return Priority.of(this.rawValue & mask.rawValue);
    }
}

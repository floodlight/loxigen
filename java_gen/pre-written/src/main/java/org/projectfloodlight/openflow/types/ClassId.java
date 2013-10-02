package org.projectfloodlight.openflow.types;

import javax.annotation.concurrent.Immutable;

import com.google.common.primitives.UnsignedInts;

@Immutable
public class ClassId implements OFValueType<ClassId> {
    static final int LENGTH = 4;

    private final static int NONE_VAL = 0;
    public final static ClassId NONE = new ClassId(NONE_VAL);

    private final int rawValue;

    private ClassId(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static ClassId of(final int raw) {
        if(raw == NONE_VAL)
            return NONE;

        return new ClassId(raw);
    }

    public int getInt() {
        return rawValue;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public String toString() {
        return Integer.toString(rawValue);
    }

    @Override
    public ClassId applyMask(ClassId mask) {
        return ClassId.of(rawValue & mask.rawValue);    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rawValue;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassId other = (ClassId) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public int compareTo(ClassId o) {
        return UnsignedInts.compare(rawValue, rawValue);
    }
}

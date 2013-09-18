package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.annotations.Immutable;

@Immutable
public class ClassId implements OFValueType<ClassId> {
    static final int LENGTH = 4;
    private final int rawValue;

    public static final ClassId NO_ClassId = ClassId.of(0xFFFFFFFF);
    public static final ClassId FULL_MASK = ClassId.of(0x00000000);

    private ClassId(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static ClassId of(final int raw) {
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
    public String toString() {
        return Integer.toString(rawValue);
    }

    @Override
    public ClassId applyMask(ClassId mask) {
        return ClassId.of(this.rawValue & mask.rawValue);
    }
}

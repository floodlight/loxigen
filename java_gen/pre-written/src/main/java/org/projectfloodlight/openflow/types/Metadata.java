package org.projectfloodlight.openflow.types;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Metadata {
    static final int LENGTH = 4;
    private final int rawValue;

    private Metadata(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static Metadata of(final int raw) {
        return new Metadata(raw);
    }

    public int getInt() {
        return rawValue;
    }

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
        Metadata other = (Metadata) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(rawValue);
    }
}

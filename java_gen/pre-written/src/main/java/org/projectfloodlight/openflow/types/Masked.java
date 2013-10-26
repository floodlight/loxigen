package org.projectfloodlight.openflow.types;

import com.google.common.hash.PrimitiveSink;



public class Masked<T extends OFValueType<T>> implements OFValueType<Masked<T>> {
    protected T value;
    protected T mask;

    protected Masked(T value, T mask) {
        this.value = value.applyMask(mask);
        this.mask = mask;
    }

    public T getValue() {
        return value;
    }

    public T getMask() {
        return mask;
    }

    public static <T extends OFValueType<T>> Masked<T> of(T value, T mask) {
        return new Masked<T>(value, mask);
    }

    @Override
    public int getLength() {
        return this.value.getLength() + this.mask.getLength();
    }

    @Override
    public String toString() {
        // General representation: value/mask
        StringBuilder sb = new StringBuilder();
        sb.append(value.toString()).append('/').append(mask.toString());
        return sb.toString();
    }

    @Override
    public Masked<T> applyMask(Masked<T> mask) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Masked<?>))
            return false;
        Masked<?> mobj = (Masked<?>)obj;
        return this.value.equals(mobj.value) && this.mask.equals(mobj.mask);
    }

    @Override
    public int hashCode() {
        final int prime = 59;
        int result = 1;
        result = prime * result + this.value.hashCode();
        result = prime * result + this.mask.hashCode();
        return result;
    }

    @Override
    public int compareTo(Masked<T> o) {
        int res = value.compareTo(o.value);
        if(res != 0)
            return res;
        else
            return mask.compareTo(o.mask);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        value.putTo(sink);
        mask.putTo(sink);
    }
}

package org.openflow.types;



public class Masked<T extends OFValueType> implements OFValueType {
    private T value;
    private T mask;
    
    private Masked(T value, T mask) {
        this.value = value;
        this.mask = mask;
    }
    
    public T getValue() {
        return value;
    }
    
    public T getMask() {
        return mask;
    }

    @Override
    public int getLength() {
        return this.value.getLength() + this.mask.getLength();
    }

    volatile byte[] bytesCache = null;
    
    @Override
    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized(this) {
                if (bytesCache == null) {
                    byte[] bytesValue = this.value.getBytes();
                    byte[] bytesMask = this.mask.getBytes();
                    bytesCache = new byte[bytesValue.length + bytesMask.length];
                    System.arraycopy(bytesValue, 0, bytesCache, 0, bytesValue.length);
                    System.arraycopy(bytesMask, 0, bytesCache, bytesValue.length, bytesMask.length);
                }
            }
        }
        return bytesCache;
    }

    public static <T extends OFValueType> Masked<T> of(T value, T mask) {
        return new Masked<T>(value, mask);
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
    public String toString() {
        if (value.getClass() == IPv4.class) {
            // TODO: How to output the mask when not in CIDR notation?
            StringBuilder res = new StringBuilder();
            res.append(((IPv4)value).toString());
            
            int maskint = ((IPv4)mask).getInt();
            if (Integer.bitCount((~maskint) + 1) == 1) {
                // CIDR notation
                res.append('/');
                res.append(Integer.bitCount(maskint));
            } else {
                // Arbitrary mask not in CIDR notation
                // TODO: HERE?
            }
            
            return res.toString();
        } else if (value.getClass() == IPv6.class) {
            // TODO: Return IPv6 string
            StringBuilder sb = new StringBuilder();
            sb.append(value.toString()).append('/').append(mask.toString());
            return sb.toString();
        } else {
            // General representation: value/mask
            StringBuilder sb = new StringBuilder();
            sb.append(value.toString()).append('/').append(mask.toString());
            return sb.toString();
        }
    }
    
}

package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;

import com.google.common.hash.PrimitiveSink;

public class OFBitMask512 implements OFValueType<OFBitMask512> {

    static final int LENGTH = 64;

    private final long raw1;
    private final long raw2;
    private final long raw3;
    private final long raw4;
    private final long raw5;
    private final long raw6;
    private final long raw7;
    private final long raw8;

    public static final OFBitMask512 ALL = new OFBitMask512(-1, -1, -1, -1,
                                                            -1, -1, -1, -1);
    public static final OFBitMask512 NONE = new OFBitMask512(0, 0, 0, 0,
                                                             0, 0, 0, 0);

    public static final OFBitMask512 NO_MASK = ALL;
    public static final OFBitMask512 FULL_MASK = NONE;

    private OFBitMask512(long raw1, long raw2, long raw3, long raw4,
                         long raw5, long raw6, long raw7, long raw8) {
        this.raw1 = raw1;
        this.raw2 = raw2;
        this.raw3 = raw3;
        this.raw4 = raw4;
        this.raw5 = raw5;
        this.raw6 = raw6;
        this.raw7 = raw7;
        this.raw8 = raw8;
    }

    public static OFBitMask512 of(long raw1, long raw2, long raw3, long raw4,
                                  long raw5, long raw6, long raw7, long raw8) {
        if (raw1 == -1 && raw2 == -1 && raw3 == -1 && raw4 == -1
                && raw5 == -1 && raw6 == -1 && raw7 == -1 && raw8 == -1)
            return ALL;
        if (raw1 == 0 && raw2 == 0 && raw3 == 0 && raw4 == 0
                && raw5 == 0 && raw6 == 0 && raw7 == 0 && raw8 == 0)
            return NONE;
        return new OFBitMask512(raw1, raw2, raw3, raw4, raw5, raw6, raw7, raw8);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public OFBitMask512 applyMask(OFBitMask512 mask) {
        return of(this.raw1 & mask.raw1, this.raw2 & mask.raw2,
                  this.raw3 & mask.raw3, this.raw4 & mask.raw4,
                  this.raw5 & mask.raw5, this.raw6 & mask.raw6,
                  this.raw7 & mask.raw7, this.raw8 & mask.raw8);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (raw1 ^ (raw1 >>> 32));
        result = prime * result + (int) (raw2 ^ (raw2 >>> 32));
        result = prime * result + (int) (raw3 ^ (raw3 >>> 32));
        result = prime * result + (int) (raw4 ^ (raw4 >>> 32));
        result = prime * result + (int) (raw5 ^ (raw5 >>> 32));
        result = prime * result + (int) (raw6 ^ (raw6 >>> 32));
        result = prime * result + (int) (raw7 ^ (raw7 >>> 32));
        result = prime * result + (int) (raw8 ^ (raw8 >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OFBitMask512 other = (OFBitMask512) obj;
        if (raw1 != other.raw1) return false;
        if (raw2 != other.raw2) return false;
        if (raw3 != other.raw3) return false;
        if (raw4 != other.raw4) return false;
        if (raw5 != other.raw5) return false;
        if (raw6 != other.raw6) return false;
        if (raw7 != other.raw7) return false;
        if (raw8 != other.raw8) return false;
        return true;
    }

    protected static boolean isBitOn(long raw1, long raw2, long raw3, long raw4,
                                     long raw5, long raw6, long raw7, long raw8, int bit) {
        if (bit < 0 || bit >= 512)
            throw new IndexOutOfBoundsException();
        long word;
        if (bit < 64) {
            word = raw8;
        } else if (bit < 128) {
            word = raw7;
            bit -= 64;
        } else if (bit < 192) {
            word = raw6;
            bit -= 128;
        } else if (bit < 256) {
            word = raw5;
            bit -= 192;
        } else if (bit < 320) {
            word = raw4;
            bit -= 256;
        } else if (bit < 384) {
            word = raw3;
            bit -= 320;
        } else if (bit < 448) {
            word = raw2;
            bit -= 384;
        } else {
            word = raw1;
            bit -= 448;
        }
        return (word & ((long)1 << bit)) != 0;
    }

    public void write64Bytes(ByteBuf cb) {
        cb.writeLong(raw1);
        cb.writeLong(raw2);
        cb.writeLong(raw3);
        cb.writeLong(raw4);
        cb.writeLong(raw5);
        cb.writeLong(raw6);
        cb.writeLong(raw7);
        cb.writeLong(raw8);
    }

    public static OFBitMask512 read64Bytes(ByteBuf cb) {
        long raw1 = cb.readLong();
        long raw2 = cb.readLong();
        long raw3 = cb.readLong();
        long raw4 = cb.readLong();
        long raw5 = cb.readLong();
        long raw6 = cb.readLong();
        long raw7 = cb.readLong();
        long raw8 = cb.readLong();
        return of(raw1, raw2, raw3, raw4, raw5, raw6, raw7, raw8);
    }

    public boolean isOn(int bit) {
        return isBitOn(raw1, raw2, raw3, raw4, raw5, raw6, raw7, raw8, bit);
    }

    @Override
    public String toString() {
        return (String.format("%64s", Long.toBinaryString(raw8))
                + String.format("%64s", Long.toBinaryString(raw7))
                + String.format("%64s", Long.toBinaryString(raw6))
                + String.format("%64s", Long.toBinaryString(raw5))
                + String.format("%64s", Long.toBinaryString(raw4))
                + String.format("%64s", Long.toBinaryString(raw3))
                + String.format("%64s", Long.toBinaryString(raw2))
                + String.format("%64s", Long.toBinaryString(raw1))).replaceAll(" ", "0");
    }

    @Override
    public int compareTo(OFBitMask512 o) {
        long c = this.raw1 - o.raw1;
        if (c != 0)
            return Long.signum(c);
        c = this.raw2 - o.raw2;
        if (c != 0)
            return Long.signum(c);
        c = this.raw3 - o.raw3;
        if (c != 0)
            return Long.signum(c);
        c = this.raw4 - o.raw4;
        if (c != 0)
            return Long.signum(c);
        c = this.raw5 - o.raw5;
        if (c != 0)
            return Long.signum(c);
        c = this.raw6 - o.raw6;
        if (c != 0)
            return Long.signum(c);
        c = this.raw7 - o.raw7;
        if (c != 0)
            return Long.signum(c);
        return Long.signum(this.raw8 - o.raw8);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putLong(raw1);
        sink.putLong(raw2);
        sink.putLong(raw3);
        sink.putLong(raw4);
        sink.putLong(raw5);
        sink.putLong(raw6);
        sink.putLong(raw7);
        sink.putLong(raw8);
    }

}

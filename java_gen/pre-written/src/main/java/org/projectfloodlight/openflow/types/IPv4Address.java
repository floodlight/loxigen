package org.projectfloodlight.openflow.types;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;



/**
 * Wrapper around an IPv4Address address
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class IPv4Address extends IPAddress<IPv4Address> {
    static final int LENGTH = 4;
    private final int rawValue;

    private final static int NONE_VAL = 0x0;
    public final static IPv4Address NONE = new IPv4Address(NONE_VAL);

    public static final IPv4Address NO_MASK = IPv4Address.of(0xFFFFFFFF);
    public static final IPv4Address FULL_MASK = IPv4Address.of(0x00000000);

    private IPv4Address(final int rawValue) {
        this.rawValue = rawValue;
    }

    @Override
    public IPVersion getIpVersion() {
        return IPVersion.IPv4;
    }

    public static IPv4Address of(final byte[] address) {
        if (address.length != LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid byte array length for IPv4Address address: " + address.length);
        }

        int raw =
                (address[0] & 0xFF) << 24 | (address[1] & 0xFF) << 16
                        | (address[2] & 0xFF) << 8 | (address[3] & 0xFF) << 0;
        return IPv4Address.of(raw);
    }

    public static IPv4Address of(final int raw) {
        if(raw == NONE_VAL)
            return NONE;
        return new IPv4Address(raw);
    }

    public static IPv4Address of(final String string) {
        int start = 0;
        int shift = 24;

        int raw = 0;
        while (shift >= 0) {
            int end = string.indexOf('.', start);
            if (end == start || !((shift > 0) ^ (end < 0)))
                throw new IllegalArgumentException("IP Address not well formed: " + string);

            String substr =
                    end > 0 ? string.substring(start, end) : string.substring(start);
            int val = Integer.parseInt(substr);
            if (val < 0 || val > 255)
                throw new IllegalArgumentException("IP Address not well formed: " + string);

            raw |= val << shift;

            shift -= 8;
            start = end + 1;
        }
        return IPv4Address.of(raw);
    }

    public int getInt() {
        return rawValue;
    }

    volatile byte[] bytesCache = null;

    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache =
                            new byte[] { (byte) ((rawValue >>> 24) & 0xFF),
                                    (byte) ((rawValue >>> 16) & 0xFF),
                                    (byte) ((rawValue >>> 8) & 0xFF),
                                    (byte) ((rawValue >>> 0) & 0xFF) };
                }
            }
        }
        return bytesCache;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append((rawValue >> 24) & 0xFF).append('.');
        res.append((rawValue >> 16) & 0xFF).append('.');
        res.append((rawValue >> 8) & 0xFF).append('.');
        res.append((rawValue >> 0) & 0xFF);
        return res.toString();
    }

    public void write4Bytes(ChannelBuffer c) {
        c.writeInt(rawValue);
    }

    public static IPv4Address read4Bytes(ChannelBuffer c) {
        return IPv4Address.of(c.readInt());
    }

    @Override
    public IPv4Address applyMask(IPv4Address mask) {
        return IPv4Address.of(this.rawValue & mask.rawValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytesCache);
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
        IPv4Address other = (IPv4Address) obj;
        if (!Arrays.equals(bytesCache, other.bytesCache))
            return false;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    @Override
    public int compareTo(IPv4Address o) {
        return UnsignedInts.compare(rawValue, o.rawValue);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(rawValue);
    }

}

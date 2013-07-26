package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;



/**
 * Wrapper around an IPv4 address
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class IPv4 implements OFValueType {
    static final int LENGTH = 4;
    private final int rawValue;

    private IPv4(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static IPv4 of(final byte[] address) {
        if (address.length != LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid byte array length for IPv4 address: " + address);
        }

        int raw =
                (address[0] & 0xFF) << 24 | (address[1] & 0xFF) << 16
                        | (address[2] & 0xFF) << 8 | (address[3] & 0xFF) << 0;
        return IPv4.of(raw);
    }

    public static IPv4 of(final int raw) {
        return new IPv4(raw);
    }

    public static IPv4 of(final String string) {
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
        return IPv4.of(raw);
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
        IPv4 other = (IPv4) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }
    
    public void write4Bytes(ChannelBuffer c) {
        c.writeInt(rawValue);
    }
    
    public static IPv4 read4Bytes(ChannelBuffer c) {
        return IPv4.of(c.readInt());
    }
    
}

package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;


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
    
    public static OFValueType ofPossiblyMasked(final String string) {
        int start = 0;
        int shift = 24;
        int slashPos;

        String ip = string;
        int maskBits = 0;
        if ((slashPos = string.indexOf('/')) != -1) {
            ip = string.substring(0, slashPos);
            try {
                String suffix = string.substring(slashPos + 1);
                if (suffix.length() == 0)
                    throw new IllegalArgumentException("IP Address not well formed: " + string);
                maskBits = Integer.parseInt(suffix);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
            if (maskBits < 0 || maskBits > 32) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
        }
        
        int raw = 0;
        while (shift >= 0) {
            int end = ip.indexOf('.', start);
            if (end == start || !((shift > 0) ^ (end < 0)))
                throw new IllegalArgumentException("IP Address not well formed: " + string);

            String substr =
                    end > 0 ? ip.substring(start, end) : ip.substring(start);
            int val;
            try {
                val = Integer.parseInt(substr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
            if (val < 0 || val > 255)
                throw new IllegalArgumentException("IP Address not well formed: " + string);

            raw |= val << shift;

            shift -= 8;
            start = end + 1;
        }
        
        if (maskBits == 0) {
            // No mask
            return IPv4.of(raw);
        } else {
            // With mask
            int mask = (-1) << (32 - maskBits);
            return Masked.<IPv4>of(IPv4.of(raw), IPv4.of(mask));
        }
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
    
    public static final Serializer<IPv4> SERIALIZER_V10 = new SerializerV10();
    public static final Serializer<IPv4> SERIALIZER_V11 = SERIALIZER_V10;
    public static final Serializer<IPv4> SERIALIZER_V12 = SERIALIZER_V10;
    public static final Serializer<IPv4> SERIALIZER_V13 = SERIALIZER_V10;
    
    private static class SerializerV10 implements OFValueType.Serializer<IPv4> {

        @Override
        public void writeTo(IPv4 value, ChannelBuffer c) {
            c.writeInt(value.rawValue);
        }

        @Override
        public IPv4 readFrom(ChannelBuffer c) throws OFParseError {
            return IPv4.of(c.readInt());
        }
        
    }
}

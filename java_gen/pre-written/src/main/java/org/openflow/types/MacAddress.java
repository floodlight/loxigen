package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;
import org.openflow.util.HexString;

/**
 * Wrapper around a 6 byte mac address.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */

public class MacAddress implements OFValueType<MacAddress> {
    static final int MacAddrLen = 6;
    private final long rawValue;

    public static final MacAddress NO_MASK = MacAddress.of(0xFFFFFFFFFFFFFFFFl);
    public static final MacAddress FULL_MASK = MacAddress.of(0x0);

    private MacAddress(final long rawValue) {
        this.rawValue = rawValue;
    }

    public static MacAddress of(final byte[] address) {
        long raw =
                (address[0] & 0xFFL) << 40 | (address[1] & 0xFFL) << 32
                        | (address[2] & 0xFFL) << 24 | (address[3] & 0xFFL) << 16
                        | (address[4] & 0xFFL) << 8 | (address[5] & 0xFFL);
        return MacAddress.of(raw);
    }

    public static MacAddress of(final long raw) {
        return new MacAddress(raw);
    }

    public static MacAddress of(final String string) {
        int index = 0;
        int shift = 40;

        long raw = 0;
        if (string.length() != 6 * 2 + 5)
            throw new IllegalArgumentException("Mac address not well formed: " + string);

        while (shift >= 0) {
            raw |=
                    ((long) (Character.digit(string.charAt(index++), 16) << 4 | Character
                            .digit(string.charAt(index++), 16))) << shift;

            if (shift == 0)
                break;
            if (string.charAt(index++) != ':')
                throw new IllegalArgumentException("Mac address not well formed: " + string);
            shift -= 8;
        }
        return MacAddress.of(raw);
    }

    volatile byte[] bytesCache = null;

    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache =
                            new byte[] { (byte) ((rawValue >> 40) & 0xFF),
                                    (byte) ((rawValue >> 32) & 0xFF),
                                    (byte) ((rawValue >> 24) & 0xFF),
                                    (byte) ((rawValue >> 16) & 0xFF),
                                    (byte) ((rawValue >> 8) & 0xFF),
                                    (byte) ((rawValue >> 0) & 0xFF) };
                }
            }
        }
        return bytesCache;
    }

    @Override
    public int getLength() {
        return MacAddrLen;
    }

    @Override
    public String toString() {
        return HexString.toHexString(rawValue, 6);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (rawValue ^ (rawValue >>> 32));
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
        MacAddress other = (MacAddress) obj;
        if (rawValue != other.rawValue)
            return false;
        return true;
    }

    public long getLong() {
        return rawValue;
    }

    public void write6Bytes(ChannelBuffer c) {
        c.writeInt((int) (this.rawValue >> 16));
        c.writeShort((int) this.rawValue & 0xFFFF);
    }

    public static MacAddress read6Bytes(ChannelBuffer c) throws OFParseError {
        long raw = c.readUnsignedInt() << 16 | c.readUnsignedShort();
        return MacAddress.of(raw);
    }

    @Override
    public MacAddress applyMask(MacAddress mask) {
        return MacAddress.of(this.rawValue & mask.rawValue);
    }
    
    

}

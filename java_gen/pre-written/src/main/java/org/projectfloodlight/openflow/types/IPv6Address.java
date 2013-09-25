package org.projectfloodlight.openflow.types;

import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

/**
 * IPv6 address object. Instance controlled, immutable. Internal representation:
 * two 64 bit longs (not that you'd have to know).
 *
 * @author Andreas Wundsam <andreas.wundsam@teleteach.de>
 */
public class IPv6Address implements OFValueType<IPv6Address> {
    static final int LENGTH = 16;
    private final long raw1;
    private final long raw2;

    private final static long NONE_VAL1 = 0x0L;
    private final static long NONE_VAL2 = 0x0L;
    public static final IPv6Address NONE = new IPv6Address(NONE_VAL1, NONE_VAL2);

    public static final IPv6Address NO_MASK = IPv6Address.of(0xFFFFFFFFFFFFFFFFl, 0xFFFFFFFFFFFFFFFFl);
    public static final IPv6Address FULL_MASK = IPv6Address.of(0x0, 0x0);

    private IPv6Address(final long raw1, final long raw2) {
        this.raw1 = raw1;
        this.raw2 = raw2;
    }

    public static IPv6Address of(final byte[] address) {
        if (address.length != LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid byte array length for IPv6 address: " + address.length);
        }

        long raw1 =
                (address[0] & 0xFFL) << 56 | (address[1] & 0xFFL) << 48
                        | (address[2] & 0xFFL) << 40 | (address[3] & 0xFFL) << 32
                        | (address[4] & 0xFFL) << 24 | (address[5] & 0xFFL) << 16
                        | (address[6] & 0xFFL) << 8 | (address[7]);

        long raw2 =
                (address[8] & 0xFFL) << 56 | (address[9] & 0xFFL) << 48
                        | (address[10] & 0xFFL) << 40 | (address[11] & 0xFFL) << 32
                        | (address[12] & 0xFFL) << 24 | (address[13] & 0xFFL) << 16
                        | (address[14] & 0xFFL) << 8 | (address[15]);

        return IPv6Address.of(raw1, raw2);
    }

    private static class IPv6Builder {
        private long raw1, raw2;

        public void setUnsignedShortWord(final int i, final int value) {
            int shift = 48 - (i % 4) * 16;

            if (value < 0 || value > 0xFFFF)
                throw new IllegalArgumentException("16 bit word must be in [0, 0xFFFF]");

            if (i >= 0 && i < 4)
                raw1 = raw1 & ~(0xFFFFL << shift) | (value & 0xFFFFL) << shift;
            else if (i >= 4 && i < 8)
                raw2 = raw2 & ~(0xFFFFL << shift) | (value & 0xFFFFL) << shift;
            else
                throw new IllegalArgumentException("16 bit word index must be in [0,7]");
        }

        public IPv6Address getIPv6() {
            return IPv6Address.of(raw1, raw2);
        }
    }

    private final static Pattern colonPattern = Pattern.compile(":");

    public static IPv6Address of(final String string) {
        IPv6Builder builder = new IPv6Builder();
        String[] parts = colonPattern.split(string, -1);

        int leftWord = 0;
        int leftIndex = 0;

        boolean hitZeroCompression = false;

        for (leftIndex = 0; leftIndex < parts.length; leftIndex++) {
            String part = parts[leftIndex];
            if (part.length() == 0) {
                // hit empty group of zero compression
                hitZeroCompression = true;
                break;
            }
            builder.setUnsignedShortWord(leftWord++, Integer.parseInt(part, 16));
        }

        if (hitZeroCompression) {
            if (leftIndex == 0) {
                // if colon is at the start, two columns must be at the start,
                // move to the second empty group
                leftIndex = 1;
                if (parts.length < 2 || parts[1].length() > 0)
                    throw new IllegalArgumentException("Malformed IPv6 address: " + string);
            }

            int rightWord = 7;
            int rightIndex;
            for (rightIndex = parts.length - 1; rightIndex > leftIndex; rightIndex--) {
                String part = parts[rightIndex];
                if (part.length() == 0)
                    break;
                builder.setUnsignedShortWord(rightWord--, Integer.parseInt(part, 16));
            }
            if (rightIndex == parts.length - 1) {
                // if colon is at the end, two columns must be at the end, move
                // to the second empty group
                if (rightIndex < 1 || parts[rightIndex - 1].length() > 0)
                    throw new IllegalArgumentException("Malformed IPv6 address: " + string);
                rightIndex--;
            }
            if (leftIndex != rightIndex)
                throw new IllegalArgumentException("Malformed IPv6 address: " + string);
        } else {
            if (leftIndex != 8) {
                throw new IllegalArgumentException("Malformed IPv6 address: " + string);
            }
        }
        return builder.getIPv6();
    }

    public static IPv6Address of(final long raw1, final long raw2) {
        if(raw1==NONE_VAL1 && raw2 == NONE_VAL2)
            return NONE;
        return new IPv6Address(raw1, raw2);
    }

    volatile byte[] bytesCache = null;

    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache =
                            new byte[] { (byte) ((raw1 >> 56) & 0xFF),
                                    (byte) ((raw1 >> 48) & 0xFF),
                                    (byte) ((raw1 >> 40) & 0xFF),
                                    (byte) ((raw1 >> 32) & 0xFF),
                                    (byte) ((raw1 >> 24) & 0xFF),
                                    (byte) ((raw1 >> 16) & 0xFF),
                                    (byte) ((raw1 >> 8) & 0xFF),
                                    (byte) ((raw1 >> 0) & 0xFF),

                                    (byte) ((raw2 >> 56) & 0xFF),
                                    (byte) ((raw2 >> 48) & 0xFF),
                                    (byte) ((raw2 >> 40) & 0xFF),
                                    (byte) ((raw2 >> 32) & 0xFF),
                                    (byte) ((raw2 >> 24) & 0xFF),
                                    (byte) ((raw2 >> 16) & 0xFF),
                                    (byte) ((raw2 >> 8) & 0xFF),
                                    (byte) ((raw2 >> 0) & 0xFF) };
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
        return toString(true, false);
    }

    public int getUnsignedShortWord(final int i) {
        if (i >= 0 && i < 4)
            return (int) ((raw1 >>> (48 - i * 16)) & 0xFFFF);
        else if (i >= 4 && i < 8)
            return (int) ((raw2 >>> (48 - (i - 4) * 16)) & 0xFFFF);
        else
            throw new IllegalArgumentException("16 bit word index must be in [0,7]");
    }

    /** get the index of the first word where to apply IPv6 zero compression */
    public int getZeroCompressStart() {
        int start = Integer.MAX_VALUE;
        int maxLength = -1;

        int candidateStart = -1;

        for (int i = 0; i < 8; i++) {
            if (candidateStart >= 0) {
                // in a zero octect
                if (getUnsignedShortWord(i) != 0) {
                    // end of this candidate word
                    int candidateLength = i - candidateStart;
                    if (candidateLength >= maxLength) {
                        start = candidateStart;
                        maxLength = candidateLength;
                    }
                    candidateStart = -1;
                }
            } else {
                // not in a zero octect
                if (getUnsignedShortWord(i) == 0) {
                    candidateStart = i;
                }
            }
        }

        if (candidateStart >= 0) {
            int candidateLength = 8 - candidateStart;
            if (candidateLength >= maxLength) {
                start = candidateStart;
                maxLength = candidateLength;
            }
        }

        return start;
    }

    public String toString(final boolean zeroCompression, final boolean leadingZeros) {
        StringBuilder res = new StringBuilder();

        int compressionStart = zeroCompression ? getZeroCompressStart() : Integer.MAX_VALUE;
        boolean inCompression = false;
        boolean colonNeeded = false;

        for (int i = 0; i < 8; i++) {
            int word = getUnsignedShortWord(i);

            if (word == 0) {
                if (inCompression)
                    continue;
                else if (i == compressionStart) {
                    res.append(':').append(':');
                    inCompression = true;
                    colonNeeded = false;
                    continue;
                }
            } else {
                inCompression = false;
            }

            if (colonNeeded) {
                res.append(':');
                colonNeeded = false;
            }

            res.append(leadingZeros ? String.format("%04x", word) : Integer.toString(word,
                    16));
            colonNeeded = true;
        }
        return res.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (raw1 ^ (raw1 >>> 32));
        result = prime * result + (int) (raw2 ^ (raw2 >>> 32));
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
        IPv6Address other = (IPv6Address) obj;
        if (raw1 != other.raw1)
            return false;
        if (raw2 != other.raw2)
            return false;
        return true;
    }

    public void write16Bytes(ChannelBuffer c) {
        c.writeLong(this.raw1);
        c.writeLong(this.raw2);
    }

    public static IPv6Address read16Bytes(ChannelBuffer c) throws OFParseError {
        return IPv6Address.of(c.readLong(), c.readLong());
    }

    @Override
    public IPv6Address applyMask(IPv6Address mask) {
        return IPv6Address.of(this.raw1 & mask.raw1, this.raw2 & mask.raw2);
    }
}

package org.projectfloodlight.openflow.types;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.util.HexString;

import com.google.common.base.Preconditions;
import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.Longs;

import io.netty.buffer.ByteBuf;

/**
 * Wrapper around a 6 byte mac address.
 *
 * @author Andreas Wundsam {@literal <}andreas.wundsam@bigswitch.com{@literal >}
 */

public class MacAddress implements OFValueType<MacAddress> {

    static final int MacAddrLen = 6;
    private final long rawValue;

    private final static long NONE_VAL = 0x0L;
    public static final MacAddress NONE = new MacAddress(NONE_VAL);

    private final static long BROADCAST_VAL = 0x0000FFFFFFFFFFFFL;
    public static final MacAddress BROADCAST = new MacAddress(BROADCAST_VAL);

    public static final MacAddress NO_MASK = MacAddress.of(0xFFFFFFFFFFFFFFFFl);
    public static final MacAddress FULL_MASK = MacAddress.of(0x0);

    private static final long LLDP_MAC_ADDRESS_MASK = 0xfffffffffff0L;
    private static final long LLDP_MAC_ADDRESS_VALUE = 0x0180c2000000L;
    private final static MacAddress IPV4_MULTICAST_BASE_ADDRESS =
           MacAddress.of("01:00:5E:00:00:00");
    private final static MacAddress IPV6_MULTICAST_BASE_ADDRESS =
           MacAddress.of("33:33:00:00:00:00");

    private static final String FORMAT_ERROR = "Mac address is not well-formed. " +
            "It must consist of 6 hex digit pairs separated by colons or hyphens: ";
    private static final int MAC_STRING_LENGTH = 6 * 2 + 5;


    private MacAddress(final long rawValue) {
        this.rawValue = rawValue;
    }

    public static MacAddress of(final byte[] address) {
        if (address.length != MacAddrLen)
            throw new IllegalArgumentException(
                    "Mac address byte array must be exactly 6 bytes long; length = " + address.length);
        long raw =
                (address[0] & 0xFFL) << 40 | (address[1] & 0xFFL) << 32
                        | (address[2] & 0xFFL) << 24 | (address[3] & 0xFFL) << 16
                        | (address[4] & 0xFFL) << 8 | (address[5] & 0xFFL);
        return MacAddress.of(raw);
    }

    public static MacAddress of(long raw) {
        raw &= BROADCAST_VAL;
        if(raw == NONE_VAL)
            return NONE;
        if (raw == BROADCAST_VAL)
            return BROADCAST;
        return new MacAddress(raw);
    }

    /** Parse a mac adress from a string representation as
     *  6 hex bytes separated by colons or hyphens (01:02:03:04:05:06,
     *  01-02-03-04-05-06).
     *
     * @param macString - a mac address in string representation
     * @return the parsed MacAddress
     * @throws IllegalArgumentException if macString is not a valid mac adddress
     */
    @Nonnull
    public static MacAddress of(@Nonnull final String macString) throws IllegalArgumentException {
        Preconditions.checkNotNull(macString, "macStringmust not be null");
        Preconditions.checkArgument(macString.length() == MAC_STRING_LENGTH,
                FORMAT_ERROR + macString);
        final char separator = macString.charAt(2);
        Preconditions.checkArgument(separator == ':' || separator == '-',
                FORMAT_ERROR + macString + " (invalid separator)");

        int index = 0;
        int shift = 40;
        long raw = 0;

        while (shift >= 0) {
            int digit1 = Character.digit(macString.charAt(index++), 16);
            int digit2 = Character.digit(macString.charAt(index++), 16);
            if ((digit1 < 0) || (digit2 < 0))
                throw new IllegalArgumentException(FORMAT_ERROR + macString);
            raw |= ((long) (digit1 << 4 | digit2)) << shift;

            if (shift == 0) {
                break;
            }

            // Iterate over separators
            if (macString.charAt(index++) != separator) {
                throw new IllegalArgumentException(FORMAT_ERROR + macString +
                                                   " (inconsistent separators");
            }

            shift -= 8;
        }
        return MacAddress.of(raw);
    }

    /**
     * Creates a {@link MacAddress} from a {@link DatapathId}. This factory
     * method assumes that the first two bytes of the {@link DatapathId} are 0 bytes.
     * @param dpid the {@link DatapathId} to create the {@link MacAddress} from
     * @return a {@link MacAddress} derived from the supplied {@link DatapathId}
     */
    public static MacAddress of(@Nonnull DatapathId dpid) {
        Preconditions.checkNotNull(dpid, "dpid must not be null");

        long raw = dpid.getLong();

        // Mask out valid bytes
        if( (raw & ~BROADCAST_VAL) != 0L) {
            throw new IllegalArgumentException("First two bytes of supplied "
                 + "Datapathid must be 0");
        }
        return of(raw);
    }

    private volatile byte[] bytesCache = null;

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
        return Arrays.copyOf(bytesCache, bytesCache.length);
    }

    /**
     * Returns {@code true} if the MAC address is the broadcast address.
     * @return {@code true} if the MAC address is the broadcast address.
     */
    public boolean isBroadcast() {
        return this == BROADCAST;
    }

    /**
     * Returns {@code true} if the MAC address is a multicast address.
     * @return {@code true} if the MAC address is a multicast address.
     */
    public boolean isMulticast() {
        if (isBroadcast()) {
            return false;
        }
        return (rawValue & (0x01L << 40)) != 0;
    }

    /**
     * Returns {@code true} if the MAC address is an LLDP mac address.
     * @return {@code true} if the MAC address is an LLDP mac address.
     */
    public boolean isLLDPAddress() {
        return (rawValue & LLDP_MAC_ADDRESS_MASK) == LLDP_MAC_ADDRESS_VALUE;
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

    public void write6Bytes(ByteBuf c) {
        c.writeInt((int) (this.rawValue >> 16));
        c.writeShort((int) this.rawValue & 0xFFFF);
    }

    public static MacAddress read6Bytes(ByteBuf c) throws OFParseError {
        long raw = c.readUnsignedInt() << 16 | c.readUnsignedShort();
        return MacAddress.of(raw);
    }

    @Override
    public MacAddress applyMask(MacAddress mask) {
        return MacAddress.of(this.rawValue & mask.rawValue);
    }

    @Override
    public int compareTo(MacAddress o) {
        return Longs.compare(rawValue, o.rawValue);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putLong(rawValue);
    }

    /*
     * Parse an IPv4 Multicast address and return the macAddress
     * corresponding to the multicast IPv4 address.
     *
     * For multicast forwarding, the mac addresses in the range
     * 01-00-5E-00-00-00 to 01-00-5E-7F-FF-FF have been reserved.
     * The most significant 25 bits of the above 48-bit mac address
     * are fixed while the lower 23 bits are variable.
     * These lower 23 bits are derived from the lower 23 bits
     * of the multicast IP address.
     *
     * AND ipv4 address with 0x07FFFFF to extract the last 23 bits
     * OR with 01:00:5E:00:00:00 MAC (first 25 bits)
     *
     * @param ipv4 - ipv4 multicast address
     * @return the MacAddress corresponding to the multicast address
     * @throws IllegalArgumentException if ipv4 is not a valid multicast address
     */
    @Nonnull
    public static MacAddress forIPv4MulticastAddress(IPv4Address ipv4)
            throws IllegalArgumentException {

        if (!ipv4.isMulticast())
            throw new IllegalArgumentException(
                    "Not a Multicast IPAddress\"" + ipv4 + "\"");

        long ipLong = ipv4.getInt();
        int ipMask = 0x007FFFFF;
        ipLong = ipLong & ipMask;

        long macLong = IPV4_MULTICAST_BASE_ADDRESS.getLong(); // 01:00:5E:00:00:00
        macLong = macLong | ipLong;
        MacAddress returnMac = MacAddress.of(macLong);

        return returnMac;
    }

    /**
     * Generate a MAC address corresponding to multicast IPv6  address.
     *
     * Take the last 4 bytes of IPv6 address and copy them to the base IPv6
     * multicast mac address - 33:33:00:00:00:00.
     *
     * @param ipv6 - IPv6 address corresponding to which multicast MAC addr
     * need to be generated.
     * @return - the generated multicast mac address.
     * @throws IllegalArgumentException if ipv6 address is not a valid IPv6
     * multicast address.
     */
    @Nonnull
    public static MacAddress forIPv6MulticastAddr(IPv6Address ipv6)
            throws IllegalArgumentException {
        if (!ipv6.isMulticast()) {
            throw new IllegalArgumentException(
                    "Not a Multicast IPv6Address\"" + ipv6 + "\"");
        }
        long ipLong = ((ipv6.getUnsignedShortWord(6) << 16) |
                                         ipv6.getUnsignedShortWord(7));
        long ipMask = 0xFFFFFFFFl;
        ipLong = ipLong & ipMask;

        long macLong = IPV6_MULTICAST_BASE_ADDRESS.getLong();//33:33:00:00:00:00
        macLong = macLong | ipLong;
        MacAddress returnMac = MacAddress.of(macLong);

        return returnMac;
    }
}

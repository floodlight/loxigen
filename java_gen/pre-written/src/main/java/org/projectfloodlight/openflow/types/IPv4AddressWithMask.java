package org.projectfloodlight.openflow.types;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;


public class IPv4AddressWithMask extends IPAddressWithMask<IPv4Address> {
    public final static IPv4AddressWithMask NONE = of(IPv4Address.NONE, IPv4Address.NONE);

    /**
     * Reserved networks as per RFC 3330
     * Address Block             Present Use                       Reference
     ---------------------------------------------------------------------
     * 0.0.0.0/8            "This" Network                 [RFC1700, page 4]
     * 10.0.0.0/8           Private-Use Networks                   [RFC1918]
     * 14.0.0.0/8           Public-Data Networks         [RFC1700, page 181]
     * 24.0.0.0/8           Cable Television Networks                    --
     * 39.0.0.0/8           Reserved but subject
     *                      to allocation                       [RFC1797]
     * 127.0.0.0/8          Loopback                       [RFC1700, page 5]
     * 128.0.0.0/16         Reserved but subject
     *                      to allocation                             --
     * 169.254.0.0/16       Link Local                                   --
     * 172.16.0.0/12        Private-Use Networks                   [RFC1918]
     * 191.255.0.0/16       Reserved but subject
     *                      to allocation                             --
     * 192.0.0.0/24         Reserved but subject
     *                      to allocation                             --
     * 192.0.2.0/24         Test-Net
     * 192.88.99.0/24       6to4 Relay Anycast                     [RFC3068]
     * 192.168.0.0/16       Private-Use Networks                   [RFC1918]
     * 198.18.0.0/15        Network Interconnect
     *                      Device Benchmark Testing            [RFC2544]
     * 223.255.255.0/24     Reserved but subject
     *                      to allocation                             --
     * 224.0.0.0/4          Multicast                              [RFC3171]
     * 240.0.0.0/4          Reserved for Future Use        [RFC1700, page 4]

     */
    public static final IPAddressWithMask<?> THIS_NETWORK =
            IPAddressWithMask.of("0.0.0.0/8");
    public static final IPAddressWithMask<?> PRIVATE1_NETWORK =
            IPAddressWithMask.of("10.0.0.0/8");
    public static final IPAddressWithMask<?> PRIVATE2_NETWORK =
            IPAddressWithMask.of("172.16.0.0/12");
    public static final IPAddressWithMask<?> PRIVATE3_NETWORK =
            IPAddressWithMask.of("192.168.0.0/16");
    public static final IPAddressWithMask<?> PUBLIC_DATA_NETWORK =
            IPAddressWithMask.of("14.0.0.0/8");
    public static final IPAddressWithMask<?> CABLE_TV_NETWORK =
            IPAddressWithMask.of("24.0.0.0/8");

    public static final IPAddressWithMask<?> INTERNET_HOST_LOOPBACK_NETWORK =
            IPAddressWithMask.of("127.0.0.0/8");
    public static final IPAddressWithMask<?> LINK_LOCAL_NETWORK =
            IPAddressWithMask.of("169.254.0.0/16");
    public static final IPAddressWithMask<?> TEST_NET =
            IPAddressWithMask.of("192.0.2.0/24");
    public static final IPAddressWithMask<?> RELAY_ANYCAST_NETWORK =
            IPAddressWithMask.of("192.88.99.0/24");
    public static final IPAddressWithMask<?> INTERCONNECT_NETWORK =
            IPAddressWithMask.of("198.18.0.0/15");

    public static final IPAddressWithMask<?> MULTICAST_NETWORK =
            IPAddressWithMask.of("224.0.0.0/4");

    public static final IPAddressWithMask<?> RESERVED1_NETWORK =
            IPAddressWithMask.of("39.0.0.0/8");
    public static final IPAddressWithMask<?> RESERVED2_NETWORK =
            IPAddressWithMask.of("128.0.0.0/16");
    public static final IPAddressWithMask<?> RESERVED3_NETWORK =
            IPAddressWithMask.of("191.255.0.0/16");
    public static final IPAddressWithMask<?> RESERVED4_NETWORK =
            IPAddressWithMask.of("192.0.0.0/24");
    public static final IPAddressWithMask<?> RESERVED5_NETWORK =
            IPAddressWithMask.of("223.255.255.0/24");
    public static final IPAddressWithMask<?> RESERVED_FUTURE =
            IPAddressWithMask.of("240.0.0.0/4");


    private IPv4AddressWithMask(int rawValue, int rawMask) {
        super(IPv4Address.of(rawValue), IPv4Address.of(rawMask));
    }

    private IPv4AddressWithMask(IPv4Address value, IPv4Address mask) {
        super(value, mask);
    }

    @Override
    public IPVersion getIpVersion() {
        return IPVersion.IPv4;
    }

    /**
     * Returns an {@code IPv4AddressWithMask} object that represents the given
     * raw IP address masked by the given raw IP address mask.
     *
     * @param rawValue  the raw IP address to be masked
     * @param rawMask   the raw IP address mask
     * @return          an {@code IPv4AddressWithMask} object that represents
     *                  the given raw IP address masked by the given raw IP
     *                  address mask
     * @deprecated      replaced by {@link IPv4Address#of(int)} and
     *                  {@link IPv4Address#withMask(IPv4Address)}, e.g. <code>
     *                  IPv4Address.of(int).withMask(IPv4Address.of(int))
     *                  </code>
     */
    @Nonnull
    @Deprecated
    public static IPv4AddressWithMask of(final int rawValue, final int rawMask) {
        return new IPv4AddressWithMask(rawValue, rawMask);
    }

    /**
     * Returns an {@code IPv4AddressWithMask} object that represents the given
     * IP address masked by the given IP address mask. Both arguments are given
     * as {@code IPv4Address} objects.
     *
     * @param value  the IP address to be masked
     * @param mask   the IP address mask
     * @return       an {@code IPv4AddressWithMask} object that represents
     *               the given IP address masked by the given IP address mask
     * @throws NullPointerException  if any of the given {@code IPv4Address}
     *                               objects were {@code null}
     */
    @Nonnull
    public static IPv4AddressWithMask of(
            @Nonnull final IPv4Address value,
            @Nonnull final IPv4Address mask) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkNotNull(mask, "mask must not be null");

        return new IPv4AddressWithMask(value, mask);
    }

    /**
     * Returns an {@code IPv4AddressWithMask} object that corresponds to
     * the given string in CIDR notation or other acceptable notations.
     * <p>
     * The following notations are accepted.
     * <table summary=""><tr>
     * <th>Notation</th><th>Example</th><th>Notes</th>
     * </tr><tr>
     * <td>IPv4 address only</td><td>{@code 1.2.3.4}</td><td>The subnet mask of
     * prefix length 32 (i.e. {@code 255.255.255.255}) is assumed.</td>
     * </tr><tr>
     * <td>IPv4 address/mask</td><td>{@code 1.2.3.4/255.255.255.0}</td>
     * </tr><tr>
     * <td>CIDR notation</td><td>{@code 1.2.3.4/24}</td>
     * </tr></table>
     *
     * @param string  the string in acceptable notations
     * @return        an {@code IPv4AddressWithMask} object that corresponds to
     *                the given string in acceptable notations
     * @throws NullPointerException      if the given string was {@code null}
     * @throws IllegalArgumentException  if the given string was malformed
     */
    @Nonnull
    public static IPv4AddressWithMask of(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string must not be null");

        int slashPos;
        String ip = string;
        int cidrMaskLength = 32;
        IPv4Address maskAddress = null;

        // Read mask suffix
        if ((slashPos = string.indexOf('/')) != -1) {
            ip = string.substring(0, slashPos);
            try {
                String suffix = string.substring(slashPos + 1);
                if (suffix.length() == 0)
                    throw new IllegalArgumentException("IP Address not well formed: " + string);
                if (suffix.indexOf('.') != -1) {
                    // Full mask
                    maskAddress = IPv4Address.of(suffix);
                } else {
                    // CIDR Suffix
                    cidrMaskLength = Integer.parseInt(suffix);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
        }

        // Read IP
        IPv4Address ipv4 = IPv4Address.of(ip);

        if (maskAddress != null) {
            // Full address mask
            return IPv4AddressWithMask.of(ipv4, maskAddress);
        } else {
            return IPv4AddressWithMask.of(
                    ipv4, IPv4Address.ofCidrMaskLength(cidrMaskLength));
        }
    }

    @Override
    public boolean contains(IPAddress<?> ip) {
        Preconditions.checkNotNull(ip, "ip must not be null");

        if(ip.getIpVersion() == IPVersion.IPv4) {
            IPv4Address ipv4 = (IPv4Address) ip;
            return this.matches(ipv4);
        }

        return false;
    }
}

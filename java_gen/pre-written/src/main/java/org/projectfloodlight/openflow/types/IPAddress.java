package org.projectfloodlight.openflow.types;

import javax.annotation.Nonnull;

public abstract class IPAddress<F extends IPAddress<F>> implements OFValueType<F> {

    public abstract IPVersion getIpVersion();

    /**
     * Checks if this IPAddress represents a valid CIDR style netmask, i.e.,
     * it has a set of leading "1" bits followed by only "0" bits
     * @return true if this represents a valid CIDR style netmask, false
     * otherwise
     */
    public abstract boolean isCidrMask();

    /**
     * If this IPAddress represents a valid CIDR style netmask (see
     * isCidrMask()) returns the length of the prefix (the number of "1" bits).
     * @return length of CIDR mask if this represents a valid CIDR mask
     * @throws IllegalStateException if isCidrMask() == false
     */
    public abstract int asCidrMaskLength();

    /**
     * Checks if the IPAddress is the global broadcast address
     * 255.255.255.255 in case of IPv4
     * @return boolean true or false
     */
    public abstract boolean isBroadcast();

    /**
     * Perform a low level AND operation on the bits of two IPAddress<?> objects
     * @param   other IPAddress<?>
     * @return  new IPAddress<?> object after the AND oper
     */
    public abstract F and(F other);

    /**
     * Perform a low level OR operation on the bits of two IPAddress<?> objects
     * @param   other IPAddress<?>
     * @return  new IPAddress<?> object after the AND oper
     */
    public abstract F or(F other);

    /**
     * Returns a new IPAddress object with the bits inverted
     * @return  IPAddress<?>
     */
    public abstract F not();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    /** parse an IPv4Address or IPv6Address from their conventional string representation.
     *  For details on supported representations,  refer to {@link IPv4Address#of(String)}
     *  and {@link IPv6Address#of(String)}
     *
     * @param ip a string representation of an IP address
     * @return the parsed IP address
     * @throws NullPointerException if ip is null
     * @throws IllegalArgumentException if string is not a valid IP address
     */
    @Nonnull
    public static IPAddress<?> of(@Nonnull String ip) {
        if (ip == null) {
            throw new NullPointerException("String ip must not be null");
        }
        if (ip.indexOf('.') != -1)
            return IPv4Address.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6Address.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

}

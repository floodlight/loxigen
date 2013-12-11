package org.projectfloodlight.openflow.types;

public abstract class IPAddress<F extends IPAddress<F>> implements OFValueType<F> {

    public abstract IPVersion getIpVersion();

    /**
     * Checks if this IPAddress represents a valid CIDR style netmask, i.e.,
     * it has a set of leading "1" bits followed by only "0" bits
     * @return true if this represents a valid CIDR style netmask, false
     * otherwise
     */
    public boolean isCidrMask() {
        return asCidrMaskLength() != -1;
    }

    /**
     * If this IPAddress represents a valid CIDR style netmask (see
     * isCidrMask()) returns the length of the prefix (the number of "1" bits).
     * @return length of CIDR mask or -1 if this is not a CIDR netmask
     */
    public abstract int asCidrMaskLength();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    public static IPAddress<?> of(String ip) {
        if (ip.indexOf('.') != -1)
            return IPv4Address.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6Address.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

}

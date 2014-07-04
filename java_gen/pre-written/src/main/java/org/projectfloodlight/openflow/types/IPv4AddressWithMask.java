package org.projectfloodlight.openflow.types;

import com.google.common.base.Preconditions;


public class IPv4AddressWithMask extends IPAddressWithMask<IPv4Address> {
    public final static IPv4AddressWithMask NONE = of(IPv4Address.NONE, IPv4Address.NONE);

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

    public static IPv4AddressWithMask of(int rawValue, int rawMask) {
        return new IPv4AddressWithMask(rawValue, rawMask);
    }

    public static IPv4AddressWithMask of(IPv4Address value, IPv4Address mask) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkNotNull(mask, "mask must not be null");

        return new IPv4AddressWithMask(value, mask);
    }

    public static IPv4AddressWithMask of(final String string) {
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

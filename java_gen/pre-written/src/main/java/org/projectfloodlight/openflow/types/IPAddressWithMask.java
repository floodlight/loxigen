package org.projectfloodlight.openflow.types;

import com.google.common.base.Preconditions;


public abstract class IPAddressWithMask<F extends IPAddress<F>> extends Masked<F> {

    protected IPAddressWithMask(F value, F mask) {
        super(value, mask);
    }

    public abstract IPVersion getIpVersion();

    public F getSubnetBroadcastAddress() {
        if (!mask.isCidrMask()) {
            throw new IllegalArgumentException("Mask Invalid " + mask +
                                               " cannot get subnet for non CIDR mask");
        }
        return value.or(mask.not());
    }

    public boolean isSubnetBroadcastAddress(F candidate) {
        return getSubnetBroadcastAddress().equals(candidate);
    }

    public static IPAddressWithMask<?> of(String ip) {
        Preconditions.checkNotNull(ip, "string ip must not be null");

        if (ip.indexOf('.') != -1)
            return IPv4AddressWithMask.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6AddressWithMask.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(value.toString());

        res.append('/');
        if (mask.isCidrMask()) {
            // CIDR notation
            res.append(mask.asCidrMaskLength());
        } else {
            // Full address mask
            res.append(mask.toString());
        }

        return res.toString();
    }

    public boolean contains(IPAddress<?> ip) {
        Preconditions.checkNotNull(ip, "ip must not be null");

        // Ensure mask and IP of the same version
        if(getIpVersion() == ip.getIpVersion()) {
            if(getIpVersion() == IPVersion.IPv4) {
                IPv4AddressWithMask ipv4mask = (IPv4AddressWithMask) this;
                IPv4Address ipv4 = (IPv4Address) ip;
                return ipv4mask.matches(ipv4);
            } else {
                IPv6AddressWithMask ipv6mask = (IPv6AddressWithMask) this;
                IPv6Address ipv6 = (IPv6Address) ip;
                return ipv6mask.matches(ipv6);
            }
        }
        return false;
    }

}

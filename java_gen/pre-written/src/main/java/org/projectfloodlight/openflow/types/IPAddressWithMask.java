package org.projectfloodlight.openflow.types;


public abstract class IPAddressWithMask<F extends IPAddress<F>> extends Masked<F> {

    protected IPAddressWithMask(F value, F mask) {
        super(value, mask);
    }

    public abstract IPVersion getIpVersion();

    public static IPAddressWithMask<?> of(String ip) {
        if (ip.indexOf('.') != -1)
            return IPv4AddressWithMask.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6AddressWithMask.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

}

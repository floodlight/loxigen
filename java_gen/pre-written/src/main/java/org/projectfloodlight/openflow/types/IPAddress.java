package org.projectfloodlight.openflow.types;

public abstract class IPAddress<F extends IPAddress<F>> implements OFValueType<F> {

    public abstract IPVersion getIpVersion();

    public static IPAddress<?> of(String ip) {
        if (ip.indexOf('.') != -1)
            return IPv4Address.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6Address.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

}

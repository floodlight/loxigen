            // normalize wildcard fields to mimic old OpenVSwitch behavior. When prerequisites for a field were not met
            // e.g., eth_type is not set to 0x800, old OVS would set the value of the corresponding ignored fields (e.g.,
            // ip_src, tcp_dst) to 0, AND ALSO SET THE WILDCARD to 0. It doesn't do that any more as of 1.1.2 and 1.4
            if(ethType.equals(EthType.IPv4)) {
                // IP
                if(ipProto.equals(IpProtocol.IP_PROTO_TCP) || ipProto.equals(IpProtocol.IP_PROTO_UDP) || ipProto.equals(IpProtocol.IP_PROTO_ICMP)) {
                    // fully speced, wildcards and all values are fine
                    // normalize 32-63 ipv4 src 'mask' to a full bitmask
                    if((wildcards & OFPFW_NW_SRC_ALL) != 0)
                        wildcards |= OFPFW_NW_SRC_MASK;

                    // normalize 32-63 ipv4 dst 'mask' to a full bitmask
                    if((wildcards & OFPFW_NW_DST_ALL) != 0)
                        wildcards |= OFPFW_NW_DST_MASK;

                } else {
                    // normalize 32-63 ipv4 src 'mask' to a full bitmask
                    if((wildcards & OFPFW_NW_SRC_ALL) != 0)
                        wildcards |= OFPFW_NW_SRC_MASK;

                    // normalize 32-63 ipv4 dst 'mask' to a full bitmask
                    if((wildcards & OFPFW_NW_DST_ALL) != 0)
                        wildcards |= OFPFW_NW_DST_MASK;

                    // not TCP/UDP/ICMP -> Clear TP wildcards for the wire
                    wildcards &= ~(OFPFW_TP_SRC | OFPFW_TP_DST);
                    tcpSrc = TransportPort.NONE;
                    tcpDst = TransportPort.NONE;
                }
            } else if (ethType.equals(EthType.ARP)) {
                // normalize 32-63 ipv4 src 'mask' to a full bitmask
                if((wildcards & OFPFW_NW_SRC_ALL) != 0)
                    wildcards |= OFPFW_NW_SRC_MASK;

                // normalize 32-63 ipv4 dst 'mask' to a full bitmask
                if((wildcards & OFPFW_NW_DST_ALL) != 0)
                    wildcards |= OFPFW_NW_DST_MASK;

                // ARP: clear NW_TOS / TP wildcards for the wire
                wildcards &= ~( OFPFW_NW_TOS | OFPFW_TP_SRC | OFPFW_TP_DST);
                ipDscp = IpDscp.NONE;
                tcpSrc = TransportPort.NONE;
                tcpDst = TransportPort.NONE;
            } else {
                // not even IP. Clear NW/TP wildcards for the wire
                wildcards &= ~( OFPFW_NW_TOS | OFPFW_NW_PROTO | OFPFW_NW_SRC_MASK | OFPFW_NW_DST_MASK | OFPFW_TP_SRC | OFPFW_TP_DST);
                ipDscp = IpDscp.NONE;
                ipProto = IpProtocol.NONE;
                ipv4Src = IPv4Address.NONE;
                ipv4Dst = IPv4Address.NONE;
                tcpSrc = TransportPort.NONE;
                tcpDst = TransportPort.NONE;
            }

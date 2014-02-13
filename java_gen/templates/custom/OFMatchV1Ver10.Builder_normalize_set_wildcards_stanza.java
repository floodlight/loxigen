            // normalize match fields according to current OpenVSwitch behavior. When prerequisites for a field are not met
            // e.g., eth_type is not set to 0x800, OVS sets the value of corresponding ignored fields (e.g.,
            // ip_src, tcp_dst) to 0, and sets the wildcard bit to 1.
            if(ethType.equals(EthType.IPv4)) {
                // IP
                if(ipProto.equals(IpProtocol.TCP) || ipProto.equals(IpProtocol.UDP) || ipProto.equals(IpProtocol.ICMP)) {
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
                    wildcards |= (OFPFW_TP_SRC | OFPFW_TP_DST);
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
                wildcards |= ( OFPFW_NW_TOS | OFPFW_TP_SRC | OFPFW_TP_DST);
                ipDscp = IpDscp.NONE;
                tcpSrc = TransportPort.NONE;
                tcpDst = TransportPort.NONE;
            } else {
                // not even IP. Clear NW/TP wildcards for the wire
                wildcards |= ( OFPFW_NW_TOS | OFPFW_NW_PROTO | OFPFW_NW_SRC_MASK | OFPFW_NW_DST_MASK | OFPFW_TP_SRC | OFPFW_TP_DST);
                ipDscp = IpDscp.NONE;
                ipProto = IpProtocol.NONE;
                ipv4Src = IPv4Address.NONE;
                ipv4Dst = IPv4Address.NONE;
                tcpSrc = TransportPort.NONE;
                tcpDst = TransportPort.NONE;
            }

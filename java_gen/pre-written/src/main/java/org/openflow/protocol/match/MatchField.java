package org.openflow.protocol.match;

import org.openflow.types.ArpOpcode;
import org.openflow.types.EthType;
import org.openflow.types.ICMPv4Code;
import org.openflow.types.ICMPv4Type;
import org.openflow.types.IPv4;
import org.openflow.types.IPv6;
import org.openflow.types.IPv6FlowLabel;
import org.openflow.types.IpDscp;
import org.openflow.types.IpEcn;
import org.openflow.types.IpProtocol;
import org.openflow.types.MacAddress;
import org.openflow.types.OFMetadata;
import org.openflow.types.OFPort;
import org.openflow.types.OFValueType;
import org.openflow.types.TransportPort;
import org.openflow.types.VlanPcp;
import org.openflow.types.VlanVid;

@SuppressWarnings("unchecked")
public class MatchField<F extends OFValueType<F>> {
    private final String name;
    public final MatchFields id;
    private final Prerequisite<?>[] prerequisites;

    private MatchField(final String name, final MatchFields id, Prerequisite<?>... prerequisites) {
        this.name = name;
        this.id = id;
        this.prerequisites = prerequisites;
    }

    public final static MatchField<OFPort> IN_PORT =
            new MatchField<OFPort>("in_port", MatchFields.IN_PORT);
    
    public final static MatchField<OFPort> IN_PHY_PORT =
            new MatchField<OFPort>("in_phy_port", MatchFields.PHYSICAL_PORT,
                    new Prerequisite<OFPort>(MatchField.IN_PORT));
    
    public final static MatchField<OFMetadata> METADATA =
            new MatchField<OFMetadata>("metadata", MatchFields.METADATA);

    public final static MatchField<MacAddress> ETH_DST =
            new MatchField<MacAddress>("eth_dst", MatchFields.ETH_DST);
    
    public final static MatchField<MacAddress> ETH_SRC =
            new MatchField<MacAddress>("eth_src", MatchFields.ETH_SRC);

    public final static MatchField<EthType> ETH_TYPE =
            new MatchField<EthType>("eth_type", MatchFields.ETH_TYPE);
    
    public final static MatchField<VlanVid> VLAN_VID =
            new MatchField<VlanVid>("vlan_vid", MatchFields.VLAN_VID);
    
    public final static MatchField<VlanPcp> VLAN_PCP =
            new MatchField<VlanPcp>("vlan_pcp", MatchFields.VLAN_PCP,
                    new Prerequisite<VlanVid>(MatchField.VLAN_VID));
    
    public final static MatchField<IpDscp> IP_DSCP =
            new MatchField<IpDscp>("ip_dscp", MatchFields.IP_DSCP,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv4, EthType.ETH_TYPE_IPv6));
    
    public final static MatchField<IpEcn> IP_ECN =
            new MatchField<IpEcn>("ip_dscp", MatchFields.IP_ECN,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv4, EthType.ETH_TYPE_IPv6));
    
    public final static MatchField<IpProtocol> IP_PROTO =
            new MatchField<IpProtocol>("ip_proto", MatchFields.IP_PROTO,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv4, EthType.ETH_TYPE_IPv6));

    public final static MatchField<IPv4> IPV4_SRC =
            new MatchField<IPv4>("ipv4_src", MatchFields.IPV4_SRC,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv4));
    
    public final static MatchField<IPv4> IPV4_DST =
            new MatchField<IPv4>("ipv4_dst", MatchFields.IPV4_DST,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv4));

    public final static MatchField<TransportPort> TCP_SRC = new MatchField<TransportPort>(
            "tcp_src", MatchFields.TCP_SRC,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_TCP));
    
    public final static MatchField<TransportPort> TCP_DST = new MatchField<TransportPort>(
            "tcp_dst", MatchFields.TCP_DST,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_TCP));

    public final static MatchField<TransportPort> UDP_SRC = new MatchField<TransportPort>(
            "udp_src", MatchFields.UDP_SRC,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_UDP));
    
    public final static MatchField<TransportPort> UDP_DST = new MatchField<TransportPort>(
            "udp_dst", MatchFields.UDP_DST,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_UDP));

    public final static MatchField<TransportPort> SCTP_SRC = new MatchField<TransportPort>(
            "sctp_src", MatchFields.SCTP_SRC,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_SCTP));
    
    public final static MatchField<TransportPort> SCTP_DST = new MatchField<TransportPort>(
            "sctp_dst", MatchFields.SCTP_DST,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_SCTP));

    public final static MatchField<ICMPv4Type> ICMPV4_TYPE = new MatchField<ICMPv4Type>(
            "icmpv4_src", MatchFields.ICMPV4_TYPE,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_ICMP));
    
    public final static MatchField<ICMPv4Code> ICMPV4_CODE = new MatchField<ICMPv4Code>(
            "icmpv4_dst", MatchFields.ICMPV4_CODE,
            new Prerequisite<IpProtocol>(MatchField.IP_PROTO, IpProtocol.IP_PROTO_ICMP));

    public final static MatchField<ArpOpcode> ARP_OP = new MatchField<ArpOpcode>(
            "arp_op", MatchFields.ARP_OP,
            new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_ARP));
    
    public final static MatchField<IPv4> ARP_SPA =
            new MatchField<IPv4>("arp_spa", MatchFields.ARP_SPA,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_ARP));
    
    public final static MatchField<IPv4> ARP_TPA =
            new MatchField<IPv4>("arp_tpa", MatchFields.ARP_TPA,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_ARP));
    
    public final static MatchField<MacAddress> ARP_SHA =
            new MatchField<MacAddress>("arp_sha", MatchFields.ARP_SHA,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_ARP));
    
    public final static MatchField<MacAddress> ARP_THA =
            new MatchField<MacAddress>("arp_tha", MatchFields.ARP_THA,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_ARP));

    public final static MatchField<IPv6> IPV6_SRC =
            new MatchField<IPv6>("ipv6_src", MatchFields.IPV6_SRC,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv6));
    
    public final static MatchField<IPv6> IPV6_DST =
            new MatchField<IPv6>("ipv6_dst", MatchFields.IPV6_DST,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv6));

    public final static MatchField<IPv6FlowLabel> IPV6_FLABEL =
            new MatchField<IPv6FlowLabel>("ipv6_flabel", MatchFields.IPV6_FLOWLABEL,
                    new Prerequisite<EthType>(MatchField.ETH_TYPE, EthType.ETH_TYPE_IPv6));

    public String getName() {
        return name;
    }
    
    public boolean arePrerequisitesOK(Match match) {
        for (Prerequisite<?> p : this.prerequisites) {
            if (!p.isStaisfied(match)) {
                return false;
            }
        }
        return true;
    }

}

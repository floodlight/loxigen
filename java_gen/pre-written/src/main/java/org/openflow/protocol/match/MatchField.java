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
import org.openflow.types.Masked;
import org.openflow.types.OFPort;
import org.openflow.types.OFValueType;
import org.openflow.types.TransportPort;
import org.openflow.types.VlanPcp;
import org.openflow.types.VlanVid;

public class MatchField<F extends OFValueType<F>> {
    private final String name;
    public final MatchFields id;

    private MatchField(final String name, final MatchFields id) {
        this.name = name;
        this.id = id;
    }

    public final static MatchField<OFPort> IN_PORT =
            new MatchField<OFPort>("in_port", MatchFields.IN_PORT);
    public final static MatchField<OFPort> IN_PHY_PORT =
            new MatchField<OFPort>("in_phy_port", MatchFields.PHYSICAL_PORT);
    public final static MatchField<OFPort> METADATA =
            new MatchField<OFPort>("metadata", MatchFields.METADATA);

    public final static MatchField<Masked<MacAddress>> ETH_DST =
            new MatchField<Masked<MacAddress>>("eth_dst", MatchFields.ETH_DST);
    public final static MatchField<Masked<MacAddress>> ETH_SRC =
            new MatchField<Masked<MacAddress>>("eth_src", MatchFields.ETH_SRC);

    public final static MatchField<EthType> ETH_TYPE =
            new MatchField<EthType>("eth_type", MatchFields.ETH_TYPE);
    
    public final static MatchField<Masked<VlanVid>> VLAN_VID =
            new MatchField<Masked<VlanVid>>("vlan_vid", MatchFields.VLAN_VID);
    public final static MatchField<VlanPcp> VLAN_PCP =
            new MatchField<VlanPcp>("vlan_pcp", MatchFields.VLAN_PCP);
    
    
    public final static MatchField<IpDscp> IP_DSCP =
            new MatchField<IpDscp>("ip_dscp", MatchFields.IP_DSCP);
    public final static MatchField<IpEcn> IP_ECN =
            new MatchField<IpEcn>("ip_dscp", MatchFields.IP_ECN);
    public final static MatchField<IpProtocol> IP_PROTO =
            new MatchField<IpProtocol>("ip_proto", MatchFields.IP_PROTO);

    public final static MatchField<Masked<IPv4>> IPV4_SRC =
            new MatchField<Masked<IPv4>>("ipv4_src", MatchFields.IPV4_SRC);
    public final static MatchField<Masked<IPv4>> IPV4_DST =
            new MatchField<Masked<IPv4>>("ipv4_dst", MatchFields.IPV4_DST);

    public final static MatchField<TransportPort> TCP_SRC = new MatchField<TransportPort>(
            "tcp_src", MatchFields.TCP_SRC);
    public final static MatchField<TransportPort> TCP_DST = new MatchField<TransportPort>(
            "tcp_dst", MatchFields.TCP_DST);

    public final static MatchField<TransportPort> UDP_SRC = new MatchField<TransportPort>(
            "udp_src", MatchFields.UDP_SRC);
    public final static MatchField<TransportPort> UDP_DST = new MatchField<TransportPort>(
            "udp_dst", MatchFields.UDP_DST);

    public final static MatchField<TransportPort> SCTP_SRC = new MatchField<TransportPort>(
            "sctp_src", MatchFields.SCTP_SRC);
    public final static MatchField<TransportPort> SCTP_DST = new MatchField<TransportPort>(
            "sctp_dst", MatchFields.SCTP_DST);

    public final static MatchField<ICMPv4Type> ICMPV4_TYPE = new MatchField<ICMPv4Type>(
            "icmpv4_src", MatchFields.ICMPV4_TYPE);
    public final static MatchField<ICMPv4Code> ICMPV4_CODE = new MatchField<ICMPv4Code>(
            "icmpv4_dst", MatchFields.ICMPV4_CODE);

    public final static MatchField<ArpOpcode> ARP_OP = new MatchField<ArpOpcode>(
            "arp_op", MatchFields.ARP_OP);
    public final static MatchField<Masked<IPv4>> ARP_SPA =
            new MatchField<Masked<IPv4>>("arp_spa", MatchFields.ARP_SPA);
    public final static MatchField<Masked<IPv4>> ARP_TPA =
            new MatchField<Masked<IPv4>>("arp_tpa", MatchFields.ARP_TPA);
    public final static MatchField<Masked<MacAddress>> ARP_SHA =
            new MatchField<Masked<MacAddress>>("arp_sha", MatchFields.ARP_SHA);
    public final static MatchField<Masked<MacAddress>> ARP_THA =
            new MatchField<Masked<MacAddress>>("arp_tha", MatchFields.ARP_THA);

    public final static MatchField<Masked<IPv6>> IPV6_SRC =
            new MatchField<Masked<IPv6>>("ipv6_src", MatchFields.IPV6_SRC);
    public final static MatchField<Masked<IPv6>> IPV6_DST =
            new MatchField<Masked<IPv6>>("ipv6_dst", MatchFields.IPV6_DST);

    public final static MatchField<Masked<IPv6FlowLabel>> IPV6_FLABEL =
            new MatchField<Masked<IPv6FlowLabel>>("ipv6_flabel", MatchFields.IPV6_FLOWLABEL);

    public String getName() {
        return name;
    }

}

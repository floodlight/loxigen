package org.openflow.protocol.match;

import org.openflow.types.ArpOpcode;
import org.openflow.types.EthType;
import org.openflow.types.ICMPv4Code;
import org.openflow.types.ICMPv4Type;
import org.openflow.types.IPv4WithMask;
import org.openflow.types.IPv6FlowLabel;
import org.openflow.types.IPv6WithMask;
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

public class MatchField<F extends OFValueType> {
    private final String name;
    public final int id;

    public MatchField(final String name) {
        this.name = name;
        this.id = -1;
    }

    public MatchField(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public final static MatchField<OFPort> IN_PORT =
            new MatchField<OFPort>("in_port", 0);
    public final static MatchField<OFPort> IN_PHY_PORT =
            new MatchField<OFPort>("in_phy_port");
    public final static MatchField<OFPort> METADATA =
            new MatchField<OFPort>("metadata");

    public final static MatchField<Masked<MacAddress>> ETH_DST =
            new MatchField<Masked<MacAddress>>("eth_dst");
    public final static MatchField<Masked<MacAddress>> ETH_SRC =
            new MatchField<Masked<MacAddress>>("eth_src", 1);

    public final static MatchField<EthType> ETH_TYPE =
            new MatchField<EthType>("eth_type");
    
    public final static MatchField<Masked<VlanVid>> VLAN_VID =
            new MatchField<Masked<VlanVid>>("vlan_vid");
    public final static MatchField<VlanPcp> VLAN_PCP =
            new MatchField<VlanPcp>("vlan_pcp");
    
    
    public final static MatchField<IpDscp> IP_DSCP =
            new MatchField<IpDscp>("ip_dscp");
    public final static MatchField<IpEcn> IP_ECN =
            new MatchField<IpEcn>("ip_dscp");
    public final static MatchField<IpProtocol> IP_PROTO =
            new MatchField<IpProtocol>("ip_proto");

    public final static MatchField<IPv4WithMask> IPV4_SRC =
            new MatchField<IPv4WithMask>("ipv4_src");
    public final static MatchField<IPv4WithMask> IPV4_DST =
            new MatchField<IPv4WithMask>("ipv4_dst");

    public final static MatchField<TransportPort> TCP_SRC = new MatchField<TransportPort>(
            "tcp_src");
    public final static MatchField<TransportPort> TCP_DST = new MatchField<TransportPort>(
            "tcp_dst");

    public final static MatchField<TransportPort> UDP_SRC = new MatchField<TransportPort>(
            "udp_src");
    public final static MatchField<TransportPort> UDP_DST = new MatchField<TransportPort>(
            "udp_dst");

    public final static MatchField<TransportPort> SCTP_SRC = new MatchField<TransportPort>(
            "sctp_src");
    public final static MatchField<TransportPort> SCTP_DST = new MatchField<TransportPort>(
            "sctp_dst");

    public final static MatchField<ICMPv4Type> ICMPV4_TYPE = new MatchField<ICMPv4Type>(
            "icmpv4_src");
    public final static MatchField<ICMPv4Code> ICMPV4_CODE = new MatchField<ICMPv4Code>(
            "icmpv4_dst");

    public final static MatchField<ArpOpcode> ARP_OP = new MatchField<ArpOpcode>(
            "arp_op");
    public final static MatchField<IPv4WithMask> ARP_SPA =
            new MatchField<IPv4WithMask>("arp_spa");
    public final static MatchField<IPv4WithMask> ARP_TPA =
            new MatchField<IPv4WithMask>("arp_tpa");
    public final static MatchField<Masked<MacAddress>> ARP_SHA =
            new MatchField<Masked<MacAddress>>("arp_sha");
    public final static MatchField<Masked<MacAddress>> ARP_THA =
            new MatchField<Masked<MacAddress>>("arp_tha");

    public final static MatchField<IPv6WithMask> IPV6_SRC =
            new MatchField<IPv6WithMask>("ipv6_src");
    public final static MatchField<IPv6WithMask> IPV6_DST =
            new MatchField<IPv6WithMask>("ipv6_dst");

    public final static MatchField<Masked<IPv6FlowLabel>> IPV6_FLABEL =
            new MatchField<Masked<IPv6FlowLabel>>("ipv6_flabel");

    public String getName() {
        return name;
    }

}

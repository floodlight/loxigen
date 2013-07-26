package org.openflow.protocol.match;

import org.openflow.types.EthType;
import org.openflow.types.IPv4;
import org.openflow.types.IPv6;
import org.openflow.types.IpProtocol;
import org.openflow.types.MacAddress;
import org.openflow.types.Masked;
import org.openflow.types.OFPort;
import org.openflow.types.U16;
import org.openflow.types.U8;
import org.openflow.types.VlanPcp;
import org.openflow.types.VlanVid;

public class MatchField<F, M> {
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

    public final static MatchField<OFPort, NoMatch> IN_PORT =
            new MatchField<OFPort, NoMatch>("in_port", 0);
    public final static MatchField<OFPort, NoMatch> IN_PHY_PORT =
            new MatchField<OFPort, NoMatch>("in_phy_port");
    public final static MatchField<OFPort, NoMatch> METADATA =
            new MatchField<OFPort, NoMatch>("metadata");

    public final static MatchField<MacAddress, Masked<MacAddress>> ETH_DST =
            new MatchField<MacAddress, Masked<MacAddress>>("eth_dst");
    public final static MatchField<MacAddress, Masked<MacAddress>> ETH_SRC =
            new MatchField<MacAddress, Masked<MacAddress>>("eth_src", 1);

    public final static MatchField<EthType, NoMatch> ETH_TYPE =
            new MatchField<EthType, NoMatch>("eth_type");
    
    public final static MatchField<VlanVid, Masked<VlanVid>> VLAN_VID =
            new MatchField<VlanVid, Masked<VlanVid>>("vlan_vid");
    public final static MatchField<VlanPcp, NoMatch> VLAN_PCP =
            new MatchField<VlanPcp, NoMatch>("vlan_pcp");
    
    
    public final static MatchField<NoMatch, NoMatch> IP_DSCP =
            new MatchField<NoMatch, NoMatch>("ip_dscp");
    public final static MatchField<NoMatch, NoMatch> IP_ECN =
            new MatchField<NoMatch, NoMatch>("ip_dscp");
    public final static MatchField<IpProtocol, NoMatch> IP_PROTO =
            new MatchField<IpProtocol, NoMatch>("ip_proto");

    public final static MatchField<IPv4, Masked<IPv4>> IPV4_SRC =
            new MatchField<IPv4, Masked<IPv4>>("ipv4_src");
    public final static MatchField<IPv4, Masked<IPv4>> IPV4_DST =
            new MatchField<IPv4, Masked<IPv4>>("ipv4_dst");

    public final static MatchField<U16, NoMatch> TCP_SRC = new MatchField<U16, NoMatch>(
            "tcp_src");
    public final static MatchField<U16, NoMatch> TCP_DST = new MatchField<U16, NoMatch>(
            "tcp_dst");

    public final static MatchField<U16, NoMatch> UDP_SRC = new MatchField<U16, NoMatch>(
            "udp_src");
    public final static MatchField<U16, NoMatch> UDP_DST = new MatchField<U16, NoMatch>(
            "udp_dst");

    public final static MatchField<U16, NoMatch> SCTP_SRC = new MatchField<U16, NoMatch>(
            "sctp_src");
    public final static MatchField<U16, NoMatch> SCTP_DST = new MatchField<U16, NoMatch>(
            "sctp_dst");

    public final static MatchField<U8, NoMatch> ICMPV4_TYPE = new MatchField<U8, NoMatch>(
            "icmpv4_src");
    public final static MatchField<U8, NoMatch> ICMPV4_CODE = new MatchField<U8, NoMatch>(
            "icmpv4_dst");

    public final static MatchField<U16, NoMatch> ARP_OP = new MatchField<U16, NoMatch>(
            "arp_op");
    public final static MatchField<IPv4, Masked<IPv4>> ARP_SPA =
            new MatchField<IPv4, Masked<IPv4>>("arp_spa");
    public final static MatchField<IPv4, Masked<IPv4>> ARP_TPA =
            new MatchField<IPv4, Masked<IPv4>>("arp_tpa");
    public final static MatchField<MacAddress, Masked<MacAddress>> ARP_SHA =
            new MatchField<MacAddress, Masked<MacAddress>>("arp_sha");
    public final static MatchField<MacAddress, Masked<MacAddress>> ARP_THA =
            new MatchField<MacAddress, Masked<MacAddress>>("arp_tha");

    public final static MatchField<IPv6, Masked<IPv6>> IPV6_SRC =
            new MatchField<IPv6, Masked<IPv6>>("ipv6_src");
    public final static MatchField<IPv6, Masked<IPv6>> IPV6_DST =
            new MatchField<IPv6, Masked<IPv6>>("ipv6_dst");

    // FIXME: Wrong masked type?
    public final static MatchField<U8, Masked<IPv6>> IPV6_FLABEL =
            new MatchField<U8, Masked<IPv6>>("ipv6_flabel");

    public String getName() {
        return name;
    }

}

package org.projectfloodlight.openflow.protocol.match;

import java.util.Set;

import org.projectfloodlight.openflow.types.ArpOpcode;
import org.projectfloodlight.openflow.types.ClassId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.ICMPv4Code;
import org.projectfloodlight.openflow.types.ICMPv4Type;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IPv6FlowLabel;
import org.projectfloodlight.openflow.types.IpDscp;
import org.projectfloodlight.openflow.types.IpEcn;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.LagId;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBitMask128;
import org.projectfloodlight.openflow.types.OFBitMask512;
import org.projectfloodlight.openflow.types.OFBooleanValue;
import org.projectfloodlight.openflow.types.OFMetadata;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFValueType;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.PacketType;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.U128;
import org.projectfloodlight.openflow.types.U16;
import org.projectfloodlight.openflow.types.U32;
import org.projectfloodlight.openflow.types.U64;
import org.projectfloodlight.openflow.types.U8;
import org.projectfloodlight.openflow.types.UDF;
import org.projectfloodlight.openflow.types.VFI;
import org.projectfloodlight.openflow.types.VRF;
import org.projectfloodlight.openflow.types.VlanPcp;
import org.projectfloodlight.openflow.types.VxlanNI;

import com.google.common.collect.ImmutableSet;

public class MatchField<F extends OFValueType<F>> {
    private final String name;
    public final MatchFields id;
    private final Set<Prerequisite<?>> prerequisites;

    private MatchField(final String name, final MatchFields id, Prerequisite<?>... prerequisites) {
        this.name = name;
        this.id = id;
        /* guaranteed non-null (private constructor); 'null' isn't passed as prerequisites */
        this.prerequisites = ImmutableSet.copyOf(prerequisites);
    }

    public final static MatchField<OFPort> IN_PORT =
            new MatchField<>("in_port", MatchFields.IN_PORT);

    public final static MatchField<OFPort> IN_PHY_PORT =
            new MatchField<>("in_phy_port", MatchFields.IN_PHY_PORT,
                    new Prerequisite<>(MatchField.IN_PORT));

    public final static MatchField<OFMetadata> METADATA =
            new MatchField<>("metadata", MatchFields.METADATA);

    public final static MatchField<MacAddress> ETH_DST =
            new MatchField<>("eth_dst", MatchFields.ETH_DST);

    public final static MatchField<MacAddress> ETH_SRC =
            new MatchField<>("eth_src", MatchFields.ETH_SRC);

    public final static MatchField<EthType> ETH_TYPE =
            new MatchField<>("eth_type", MatchFields.ETH_TYPE);

    public final static MatchField<OFVlanVidMatch> VLAN_VID =
            new MatchField<>("vlan_vid", MatchFields.VLAN_VID);

    public final static MatchField<VlanPcp> VLAN_PCP =
            new MatchField<>("vlan_pcp", MatchFields.VLAN_PCP,
                    new Prerequisite<>(MatchField.VLAN_VID));

    public final static MatchField<IpDscp> IP_DSCP =
            new MatchField<>("ip_dscp", MatchFields.IP_DSCP,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4, EthType.IPv6));

    public final static MatchField<IpEcn> IP_ECN =
            new MatchField<>("ip_ecn", MatchFields.IP_ECN,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4, EthType.IPv6));

    public final static MatchField<IpProtocol> IP_PROTO =
            new MatchField<>("ip_proto", MatchFields.IP_PROTO,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4, EthType.IPv6));

    public final static MatchField<IPv4Address> IPV4_SRC =
            new MatchField<>("ipv4_src", MatchFields.IPV4_SRC,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4));

    public final static MatchField<IPv4Address> IPV4_DST =
            new MatchField<>("ipv4_dst", MatchFields.IPV4_DST,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4));

    public final static MatchField<TransportPort> TCP_SRC = new MatchField<>(
            "tcp_src", MatchFields.TCP_SRC,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));

    public final static MatchField<TransportPort> TCP_DST = new MatchField<>(
            "tcp_dst", MatchFields.TCP_DST,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));

    public final static MatchField<TransportPort> UDP_SRC = new MatchField<>(
            "udp_src", MatchFields.UDP_SRC,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.UDP));

    public final static MatchField<TransportPort> UDP_DST = new MatchField<>(
            "udp_dst", MatchFields.UDP_DST,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.UDP));

    public final static MatchField<TransportPort> SCTP_SRC = new MatchField<>(
            "sctp_src", MatchFields.SCTP_SRC,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.SCTP));

    public final static MatchField<TransportPort> SCTP_DST = new MatchField<>(
            "sctp_dst", MatchFields.SCTP_DST,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.SCTP));

    public final static MatchField<ICMPv4Type> ICMPV4_TYPE = new MatchField<>(
            "icmpv4_type", MatchFields.ICMPV4_TYPE,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.ICMP));

    public final static MatchField<ICMPv4Code> ICMPV4_CODE = new MatchField<>(
            "icmpv4_code", MatchFields.ICMPV4_CODE,
            new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.ICMP));

    public final static MatchField<ArpOpcode> ARP_OP = new MatchField<>(
            "arp_op", MatchFields.ARP_OP,
            new Prerequisite<>(MatchField.ETH_TYPE, EthType.ARP));

    public final static MatchField<IPv4Address> ARP_SPA =
            new MatchField<>("arp_spa", MatchFields.ARP_SPA,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.ARP));

    public final static MatchField<IPv4Address> ARP_TPA =
            new MatchField<>("arp_tpa", MatchFields.ARP_TPA,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.ARP));

    public final static MatchField<MacAddress> ARP_SHA =
            new MatchField<>("arp_sha", MatchFields.ARP_SHA,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.ARP));

    public final static MatchField<MacAddress> ARP_THA =
            new MatchField<>("arp_tha", MatchFields.ARP_THA,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.ARP));

    public final static MatchField<IPv6Address> IPV6_SRC =
            new MatchField<>("ipv6_src", MatchFields.IPV6_SRC,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));

    public final static MatchField<IPv6Address> IPV6_DST =
            new MatchField<>("ipv6_dst", MatchFields.IPV6_DST,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));

    public final static MatchField<IPv6FlowLabel> IPV6_FLABEL =
            new MatchField<>("ipv6_flabel", MatchFields.IPV6_FLABEL,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));

    public final static MatchField<U8> ICMPV6_TYPE =
            new MatchField<>("icmpv6_type", MatchFields.ICMPV6_TYPE,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.IPv6_ICMP));

    public final static MatchField<U8> ICMPV6_CODE =
            new MatchField<>("icmpv6_code", MatchFields.ICMPV6_CODE,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.IPv6_ICMP));

    public final static MatchField<IPv6Address> IPV6_ND_TARGET =
            new MatchField<>("ipv6_nd_target", MatchFields.IPV6_ND_TARGET,
                    new Prerequisite<>(MatchField.ICMPV6_TYPE, U8.of((short) 135), U8.of((short) 136)));

    public final static MatchField<MacAddress> IPV6_ND_SLL =
            new MatchField<>("ipv6_nd_sll", MatchFields.IPV6_ND_SLL,
                    new Prerequisite<>(MatchField.ICMPV6_TYPE, U8.of((short) 135)));

    public final static MatchField<MacAddress> IPV6_ND_TLL =
            new MatchField<>("ipv6_nd_tll", MatchFields.IPV6_ND_TLL,
                    new Prerequisite<>(MatchField.ICMPV6_TYPE, U8.of((short) 136)));

    public final static MatchField<U32> MPLS_LABEL =
            new MatchField<>("mpls_label", MatchFields.MPLS_LABEL,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.MPLS_UNICAST, EthType.MPLS_MULTICAST));

    public final static MatchField<U8> MPLS_TC =
            new MatchField<>("mpls_tc", MatchFields.MPLS_TC,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.MPLS_UNICAST, EthType.MPLS_MULTICAST));

    public final static MatchField<OFBooleanValue> MPLS_BOS =
            new MatchField<>("mpls_bos", MatchFields.MPLS_BOS,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.MPLS_UNICAST, EthType.MPLS_MULTICAST));

    public final static MatchField<U64> TUNNEL_ID =
            new MatchField<>("tunnel_id", MatchFields.TUNNEL_ID);

    public final static MatchField<U16> IPV6_EXTHDR =
            new MatchField<>("ipv6_exthdr", MatchFields.IPV6_EXTHDR,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));

    public final static MatchField<OFBooleanValue> PBB_UCA =
            new MatchField<>("pbb_uca", MatchFields.PBB_UCA,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.PBB));

    public final static MatchField<U16> TCP_FLAGS =
            new MatchField<>("tcp_flags", MatchFields.TCP_FLAGS,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));

    public final static MatchField<U16> OVS_TCP_FLAGS =
            new MatchField<>("ovs_tcp_flags", MatchFields.OVS_TCP_FLAGS,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));

    public final static MatchField<PacketType> PACKET_TYPE =
            new MatchField<>("packet_type", MatchFields.PACKET_TYPE);

    public final static MatchField<OFPort> ACTSET_OUTPUT =
            new MatchField<>("actset_output", MatchFields.ACTSET_OUTPUT);

    public final static MatchField<IPv4Address> TUNNEL_IPV4_SRC =
            new MatchField<>("tunnel_ipv4_src", MatchFields.TUNNEL_IPV4_SRC,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4));

    public final static MatchField<IPv4Address> TUNNEL_IPV4_DST =
            new MatchField<>("tunnel_ipv4_dst", MatchFields.TUNNEL_IPV4_DST,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4));

    public final static MatchField<OFBitMask128> BSN_IN_PORTS_128 =
            new MatchField<>("bsn_in_ports_128", MatchFields.BSN_IN_PORTS_128);

    public final static MatchField<OFBitMask512> BSN_IN_PORTS_512 =
            new MatchField<>("bsn_in_ports_512", MatchFields.BSN_IN_PORTS_512);

    public final static MatchField<LagId> BSN_LAG_ID =
            new MatchField<>("bsn_lag_id", MatchFields.BSN_LAG_ID);

    public final static MatchField<VRF> BSN_VRF =
            new MatchField<>("bsn_vrf", MatchFields.BSN_VRF);

    public final static MatchField<OFBooleanValue> BSN_GLOBAL_VRF_ALLOWED =
            new MatchField<>("bsn_global_vrf_allowed", MatchFields.BSN_GLOBAL_VRF_ALLOWED);

    public final static MatchField<ClassId> BSN_L3_INTERFACE_CLASS_ID =
            new MatchField<>("bsn_l3_interface_class_id", MatchFields.BSN_L3_INTERFACE_CLASS_ID);

    public final static MatchField<ClassId> BSN_L3_SRC_CLASS_ID =
            new MatchField<>("bsn_l3_src_class_id", MatchFields.BSN_L3_SRC_CLASS_ID);

    public final static MatchField<ClassId> BSN_L3_DST_CLASS_ID =
            new MatchField<>("bsn_l3_dst_class_id", MatchFields.BSN_L3_DST_CLASS_ID);

    public final static MatchField<ClassId> BSN_EGR_PORT_GROUP_ID =
            new MatchField<>("bsn_egr_port_group_id", MatchFields.BSN_EGR_PORT_GROUP_ID);

    public final static MatchField<ClassId> BSN_INGRESS_PORT_GROUP_ID =
            new MatchField<>("bsn_ingress_port_group_id", MatchFields.BSN_INGRESS_PORT_GROUP_ID);

    public final static MatchField<UDF> BSN_UDF0 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF0);

    public final static MatchField<UDF> BSN_UDF1 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF1);

    public final static MatchField<UDF> BSN_UDF2 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF2);

    public final static MatchField<UDF> BSN_UDF3 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF3);

    public final static MatchField<UDF> BSN_UDF4 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF4);

    public final static MatchField<UDF> BSN_UDF5 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF5);

    public final static MatchField<UDF> BSN_UDF6 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF6);

    public final static MatchField<UDF> BSN_UDF7 =
            new MatchField<>("bsn_udf", MatchFields.BSN_UDF7);

    public final static MatchField<U16> BSN_TCP_FLAGS =
            new MatchField<>("bsn_tcp_flags", MatchFields.BSN_TCP_FLAGS);

    public final static MatchField<ClassId> BSN_VLAN_XLATE_PORT_GROUP_ID =
            new MatchField<>("bsn_vlan_xlate_port_group_id", MatchFields.BSN_VLAN_XLATE_PORT_GROUP_ID);

    public final static MatchField<OFBooleanValue> BSN_L2_CACHE_HIT =
            new MatchField<>("bsn_l2_cache_hit", MatchFields.BSN_L2_CACHE_HIT);

    public final static MatchField<VxlanNI> BSN_VXLAN_NETWORK_ID =
            new MatchField<>("bsn_vxlan_network_id", MatchFields.BSN_VXLAN_NETWORK_ID);

    public final static MatchField<MacAddress> BSN_INNER_ETH_DST =
            new MatchField<>("bsn_inner_eth_dst", MatchFields.BSN_INNER_ETH_DST);

    public final static MatchField<MacAddress> BSN_INNER_ETH_SRC =
            new MatchField<>("bsn_inner_eth_src", MatchFields.BSN_INNER_ETH_SRC);

    public final static MatchField<OFVlanVidMatch> BSN_INNER_VLAN_VID =
            new MatchField<>("bsn_inner_vlan_vid", MatchFields.BSN_INNER_VLAN_VID);

    public final static MatchField<VFI> BSN_VFI =
            new MatchField<>("bsn_vfi", MatchFields.BSN_VFI);

    public final static MatchField<OFBooleanValue> BSN_IP_FRAGMENTATION =
            new MatchField<>("bsn_ip_fragmentation", MatchFields.BSN_IP_FRAGMENTATION,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv4, EthType.IPv6));

    public final static MatchField<ClassId> BSN_IFP_CLASS_ID =
            new MatchField<>("bsn_ifp_class_id", MatchFields.BSN_IFP_CLASS_ID);
   
    public final static MatchField<U32> CONN_TRACKING_STATE =
            new MatchField<>("conn_tracking_state", MatchFields.CONN_TRACKING_STATE);

    public final static MatchField<U16> CONN_TRACKING_ZONE =
            new MatchField<>("conn_tracking_zone", MatchFields.CONN_TRACKING_ZONE);
   
    public final static MatchField<U32> CONN_TRACKING_MARK =
            new MatchField<>("conn_tracking_mark", MatchFields.CONN_TRACKING_MARK);

    public final static MatchField<U128> CONN_TRACKING_LABEL =
            new MatchField<>("conn_tracking_label", MatchFields.CONN_TRACKING_LABEL);
    
    public final static MatchField<U8> CONN_TRACKING_NW_PROTO =
            new MatchField<>("conn_tracking_nw_proto", MatchFields.CONN_TRACKING_NW_PROTO);

    public final static MatchField<U32> CONN_TRACKING_NW_SRC =
            new MatchField<>("conn_tracking_nw_src", MatchFields.CONN_TRACKING_NW_SRC);

    public final static MatchField<U32> CONN_TRACKING_NW_DST =
            new MatchField<>("conn_tracking_nw_dst", MatchFields.CONN_TRACKING_NW_DST);
    
    public final static MatchField<IPv6Address> CONN_TRACKING_IPV6_SRC =
            new MatchField<>("conn_tracking_ipv6_src", MatchFields.CONN_TRACKING_IPV6_SRC,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));
    
    public final static MatchField<IPv6Address> CONN_TRACKING_IPV6_DST =
            new MatchField<>("conn_tracking_ipv6_dst", MatchFields.CONN_TRACKING_IPV6_DST,
                    new Prerequisite<>(MatchField.ETH_TYPE, EthType.IPv6));
    
    public final static MatchField<TransportPort> CONN_TRACKING_TP_SRC =
            new MatchField<>("conn_tracking_tp_src", MatchFields.CONN_TRACKING_TP_SRC,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));
    
    public final static MatchField<TransportPort> CONN_TRACKING_TP_DST =
            new MatchField<>("conn_tracking_tp_dst", MatchFields.CONN_TRACKING_TP_DST,
                    new Prerequisite<>(MatchField.IP_PROTO, IpProtocol.TCP));


    public String getName() {
        return name;
    }

    public boolean arePrerequisitesOK(Match match) {
        for (Prerequisite<?> p : this.prerequisites) {
            if (!p.isSatisfied(match)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieve what also must be matched in order to
     * use this particular MatchField.
     *
     * @return unmodifiable view of the prerequisites
     */
    public Set<Prerequisite<?>> getPrerequisites() {
        /* assumes non-null; guaranteed by constructor */
        return this.prerequisites;
    }

}

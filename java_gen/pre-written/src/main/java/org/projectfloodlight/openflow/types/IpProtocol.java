package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.Shorts;

/**
 * IP-Protocol field representation
 *
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 */
public class IpProtocol implements OFValueType<IpProtocol> {

    static final short MAX_PROTO = 0xFF;
    static final int LENGTH = 1;

    private final short proto;

    static final short IP_PROTO_NUM_HOPOPT  = 0x00;
    static final short IP_PROTO_NUM_ICMP    = 0x01;
    static final short IP_PROTO_NUM_IGMP    = 0x02;
    static final short IP_PROTO_NUM_GGP = 0x03;
    static final short IP_PROTO_NUM_IPv4    = 0x04;
    static final short IP_PROTO_NUM_ST  = 0x05;
    static final short IP_PROTO_NUM_TCP = 0x06;
    static final short IP_PROTO_NUM_CBT = 0x07;
    static final short IP_PROTO_NUM_EGP = 0x08;
    static final short IP_PROTO_NUM_IGP = 0x09;
    static final short IP_PROTO_NUM_BBN_RCC_MON = 0x0A;
    static final short IP_PROTO_NUM_NVP_II  = 0x0B;
    static final short IP_PROTO_NUM_PUP = 0x0C;
    static final short IP_PROTO_NUM_ARGUS   = 0x0D;
    static final short IP_PROTO_NUM_EMCON   = 0x0E;
    static final short IP_PROTO_NUM_XNET    = 0x0F;
    static final short IP_PROTO_NUM_CHAOS   = 0x10;
    static final short IP_PROTO_NUM_UDP = 0x11;
    static final short IP_PROTO_NUM_MUX = 0x12;
    static final short IP_PROTO_NUM_DCN_MEAS    = 0x13;
    static final short IP_PROTO_NUM_HMP = 0x14;
    static final short IP_PROTO_NUM_PRM = 0x15;
    static final short IP_PROTO_NUM_XNS_IDP = 0x16;
    static final short IP_PROTO_NUM_TRUNK_1 = 0x17;
    static final short IP_PROTO_NUM_TRUNK_2 = 0x18;
    static final short IP_PROTO_NUM_LEAF_1  = 0x19;
    static final short IP_PROTO_NUM_LEAF_2  = 0x1A;
    static final short IP_PROTO_NUM_RDP = 0x1B;
    static final short IP_PROTO_NUM_IRTP    = 0x1C;
    static final short IP_PROTO_NUM_ISO_TP4 = 0x1D;
    static final short IP_PROTO_NUM_NETBLT  = 0x1E;
    static final short IP_PROTO_NUM_MFE_NSP = 0x1F;
    static final short IP_PROTO_NUM_MERIT_INP   = 0x20;
    static final short IP_PROTO_NUM_DCCP    = 0x21;
    static final short IP_PROTO_NUM_3PC = 0x22;
    static final short IP_PROTO_NUM_IDPR    = 0x23;
    static final short IP_PROTO_NUM_XTP = 0x24;
    static final short IP_PROTO_NUM_DDP = 0x25;
    static final short IP_PROTO_NUM_IDPR_CMTP   = 0x26;
    static final short IP_PROTO_NUM_TP_PP   = 0x27;
    static final short IP_PROTO_NUM_IL  = 0x28;
    static final short IP_PROTO_NUM_IPv6    = 0x29;
    static final short IP_PROTO_NUM_SDRP    = 0x2A;
    static final short IP_PROTO_NUM_IPv6_ROUTE  = 0x2B;
    static final short IP_PROTO_NUM_IPv6_FRAG   = 0x2C;
    static final short IP_PROTO_NUM_IDRP    = 0x2D;
    static final short IP_PROTO_NUM_RSVP    = 0x2E;
    static final short IP_PROTO_NUM_GRE = 0x2F;
    static final short IP_PROTO_NUM_MHRP    = 0x30;
    static final short IP_PROTO_NUM_BNA = 0x31;
    static final short IP_PROTO_NUM_ESP = 0x32;
    static final short IP_PROTO_NUM_AH  = 0x33;
    static final short IP_PROTO_NUM_I_NLSP  = 0x34;
    static final short IP_PROTO_NUM_SWIPE   = 0x35;
    static final short IP_PROTO_NUM_NARP    = 0x36;
    static final short IP_PROTO_NUM_MOBILE  = 0x37;
    static final short IP_PROTO_NUM_TLSP    = 0x38;
    static final short IP_PROTO_NUM_SKIP    = 0x39;
    static final short IP_PROTO_NUM_IPv6_ICMP   = 0x3A;
    static final short IP_PROTO_NUM_IPv6_NO_NXT = 0x3B;
    static final short IP_PROTO_NUM_IPv6_OPTS   = 0x3C;
    static final short IP_PROTO_NUM_HOST_INTERNAL   = 0x3D;
    static final short IP_PROTO_NUM_CFTP    = 0x3E;
    static final short IP_PROTO_NUM_LOCAL_NET   = 0x3F;
    static final short IP_PROTO_NUM_SAT_EXPAK   = 0x40;
    static final short IP_PROTO_NUM_KRYPTOLAN   = 0x41;
    static final short IP_PROTO_NUM_RVD = 0x42;
    static final short IP_PROTO_NUM_IPPC    = 0x43;
    static final short IP_PROTO_NUM_DIST_FS = 0x44;
    static final short IP_PROTO_NUM_SAT_MON = 0x45;
    static final short IP_PROTO_NUM_VISA    = 0x46;
    static final short IP_PROTO_NUM_IPCV    = 0x47;
    static final short IP_PROTO_NUM_CPNX    = 0x48;
    static final short IP_PROTO_NUM_CPHB    = 0x49;
    static final short IP_PROTO_NUM_WSN = 0x4A;
    static final short IP_PROTO_NUM_PVP = 0x4B;
    static final short IP_PROTO_NUM_BR_SAT_MON  = 0x4C;
    static final short IP_PROTO_NUM_SUN_ND  = 0x4D;
    static final short IP_PROTO_NUM_WB_MON  = 0x4E;
    static final short IP_PROTO_NUM_WB_EXPAK    = 0x4F;
    static final short IP_PROTO_NUM_ISO_IP  = 0x50;
    static final short IP_PROTO_NUM_VMTP    = 0x51;
    static final short IP_PROTO_NUM_SECURE_VMTP = 0x52;
    static final short IP_PROTO_NUM_VINES   = 0x53;
    static final short IP_PROTO_NUM_TTP_IPTM = 0x54;
    static final short IP_PROTO_NUM_NSFNET_IGP  = 0x55;
    static final short IP_PROTO_NUM_DGP = 0x56;
    static final short IP_PROTO_NUM_TCF = 0x57;
    static final short IP_PROTO_NUM_EIGRP   = 0x58;
    static final short IP_PROTO_NUM_OSPF    = 0x59;
    static final short IP_PROTO_NUM_Sprite_RPC  = 0x5A;
    static final short IP_PROTO_NUM_LARP    = 0x5B;
    static final short IP_PROTO_NUM_MTP = 0x5C;
    static final short IP_PROTO_NUM_AX_25   = 0x5D;
    static final short IP_PROTO_NUM_IPIP    = 0x5E;
    static final short IP_PROTO_NUM_MICP    = 0x5F;
    static final short IP_PROTO_NUM_SCC_SP  = 0x60;
    static final short IP_PROTO_NUM_ETHERIP = 0x61;
    static final short IP_PROTO_NUM_ENCAP   = 0x62;
    static final short IP_PROTO_NUM_PRIVATE_ENCRYPT = 0x63;
    static final short IP_PROTO_NUM_GMTP    = 0x64;
    static final short IP_PROTO_NUM_IFMP    = 0x65;
    static final short IP_PROTO_NUM_PNNI    = 0x66;
    static final short IP_PROTO_NUM_PIM = 0x67;
    static final short IP_PROTO_NUM_ARIS    = 0x68;
    static final short IP_PROTO_NUM_SCPS    = 0x69;
    static final short IP_PROTO_NUM_QNX = 0x6A;
    static final short IP_PROTO_NUM_A_N = 0x6B;
    static final short IP_PROTO_NUM_IP_COMP = 0x6C;
    static final short IP_PROTO_NUM_SNP = 0x6D;
    static final short IP_PROTO_NUM_COMPAQ_PEER = 0x6E;
    static final short IP_PROTO_NUM_IPX_IN_IP   = 0x6F;
    static final short IP_PROTO_NUM_VRRP    = 0x70;
    static final short IP_PROTO_NUM_PGM = 0x71;
    static final short IP_PROTO_NUM_ZERO_HOP    = 0x72;
    static final short IP_PROTO_NUM_L2TP    = 0x73;
    static final short IP_PROTO_NUM_DDX = 0x74;
    static final short IP_PROTO_NUM_IATP    = 0x75;
    static final short IP_PROTO_NUM_STP = 0x76;
    static final short IP_PROTO_NUM_SRP = 0x77;
    static final short IP_PROTO_NUM_UTI = 0x78;
    static final short IP_PROTO_NUM_SMP = 0x79;
    static final short IP_PROTO_NUM_SM  = 0x7A;
    static final short IP_PROTO_NUM_PTP = 0x7B;
    static final short IP_PROTO_NUM_IS_IS_OVER_IPv4 = 0x7C;
    static final short IP_PROTO_NUM_FIRE    = 0x7D;
    static final short IP_PROTO_NUM_CRTP    = 0x7E;
    static final short IP_PROTO_NUM_CRUDP   = 0x7F;
    static final short IP_PROTO_NUM_SSCOPMCE    = 0x80;
    static final short IP_PROTO_NUM_IPLT    = 0x81;
    static final short IP_PROTO_NUM_SPS = 0x82;
    static final short IP_PROTO_NUM_PIPE    = 0x83;
    static final short IP_PROTO_NUM_SCTP    = 0x84;
    static final short IP_PROTO_NUM_FC  = 0x85;
    static final short IP_PROTO_NUM_RSVP_E2E_IGNORE = 0x86;
    static final short IP_PROTO_NUM_MOBILITY_HEADER = 0x87;
    static final short IP_PROTO_NUM_UDP_LITE    = 0x88;
    static final short IP_PROTO_NUM_MPLS_IN_IP  = 0x89;
    static final short IP_PROTO_NUM_MANET   = 0x8A;
    static final short IP_PROTO_NUM_HIP = 0x8B;
    static final short IP_PROTO_NUM_SHIM6   = 0x8C;

    public static final IpProtocol IP_PROTO_HOPOPT = new IpProtocol(IP_PROTO_NUM_HOPOPT);
    public static final IpProtocol IP_PROTO_ICMP = new IpProtocol(IP_PROTO_NUM_ICMP);
    public static final IpProtocol IP_PROTO_IGMP = new IpProtocol(IP_PROTO_NUM_IGMP);
    public static final IpProtocol IP_PROTO_GGP = new IpProtocol(IP_PROTO_NUM_GGP);
    public static final IpProtocol IP_PROTO_IPv4 = new IpProtocol(IP_PROTO_NUM_IPv4);
    public static final IpProtocol IP_PROTO_ST = new IpProtocol(IP_PROTO_NUM_ST);
    public static final IpProtocol IP_PROTO_TCP = new IpProtocol(IP_PROTO_NUM_TCP);
    public static final IpProtocol IP_PROTO_CBT = new IpProtocol(IP_PROTO_NUM_CBT);
    public static final IpProtocol IP_PROTO_EGP = new IpProtocol(IP_PROTO_NUM_EGP);
    public static final IpProtocol IP_PROTO_IGP = new IpProtocol(IP_PROTO_NUM_IGP);
    public static final IpProtocol IP_PROTO_BBN_RCC_MON = new IpProtocol(IP_PROTO_NUM_BBN_RCC_MON);
    public static final IpProtocol IP_PROTO_NVP_II = new IpProtocol(IP_PROTO_NUM_NVP_II);
    public static final IpProtocol IP_PROTO_PUP = new IpProtocol(IP_PROTO_NUM_PUP);
    public static final IpProtocol IP_PROTO_ARGUS = new IpProtocol(IP_PROTO_NUM_ARGUS);
    public static final IpProtocol IP_PROTO_EMCON = new IpProtocol(IP_PROTO_NUM_EMCON);
    public static final IpProtocol IP_PROTO_XNET = new IpProtocol(IP_PROTO_NUM_XNET);
    public static final IpProtocol IP_PROTO_CHAOS = new IpProtocol(IP_PROTO_NUM_CHAOS);
    public static final IpProtocol IP_PROTO_UDP = new IpProtocol(IP_PROTO_NUM_UDP);
    public static final IpProtocol IP_PROTO_MUX = new IpProtocol(IP_PROTO_NUM_MUX);
    public static final IpProtocol IP_PROTO_DCN_MEAS = new IpProtocol(IP_PROTO_NUM_DCN_MEAS);
    public static final IpProtocol IP_PROTO_HMP = new IpProtocol(IP_PROTO_NUM_HMP);
    public static final IpProtocol IP_PROTO_PRM = new IpProtocol(IP_PROTO_NUM_PRM);
    public static final IpProtocol IP_PROTO_XNS_IDP = new IpProtocol(IP_PROTO_NUM_XNS_IDP);
    public static final IpProtocol IP_PROTO_TRUNK_1 = new IpProtocol(IP_PROTO_NUM_TRUNK_1);
    public static final IpProtocol IP_PROTO_TRUNK_2 = new IpProtocol(IP_PROTO_NUM_TRUNK_2);
    public static final IpProtocol IP_PROTO_LEAF_1 = new IpProtocol(IP_PROTO_NUM_LEAF_1);
    public static final IpProtocol IP_PROTO_LEAF_2 = new IpProtocol(IP_PROTO_NUM_LEAF_2);
    public static final IpProtocol IP_PROTO_RDP = new IpProtocol(IP_PROTO_NUM_RDP);
    public static final IpProtocol IP_PROTO_IRTP = new IpProtocol(IP_PROTO_NUM_IRTP);
    public static final IpProtocol IP_PROTO_ISO_TP4 = new IpProtocol(IP_PROTO_NUM_ISO_TP4);
    public static final IpProtocol IP_PROTO_NETBLT = new IpProtocol(IP_PROTO_NUM_NETBLT);
    public static final IpProtocol IP_PROTO_MFE_NSP = new IpProtocol(IP_PROTO_NUM_MFE_NSP);
    public static final IpProtocol IP_PROTO_MERIT_INP = new IpProtocol(IP_PROTO_NUM_MERIT_INP);
    public static final IpProtocol IP_PROTO_DCCP = new IpProtocol(IP_PROTO_NUM_DCCP);
    public static final IpProtocol IP_PROTO_3PC = new IpProtocol(IP_PROTO_NUM_3PC);
    public static final IpProtocol IP_PROTO_IDPR = new IpProtocol(IP_PROTO_NUM_IDPR);
    public static final IpProtocol IP_PROTO_XTP = new IpProtocol(IP_PROTO_NUM_XTP);
    public static final IpProtocol IP_PROTO_DDP = new IpProtocol(IP_PROTO_NUM_DDP);
    public static final IpProtocol IP_PROTO_IDPR_CMTP = new IpProtocol(IP_PROTO_NUM_IDPR_CMTP);
    public static final IpProtocol IP_PROTO_TP_PP = new IpProtocol(IP_PROTO_NUM_TP_PP);
    public static final IpProtocol IP_PROTO_IL = new IpProtocol(IP_PROTO_NUM_IL);
    public static final IpProtocol IP_PROTO_IPv6 = new IpProtocol(IP_PROTO_NUM_IPv6);
    public static final IpProtocol IP_PROTO_SDRP = new IpProtocol(IP_PROTO_NUM_SDRP);
    public static final IpProtocol IP_PROTO_IPv6_ROUTE = new IpProtocol(IP_PROTO_NUM_IPv6_ROUTE);
    public static final IpProtocol IP_PROTO_IPv6_FRAG = new IpProtocol(IP_PROTO_NUM_IPv6_FRAG);
    public static final IpProtocol IP_PROTO_IDRP = new IpProtocol(IP_PROTO_NUM_IDRP);
    public static final IpProtocol IP_PROTO_RSVP = new IpProtocol(IP_PROTO_NUM_RSVP);
    public static final IpProtocol IP_PROTO_GRE = new IpProtocol(IP_PROTO_NUM_GRE);
    public static final IpProtocol IP_PROTO_MHRP = new IpProtocol(IP_PROTO_NUM_MHRP);
    public static final IpProtocol IP_PROTO_BNA = new IpProtocol(IP_PROTO_NUM_BNA);
    public static final IpProtocol IP_PROTO_ESP = new IpProtocol(IP_PROTO_NUM_ESP);
    public static final IpProtocol IP_PROTO_AH = new IpProtocol(IP_PROTO_NUM_AH);
    public static final IpProtocol IP_PROTO_I_NLSP = new IpProtocol(IP_PROTO_NUM_I_NLSP);
    public static final IpProtocol IP_PROTO_SWIPE = new IpProtocol(IP_PROTO_NUM_SWIPE);
    public static final IpProtocol IP_PROTO_NARP = new IpProtocol(IP_PROTO_NUM_NARP);
    public static final IpProtocol IP_PROTO_MOBILE = new IpProtocol(IP_PROTO_NUM_MOBILE);
    public static final IpProtocol IP_PROTO_TLSP = new IpProtocol(IP_PROTO_NUM_TLSP);
    public static final IpProtocol IP_PROTO_SKIP = new IpProtocol(IP_PROTO_NUM_SKIP);
    public static final IpProtocol IP_PROTO_IPv6_ICMP = new IpProtocol(IP_PROTO_NUM_IPv6_ICMP);
    public static final IpProtocol IP_PROTO_IPv6_NO_NXT = new IpProtocol(IP_PROTO_NUM_IPv6_NO_NXT);
    public static final IpProtocol IP_PROTO_IPv6_OPTS = new IpProtocol(IP_PROTO_NUM_IPv6_OPTS);
    public static final IpProtocol IP_PROTO_HOST_INTERNAL = new IpProtocol(IP_PROTO_NUM_HOST_INTERNAL);
    public static final IpProtocol IP_PROTO_CFTP = new IpProtocol(IP_PROTO_NUM_CFTP);
    public static final IpProtocol IP_PROTO_LOCAL_NET = new IpProtocol(IP_PROTO_NUM_LOCAL_NET);
    public static final IpProtocol IP_PROTO_SAT_EXPAK = new IpProtocol(IP_PROTO_NUM_SAT_EXPAK);
    public static final IpProtocol IP_PROTO_KRYPTOLAN = new IpProtocol(IP_PROTO_NUM_KRYPTOLAN);
    public static final IpProtocol IP_PROTO_RVD = new IpProtocol(IP_PROTO_NUM_RVD);
    public static final IpProtocol IP_PROTO_IPPC = new IpProtocol(IP_PROTO_NUM_IPPC);
    public static final IpProtocol IP_PROTO_DIST_FS = new IpProtocol(IP_PROTO_NUM_DIST_FS);
    public static final IpProtocol IP_PROTO_SAT_MON = new IpProtocol(IP_PROTO_NUM_SAT_MON);
    public static final IpProtocol IP_PROTO_VISA = new IpProtocol(IP_PROTO_NUM_VISA);
    public static final IpProtocol IP_PROTO_IPCV = new IpProtocol(IP_PROTO_NUM_IPCV);
    public static final IpProtocol IP_PROTO_CPNX = new IpProtocol(IP_PROTO_NUM_CPNX);
    public static final IpProtocol IP_PROTO_CPHB = new IpProtocol(IP_PROTO_NUM_CPHB);
    public static final IpProtocol IP_PROTO_WSN = new IpProtocol(IP_PROTO_NUM_WSN);
    public static final IpProtocol IP_PROTO_PVP = new IpProtocol(IP_PROTO_NUM_PVP);
    public static final IpProtocol IP_PROTO_BR_SAT_MON = new IpProtocol(IP_PROTO_NUM_BR_SAT_MON);
    public static final IpProtocol IP_PROTO_SUN_ND = new IpProtocol(IP_PROTO_NUM_SUN_ND);
    public static final IpProtocol IP_PROTO_WB_MON = new IpProtocol(IP_PROTO_NUM_WB_MON);
    public static final IpProtocol IP_PROTO_WB_EXPAK = new IpProtocol(IP_PROTO_NUM_WB_EXPAK);
    public static final IpProtocol IP_PROTO_ISO_IP = new IpProtocol(IP_PROTO_NUM_ISO_IP);
    public static final IpProtocol IP_PROTO_VMTP = new IpProtocol(IP_PROTO_NUM_VMTP);
    public static final IpProtocol IP_PROTO_SECURE_VMTP = new IpProtocol(IP_PROTO_NUM_SECURE_VMTP);
    public static final IpProtocol IP_PROTO_VINES = new IpProtocol(IP_PROTO_NUM_VINES);
    public static final IpProtocol IP_PROTO_TTP_IPTM = new IpProtocol(IP_PROTO_NUM_TTP_IPTM);
    public static final IpProtocol IP_PROTO_NSFNET_IGP = new IpProtocol(IP_PROTO_NUM_NSFNET_IGP);
    public static final IpProtocol IP_PROTO_DGP = new IpProtocol(IP_PROTO_NUM_DGP);
    public static final IpProtocol IP_PROTO_TCF = new IpProtocol(IP_PROTO_NUM_TCF);
    public static final IpProtocol IP_PROTO_EIGRP = new IpProtocol(IP_PROTO_NUM_EIGRP);
    public static final IpProtocol IP_PROTO_OSPF = new IpProtocol(IP_PROTO_NUM_OSPF);
    public static final IpProtocol IP_PROTO_Sprite_RPC = new IpProtocol(IP_PROTO_NUM_Sprite_RPC);
    public static final IpProtocol IP_PROTO_LARP = new IpProtocol(IP_PROTO_NUM_LARP);
    public static final IpProtocol IP_PROTO_MTP = new IpProtocol(IP_PROTO_NUM_MTP);
    public static final IpProtocol IP_PROTO_AX_25 = new IpProtocol(IP_PROTO_NUM_AX_25);
    public static final IpProtocol IP_PROTO_IPIP = new IpProtocol(IP_PROTO_NUM_IPIP);
    public static final IpProtocol IP_PROTO_MICP = new IpProtocol(IP_PROTO_NUM_MICP);
    public static final IpProtocol IP_PROTO_SCC_SP = new IpProtocol(IP_PROTO_NUM_SCC_SP);
    public static final IpProtocol IP_PROTO_ETHERIP = new IpProtocol(IP_PROTO_NUM_ETHERIP);
    public static final IpProtocol IP_PROTO_ENCAP = new IpProtocol(IP_PROTO_NUM_ENCAP);
    public static final IpProtocol IP_PROTO_PRIVATE_ENCRYPT = new IpProtocol(IP_PROTO_NUM_PRIVATE_ENCRYPT);
    public static final IpProtocol IP_PROTO_GMTP = new IpProtocol(IP_PROTO_NUM_GMTP);
    public static final IpProtocol IP_PROTO_IFMP = new IpProtocol(IP_PROTO_NUM_IFMP);
    public static final IpProtocol IP_PROTO_PNNI = new IpProtocol(IP_PROTO_NUM_PNNI);
    public static final IpProtocol IP_PROTO_PIM = new IpProtocol(IP_PROTO_NUM_PIM);
    public static final IpProtocol IP_PROTO_ARIS = new IpProtocol(IP_PROTO_NUM_ARIS);
    public static final IpProtocol IP_PROTO_SCPS = new IpProtocol(IP_PROTO_NUM_SCPS);
    public static final IpProtocol IP_PROTO_QNX = new IpProtocol(IP_PROTO_NUM_QNX);
    public static final IpProtocol IP_PROTO_A_N = new IpProtocol(IP_PROTO_NUM_A_N);
    public static final IpProtocol IP_PROTO_IP_COMP = new IpProtocol(IP_PROTO_NUM_IP_COMP);
    public static final IpProtocol IP_PROTO_SNP = new IpProtocol(IP_PROTO_NUM_SNP);
    public static final IpProtocol IP_PROTO_COMPAQ_PEER = new IpProtocol(IP_PROTO_NUM_COMPAQ_PEER);
    public static final IpProtocol IP_PROTO_IPX_IN_IP = new IpProtocol(IP_PROTO_NUM_IPX_IN_IP);
    public static final IpProtocol IP_PROTO_VRRP = new IpProtocol(IP_PROTO_NUM_VRRP);
    public static final IpProtocol IP_PROTO_PGM = new IpProtocol(IP_PROTO_NUM_PGM);
    public static final IpProtocol IP_PROTO_ZERO_HOP = new IpProtocol(IP_PROTO_NUM_ZERO_HOP);
    public static final IpProtocol IP_PROTO_L2TP = new IpProtocol(IP_PROTO_NUM_L2TP);
    public static final IpProtocol IP_PROTO_DDX = new IpProtocol(IP_PROTO_NUM_DDX);
    public static final IpProtocol IP_PROTO_IATP = new IpProtocol(IP_PROTO_NUM_IATP);
    public static final IpProtocol IP_PROTO_STP = new IpProtocol(IP_PROTO_NUM_STP);
    public static final IpProtocol IP_PROTO_SRP = new IpProtocol(IP_PROTO_NUM_SRP);
    public static final IpProtocol IP_PROTO_UTI = new IpProtocol(IP_PROTO_NUM_UTI);
    public static final IpProtocol IP_PROTO_SMP = new IpProtocol(IP_PROTO_NUM_SMP);
    public static final IpProtocol IP_PROTO_SM = new IpProtocol(IP_PROTO_NUM_SM);
    public static final IpProtocol IP_PROTO_PTP = new IpProtocol(IP_PROTO_NUM_PTP);
    public static final IpProtocol IP_PROTO_IS_IS_OVER_IPv4 = new IpProtocol(IP_PROTO_NUM_IS_IS_OVER_IPv4);
    public static final IpProtocol IP_PROTO_FIRE = new IpProtocol(IP_PROTO_NUM_FIRE);
    public static final IpProtocol IP_PROTO_CRTP = new IpProtocol(IP_PROTO_NUM_CRTP);
    public static final IpProtocol IP_PROTO_CRUDP = new IpProtocol(IP_PROTO_NUM_CRUDP);
    public static final IpProtocol IP_PROTO_SSCOPMCE = new IpProtocol(IP_PROTO_NUM_SSCOPMCE);
    public static final IpProtocol IP_PROTO_IPLT = new IpProtocol(IP_PROTO_NUM_IPLT);
    public static final IpProtocol IP_PROTO_SPS = new IpProtocol(IP_PROTO_NUM_SPS);
    public static final IpProtocol IP_PROTO_PIPE = new IpProtocol(IP_PROTO_NUM_PIPE);
    public static final IpProtocol IP_PROTO_SCTP = new IpProtocol(IP_PROTO_NUM_SCTP);
    public static final IpProtocol IP_PROTO_FC = new IpProtocol(IP_PROTO_NUM_FC);
    public static final IpProtocol IP_PROTO_RSVP_E2E_IGNORE = new IpProtocol(IP_PROTO_NUM_RSVP_E2E_IGNORE);
    public static final IpProtocol IP_PROTO_MOBILITY_HEADER = new IpProtocol(IP_PROTO_NUM_MOBILITY_HEADER);
    public static final IpProtocol IP_PROTO_UDP_LITE = new IpProtocol(IP_PROTO_NUM_UDP_LITE);
    public static final IpProtocol IP_PROTO_MPLS_IN_IP = new IpProtocol(IP_PROTO_NUM_MPLS_IN_IP);
    public static final IpProtocol IP_PROTO_MANET = new IpProtocol(IP_PROTO_NUM_MANET);
    public static final IpProtocol IP_PROTO_HIP = new IpProtocol(IP_PROTO_NUM_HIP);
    public static final IpProtocol IP_PROTO_SHIM6 = new IpProtocol(IP_PROTO_NUM_SHIM6);

    public static final IpProtocol NONE = IP_PROTO_HOPOPT;

    public static final IpProtocol NO_MASK = IP_PROTO_HOPOPT;
    public static final IpProtocol FULL_MASK = new IpProtocol((short)0x0000);

    private IpProtocol(short version) {
        this.proto = version;
    }


    @Override
    public int getLength() {
        return LENGTH;
    }

    public static IpProtocol of(short proto) {
        switch (proto) {
            case IP_PROTO_NUM_HOPOPT:
                return IP_PROTO_HOPOPT;
            case IP_PROTO_NUM_ICMP:
                return IP_PROTO_ICMP;
            case IP_PROTO_NUM_IGMP:
                return IP_PROTO_IGMP;
            case IP_PROTO_NUM_GGP:
                return IP_PROTO_GGP;
            case IP_PROTO_NUM_IPv4:
                return IP_PROTO_IPv4;
            case IP_PROTO_NUM_ST:
                return IP_PROTO_ST;
            case IP_PROTO_NUM_TCP:
                return IP_PROTO_TCP;
            case IP_PROTO_NUM_CBT:
                return IP_PROTO_CBT;
            case IP_PROTO_NUM_EGP:
                return IP_PROTO_EGP;
            case IP_PROTO_NUM_IGP:
                return IP_PROTO_IGP;
            case IP_PROTO_NUM_BBN_RCC_MON:
                return IP_PROTO_BBN_RCC_MON;
            case IP_PROTO_NUM_NVP_II:
                return IP_PROTO_NVP_II;
            case IP_PROTO_NUM_PUP:
                return IP_PROTO_PUP;
            case IP_PROTO_NUM_ARGUS:
                return IP_PROTO_ARGUS;
            case IP_PROTO_NUM_EMCON:
                return IP_PROTO_EMCON;
            case IP_PROTO_NUM_XNET:
                return IP_PROTO_XNET;
            case IP_PROTO_NUM_CHAOS:
                return IP_PROTO_CHAOS;
            case IP_PROTO_NUM_UDP:
                return IP_PROTO_UDP;
            case IP_PROTO_NUM_MUX:
                return IP_PROTO_MUX;
            case IP_PROTO_NUM_DCN_MEAS:
                return IP_PROTO_DCN_MEAS;
            case IP_PROTO_NUM_HMP:
                return IP_PROTO_HMP;
            case IP_PROTO_NUM_PRM:
                return IP_PROTO_PRM;
            case IP_PROTO_NUM_XNS_IDP:
                return IP_PROTO_XNS_IDP;
            case IP_PROTO_NUM_TRUNK_1:
                return IP_PROTO_TRUNK_1;
            case IP_PROTO_NUM_TRUNK_2:
                return IP_PROTO_TRUNK_2;
            case IP_PROTO_NUM_LEAF_1:
                return IP_PROTO_LEAF_1;
            case IP_PROTO_NUM_LEAF_2:
                return IP_PROTO_LEAF_2;
            case IP_PROTO_NUM_RDP:
                return IP_PROTO_RDP;
            case IP_PROTO_NUM_IRTP:
                return IP_PROTO_IRTP;
            case IP_PROTO_NUM_ISO_TP4:
                return IP_PROTO_ISO_TP4;
            case IP_PROTO_NUM_NETBLT:
                return IP_PROTO_NETBLT;
            case IP_PROTO_NUM_MFE_NSP:
                return IP_PROTO_MFE_NSP;
            case IP_PROTO_NUM_MERIT_INP:
                return IP_PROTO_MERIT_INP;
            case IP_PROTO_NUM_DCCP:
                return IP_PROTO_DCCP;
            case IP_PROTO_NUM_3PC:
                return IP_PROTO_3PC;
            case IP_PROTO_NUM_IDPR:
                return IP_PROTO_IDPR;
            case IP_PROTO_NUM_XTP:
                return IP_PROTO_XTP;
            case IP_PROTO_NUM_DDP:
                return IP_PROTO_DDP;
            case IP_PROTO_NUM_IDPR_CMTP:
                return IP_PROTO_IDPR_CMTP;
            case IP_PROTO_NUM_TP_PP:
                return IP_PROTO_TP_PP;
            case IP_PROTO_NUM_IL:
                return IP_PROTO_IL;
            case IP_PROTO_NUM_IPv6:
                return IP_PROTO_IPv6;
            case IP_PROTO_NUM_SDRP:
                return IP_PROTO_SDRP;
            case IP_PROTO_NUM_IPv6_ROUTE:
                return IP_PROTO_IPv6_ROUTE;
            case IP_PROTO_NUM_IPv6_FRAG:
                return IP_PROTO_IPv6_FRAG;
            case IP_PROTO_NUM_IDRP:
                return IP_PROTO_IDRP;
            case IP_PROTO_NUM_RSVP:
                return IP_PROTO_RSVP;
            case IP_PROTO_NUM_GRE:
                return IP_PROTO_GRE;
            case IP_PROTO_NUM_MHRP:
                return IP_PROTO_MHRP;
            case IP_PROTO_NUM_BNA:
                return IP_PROTO_BNA;
            case IP_PROTO_NUM_ESP:
                return IP_PROTO_ESP;
            case IP_PROTO_NUM_AH:
                return IP_PROTO_AH;
            case IP_PROTO_NUM_I_NLSP:
                return IP_PROTO_I_NLSP;
            case IP_PROTO_NUM_SWIPE:
                return IP_PROTO_SWIPE;
            case IP_PROTO_NUM_NARP:
                return IP_PROTO_NARP;
            case IP_PROTO_NUM_MOBILE:
                return IP_PROTO_MOBILE;
            case IP_PROTO_NUM_TLSP:
                return IP_PROTO_TLSP;
            case IP_PROTO_NUM_SKIP:
                return IP_PROTO_SKIP;
            case IP_PROTO_NUM_IPv6_ICMP:
                return IP_PROTO_IPv6_ICMP;
            case IP_PROTO_NUM_IPv6_NO_NXT:
                return IP_PROTO_IPv6_NO_NXT;
            case IP_PROTO_NUM_IPv6_OPTS:
                return IP_PROTO_IPv6_OPTS;
            case IP_PROTO_NUM_HOST_INTERNAL:
                return IP_PROTO_HOST_INTERNAL;
            case IP_PROTO_NUM_CFTP:
                return IP_PROTO_CFTP;
            case IP_PROTO_NUM_LOCAL_NET:
                return IP_PROTO_LOCAL_NET;
            case IP_PROTO_NUM_SAT_EXPAK:
                return IP_PROTO_SAT_EXPAK;
            case IP_PROTO_NUM_KRYPTOLAN:
                return IP_PROTO_KRYPTOLAN;
            case IP_PROTO_NUM_RVD:
                return IP_PROTO_RVD;
            case IP_PROTO_NUM_IPPC:
                return IP_PROTO_IPPC;
            case IP_PROTO_NUM_DIST_FS:
                return IP_PROTO_DIST_FS;
            case IP_PROTO_NUM_SAT_MON:
                return IP_PROTO_SAT_MON;
            case IP_PROTO_NUM_VISA:
                return IP_PROTO_VISA;
            case IP_PROTO_NUM_IPCV:
                return IP_PROTO_IPCV;
            case IP_PROTO_NUM_CPNX:
                return IP_PROTO_CPNX;
            case IP_PROTO_NUM_CPHB:
                return IP_PROTO_CPHB;
            case IP_PROTO_NUM_WSN:
                return IP_PROTO_WSN;
            case IP_PROTO_NUM_PVP:
                return IP_PROTO_PVP;
            case IP_PROTO_NUM_BR_SAT_MON:
                return IP_PROTO_BR_SAT_MON;
            case IP_PROTO_NUM_SUN_ND:
                return IP_PROTO_SUN_ND;
            case IP_PROTO_NUM_WB_MON:
                return IP_PROTO_WB_MON;
            case IP_PROTO_NUM_WB_EXPAK:
                return IP_PROTO_WB_EXPAK;
            case IP_PROTO_NUM_ISO_IP:
                return IP_PROTO_ISO_IP;
            case IP_PROTO_NUM_VMTP:
                return IP_PROTO_VMTP;
            case IP_PROTO_NUM_SECURE_VMTP:
                return IP_PROTO_SECURE_VMTP;
            case IP_PROTO_NUM_VINES:
                return IP_PROTO_VINES;
            case IP_PROTO_NUM_TTP_IPTM:
                return IP_PROTO_TTP_IPTM;
            case IP_PROTO_NUM_NSFNET_IGP:
                return IP_PROTO_NSFNET_IGP;
            case IP_PROTO_NUM_DGP:
                return IP_PROTO_DGP;
            case IP_PROTO_NUM_TCF:
                return IP_PROTO_TCF;
            case IP_PROTO_NUM_EIGRP:
                return IP_PROTO_EIGRP;
            case IP_PROTO_NUM_OSPF:
                return IP_PROTO_OSPF;
            case IP_PROTO_NUM_Sprite_RPC:
                return IP_PROTO_Sprite_RPC;
            case IP_PROTO_NUM_LARP:
                return IP_PROTO_LARP;
            case IP_PROTO_NUM_MTP:
                return IP_PROTO_MTP;
            case IP_PROTO_NUM_AX_25:
                return IP_PROTO_AX_25;
            case IP_PROTO_NUM_IPIP:
                return IP_PROTO_IPIP;
            case IP_PROTO_NUM_MICP:
                return IP_PROTO_MICP;
            case IP_PROTO_NUM_SCC_SP:
                return IP_PROTO_SCC_SP;
            case IP_PROTO_NUM_ETHERIP:
                return IP_PROTO_ETHERIP;
            case IP_PROTO_NUM_ENCAP:
                return IP_PROTO_ENCAP;
            case IP_PROTO_NUM_PRIVATE_ENCRYPT:
                return IP_PROTO_PRIVATE_ENCRYPT;
            case IP_PROTO_NUM_GMTP:
                return IP_PROTO_GMTP;
            case IP_PROTO_NUM_IFMP:
                return IP_PROTO_IFMP;
            case IP_PROTO_NUM_PNNI:
                return IP_PROTO_PNNI;
            case IP_PROTO_NUM_PIM:
                return IP_PROTO_PIM;
            case IP_PROTO_NUM_ARIS:
                return IP_PROTO_ARIS;
            case IP_PROTO_NUM_SCPS:
                return IP_PROTO_SCPS;
            case IP_PROTO_NUM_QNX:
                return IP_PROTO_QNX;
            case IP_PROTO_NUM_A_N:
                return IP_PROTO_A_N;
            case IP_PROTO_NUM_IP_COMP:
                return IP_PROTO_IP_COMP;
            case IP_PROTO_NUM_SNP:
                return IP_PROTO_SNP;
            case IP_PROTO_NUM_COMPAQ_PEER:
                return IP_PROTO_COMPAQ_PEER;
            case IP_PROTO_NUM_IPX_IN_IP:
                return IP_PROTO_IPX_IN_IP;
            case IP_PROTO_NUM_VRRP:
                return IP_PROTO_VRRP;
            case IP_PROTO_NUM_PGM:
                return IP_PROTO_PGM;
            case IP_PROTO_NUM_ZERO_HOP:
                return IP_PROTO_ZERO_HOP;
            case IP_PROTO_NUM_L2TP:
                return IP_PROTO_L2TP;
            case IP_PROTO_NUM_DDX:
                return IP_PROTO_DDX;
            case IP_PROTO_NUM_IATP:
                return IP_PROTO_IATP;
            case IP_PROTO_NUM_STP:
                return IP_PROTO_STP;
            case IP_PROTO_NUM_SRP:
                return IP_PROTO_SRP;
            case IP_PROTO_NUM_UTI:
                return IP_PROTO_UTI;
            case IP_PROTO_NUM_SMP:
                return IP_PROTO_SMP;
            case IP_PROTO_NUM_SM:
                return IP_PROTO_SM;
            case IP_PROTO_NUM_PTP:
                return IP_PROTO_PTP;
            case IP_PROTO_NUM_IS_IS_OVER_IPv4:
                return IP_PROTO_IS_IS_OVER_IPv4;
            case IP_PROTO_NUM_FIRE:
                return IP_PROTO_FIRE;
            case IP_PROTO_NUM_CRTP:
                return IP_PROTO_CRTP;
            case IP_PROTO_NUM_CRUDP:
                return IP_PROTO_CRUDP;
            case IP_PROTO_NUM_SSCOPMCE:
                return IP_PROTO_SSCOPMCE;
            case IP_PROTO_NUM_IPLT:
                return IP_PROTO_IPLT;
            case IP_PROTO_NUM_SPS:
                return IP_PROTO_SPS;
            case IP_PROTO_NUM_PIPE:
                return IP_PROTO_PIPE;
            case IP_PROTO_NUM_SCTP:
                return IP_PROTO_SCTP;
            case IP_PROTO_NUM_FC:
                return IP_PROTO_FC;
            case IP_PROTO_NUM_RSVP_E2E_IGNORE:
                return IP_PROTO_RSVP_E2E_IGNORE;
            case IP_PROTO_NUM_MOBILITY_HEADER:
                return IP_PROTO_MOBILITY_HEADER;
            case IP_PROTO_NUM_UDP_LITE:
                return IP_PROTO_UDP_LITE;
            case IP_PROTO_NUM_MPLS_IN_IP:
                return IP_PROTO_MPLS_IN_IP;
            case IP_PROTO_NUM_MANET:
                return IP_PROTO_MANET;
            case IP_PROTO_NUM_HIP:
                return IP_PROTO_HIP;
            case IP_PROTO_NUM_SHIM6:
                return IP_PROTO_SHIM6;
            default:
                if (proto >= MAX_PROTO) {
                    throw new IllegalArgumentException("Illegal IP protocol number: "
                            + proto);
                } else {
                    return new IpProtocol(proto);
                }
        }
    }

    @Override
    public String toString() {
        return Integer.toHexString(proto);
    }

    public void writeByte(ChannelBuffer c) {
        c.writeByte(this.proto);
    }

    public static IpProtocol readByte(ChannelBuffer c) {
        return IpProtocol.of(c.readUnsignedByte());
    }

    @Override
    public IpProtocol applyMask(IpProtocol mask) {
        return IpProtocol.of((short)(this.proto & mask.proto));
    }

    public short getIpProtocolNumber() {
        return proto;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IpProtocol))
            return false;
        IpProtocol o = (IpProtocol)obj;
        if (o.proto != this.proto)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + proto;
        return result;
    }


    @Override
    public int compareTo(IpProtocol o) {
        return Shorts.compare(proto, o.proto);
    }


    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putShort(proto);
    }

}
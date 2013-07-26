package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;

/**
 * EtherType field representation.
 * 
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 */
public class EthType implements OFValueType {
    static final int LENGTH = 2;

    private final int rawValue;

    static final int ETH_TYPE_VAL_IPv4              = 0x0800; // Internet Protocol version 4 (IPv4)
    static final int ETH_TYPE_VAL_ARP               = 0x0806; // Address Resolution Protocol (ARP)
    static final int ETH_TYPE_VAL_WAKE_ON_LAN       = 0x0842; // Wake-on-LAN[3]
    static final int ETH_TYPE_VAL_TRILL             = 0x22F3; // IETF TRILL Protocol
    static final int ETH_TYPE_VAL_DECNET_IV         = 0x6003; // DECnet Phase IV
    static final int ETH_TYPE_VAL_REV_ARP           = 0x8035; // Reverse Address Resolution Protocol
    static final int ETH_TYPE_VAL_APPLE_TALK        = 0x809B; // AppleTalk (Ethertalk)
    static final int ETH_TYPE_VAL_APPLE_TALK_ARP    = 0x80F3; // AppleTalk Address Resolution Protocol (AARP)
    static final int ETH_TYPE_VAL_VLAN_FRAME        = 0x8100; // VLAN-tagged frame (IEEE 802.1Q) & Shortest Path Bridging IEEE 802.1aq[4]
    static final int ETH_TYPE_VAL_IPX_8137          = 0x8137; // IPX
    static final int ETH_TYPE_VAL_IPX_8138          = 0x8138; // IPX
    static final int ETH_TYPE_VAL_QNX               = 0x8204; // QNX Qnet
    static final int ETH_TYPE_VAL_IPv6              = 0x86DD; // Internet Protocol Version 6 (IPv6)
    static final int ETH_TYPE_VAL_ETH_FLOW          = 0x8808; // Ethernet flow control
    static final int ETH_TYPE_VAL_SLOW_PROTOCOLS    = 0x8809; // Slow Protocols (IEEE 802.3)
    static final int ETH_TYPE_VAL_COBRANET          = 0x8819; // CobraNet
    static final int ETH_TYPE_VAL_MPLS_UNICAST      = 0x8847; // MPLS unicast
    static final int ETH_TYPE_VAL_MPLS_MULTICAST    = 0x8848; // MPLS multicast
    static final int ETH_TYPE_VAL_PPPoE_DISCOVERY   = 0x8863; // PPPoE Discovery Stage
    static final int ETH_TYPE_VAL_PPPoE_SESSION     = 0x8864; // PPPoE Session Stage
    static final int ETH_TYPE_VAL_JUMBO_FRAMES      = 0x8870; // Jumbo Frames
    static final int ETH_TYPE_VAL_HOMEPLUG_10       = 0x887B; // HomePlug 1.0 MME
    static final int ETH_TYPE_VAL_EAP_OVER_LAN      = 0x888E; // EAP over LAN (IEEE 802.1X)
    static final int ETH_TYPE_VAL_PROFINET          = 0x8892; // PROFINET Protocol
    static final int ETH_TYPE_VAL_HYPERSCSI         = 0x889A; // HyperSCSI (SCSI over Ethernet)
    static final int ETH_TYPE_VAL_ATA_OVER_ETH      = 0x88A2; // ATA over Ethernet
    static final int ETH_TYPE_VAL_ETHERCAT          = 0x88A4; // EtherCAT Protocol
    static final int ETH_TYPE_VAL_BRIDGING          = 0x88A8; // Provider Bridging (IEEE 802.1ad) & Shortest Path Bridging IEEE 802.1aq[5]
    static final int ETH_TYPE_VAL_POWERLINK         = 0x88AB; // Ethernet Powerlink[citation needed]
    static final int ETH_TYPE_VAL_LLDP              = 0x88CC; // Link Layer Discovery Protocol (LLDP)
    static final int ETH_TYPE_VAL_SERCOS            = 0x88CD; // SERCOS III
    static final int ETH_TYPE_VAL_HOMEPLUG_AV       = 0x88E1; // HomePlug AV MME[citation needed]
    static final int ETH_TYPE_VAL_MRP               = 0x88E3; // Media Redundancy Protocol (IEC62439-2)
    static final int ETH_TYPE_VAL_MAC_SEC           = 0x88E5; // MAC security (IEEE 802.1AE)
    static final int ETH_TYPE_VAL_PTP               = 0x88F7; // Precision Time Protocol (IEEE 1588)
    static final int ETH_TYPE_VAL_CFM               = 0x8902; // IEEE 802.1ag Connectivity Fault Management (CFM) Protocol / ITU-T Recommendation Y.1731 (OAM)
    static final int ETH_TYPE_VAL_FCoE              = 0x8906; // Fibre Channel over Ethernet (FCoE)
    static final int ETH_TYPE_VAL_FCoE_INIT         = 0x8914; // FCoE Initialization Protocol
    static final int ETH_TYPE_VAL_RoCE              = 0x8915; // RDMA over Converged Ethernet (RoCE)
    static final int ETH_TYPE_VAL_HSR               = 0x892F; // High-availability Seamless Redundancy (HSR)
    static final int ETH_TYPE_VAL_CONF_TEST         = 0x9000; // Ethernet Configuration Testing Protocol[6]
    static final int ETH_TYPE_VAL_Q_IN_Q            = 0x9100; // Q-in-Q
    static final int ETH_TYPE_VAL_LLT               = 0xCAFE; // Veritas Low Latency Transport (LLT)[7] for Veritas Cluster Server

    public static final EthType ETH_TYPE_IPv4               = new EthType(ETH_TYPE_VAL_IPv4);
    public static final EthType ETH_TYPE_ARP                = new EthType(ETH_TYPE_VAL_ARP);
    public static final EthType ETH_TYPE_WAKE_ON_LAN        = new EthType(ETH_TYPE_VAL_WAKE_ON_LAN);
    public static final EthType ETH_TYPE_TRILL              = new EthType(ETH_TYPE_VAL_TRILL);
    public static final EthType ETH_TYPE_DECNET_IV          = new EthType(ETH_TYPE_VAL_DECNET_IV); 
    public static final EthType ETH_TYPE_REV_ARP            = new EthType(ETH_TYPE_VAL_REV_ARP );
    public static final EthType ETH_TYPE_APPLE_TALK         = new EthType(ETH_TYPE_VAL_APPLE_TALK); 
    public static final EthType ETH_TYPE_APPLE_TALK_ARP     = new EthType(ETH_TYPE_VAL_APPLE_TALK_ARP); 
    public static final EthType ETH_TYPE_VLAN_FRAME         = new EthType(ETH_TYPE_VAL_VLAN_FRAME );
    public static final EthType ETH_TYPE_IPX_8137           = new EthType(ETH_TYPE_VAL_IPX_8137 );
    public static final EthType ETH_TYPE_IPX_8138           = new EthType(ETH_TYPE_VAL_IPX_8138 );
    public static final EthType ETH_TYPE_QNX                = new EthType(ETH_TYPE_VAL_QNX );
    public static final EthType ETH_TYPE_IPv6               = new EthType(ETH_TYPE_VAL_IPv6 );
    public static final EthType ETH_TYPE_ETH_FLOW           = new EthType(ETH_TYPE_VAL_ETH_FLOW); 
    public static final EthType ETH_TYPE_SLOW_PROTOCOLS     = new EthType(ETH_TYPE_VAL_SLOW_PROTOCOLS );
    public static final EthType ETH_TYPE_COBRANET           = new EthType(ETH_TYPE_VAL_COBRANET );
    public static final EthType ETH_TYPE_MPLS_UNICAST       = new EthType(ETH_TYPE_VAL_MPLS_UNICAST );
    public static final EthType ETH_TYPE_MPLS_MULTICAST     = new EthType(ETH_TYPE_VAL_MPLS_MULTICAST );
    public static final EthType ETH_TYPE_PPPoE_DISCOVERY    = new EthType(ETH_TYPE_VAL_PPPoE_DISCOVERY);
    public static final EthType ETH_TYPE_PPPoE_SESSION      = new EthType(ETH_TYPE_VAL_PPPoE_SESSION );
    public static final EthType ETH_TYPE_JUMBO_FRAMES       = new EthType(ETH_TYPE_VAL_JUMBO_FRAMES );
    public static final EthType ETH_TYPE_HOMEPLUG_10        = new EthType(ETH_TYPE_VAL_HOMEPLUG_10 );
    public static final EthType ETH_TYPE_EAP_OVER_LAN       = new EthType(ETH_TYPE_VAL_EAP_OVER_LAN );
    public static final EthType ETH_TYPE_PROFINET           = new EthType(ETH_TYPE_VAL_PROFINET );
    public static final EthType ETH_TYPE_HYPERSCSI          = new EthType(ETH_TYPE_VAL_HYPERSCSI );
    public static final EthType ETH_TYPE_ATA_OVER_ETH       = new EthType(ETH_TYPE_VAL_ATA_OVER_ETH); 
    public static final EthType ETH_TYPE_ETHERCAT           = new EthType(ETH_TYPE_VAL_ETHERCAT );
    public static final EthType ETH_TYPE_BRIDGING           = new EthType(ETH_TYPE_VAL_BRIDGING );
    public static final EthType ETH_TYPE_POWERLINK          = new EthType(ETH_TYPE_VAL_POWERLINK );
    public static final EthType ETH_TYPE_LLDP               = new EthType(ETH_TYPE_VAL_LLDP );
    public static final EthType ETH_TYPE_SERCOS             = new EthType(ETH_TYPE_VAL_SERCOS );
    public static final EthType ETH_TYPE_HOMEPLUG_AV        = new EthType(ETH_TYPE_VAL_HOMEPLUG_AV );
    public static final EthType ETH_TYPE_MRP                = new EthType(ETH_TYPE_VAL_MRP );
    public static final EthType ETH_TYPE_MAC_SEC            = new EthType(ETH_TYPE_VAL_MAC_SEC); 
    public static final EthType ETH_TYPE_PTP                = new EthType(ETH_TYPE_VAL_PTP );
    public static final EthType ETH_TYPE_CFM                = new EthType(ETH_TYPE_VAL_CFM );
    public static final EthType ETH_TYPE_FCoE               = new EthType(ETH_TYPE_VAL_FCoE );
    public static final EthType ETH_TYPE_FCoE_INIT          = new EthType(ETH_TYPE_VAL_FCoE_INIT );
    public static final EthType ETH_TYPE_RoCE               = new EthType(ETH_TYPE_VAL_RoCE );
    public static final EthType ETH_TYPE_HSR                = new EthType(ETH_TYPE_VAL_HSR );
    public static final EthType ETH_TYPE_CONF_TEST          = new EthType(ETH_TYPE_VAL_CONF_TEST );
    public static final EthType ETH_TYPE_Q_IN_Q             = new EthType(ETH_TYPE_VAL_Q_IN_Q );
    public static final EthType ETH_TYPE_LLT                = new EthType(ETH_TYPE_VAL_LLT );

    private EthType(int type) {
        this.rawValue = type;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    volatile byte[] bytesCache = null;

    @Override
    public byte[] getBytes() {
        if (bytesCache == null) {
            synchronized (this) {
                if (bytesCache == null) {
                    bytesCache = new byte[] {
                                             (byte) ((rawValue >>> 8) & 0xFF),
                                             (byte) ((rawValue >>> 0) & 0xFF)
                    };
                }
            }
        }
        return bytesCache;
    }

    public static EthType of(int type) {
        switch (type) {
            case ETH_TYPE_VAL_IPv4:
                return ETH_TYPE_IPv4;
            case ETH_TYPE_VAL_ARP:
                return ETH_TYPE_ARP;
            case ETH_TYPE_VAL_WAKE_ON_LAN:
                return ETH_TYPE_WAKE_ON_LAN;
            case ETH_TYPE_VAL_TRILL:
                return ETH_TYPE_TRILL;
            case ETH_TYPE_VAL_DECNET_IV:
                return ETH_TYPE_DECNET_IV;
            case ETH_TYPE_VAL_REV_ARP:
                return ETH_TYPE_REV_ARP;
            case ETH_TYPE_VAL_APPLE_TALK:
                return ETH_TYPE_APPLE_TALK;
            case ETH_TYPE_VAL_APPLE_TALK_ARP:
                return ETH_TYPE_APPLE_TALK_ARP;
            case ETH_TYPE_VAL_VLAN_FRAME:
                return ETH_TYPE_VLAN_FRAME;
            case ETH_TYPE_VAL_IPX_8137:
                return ETH_TYPE_IPX_8137;
            case ETH_TYPE_VAL_IPX_8138:
                return ETH_TYPE_IPX_8138;
            case ETH_TYPE_VAL_QNX:
                return ETH_TYPE_QNX;
            case ETH_TYPE_VAL_IPv6:
                return ETH_TYPE_IPv6;
            case ETH_TYPE_VAL_ETH_FLOW:
                return ETH_TYPE_ETH_FLOW;
            case ETH_TYPE_VAL_SLOW_PROTOCOLS:
                return ETH_TYPE_SLOW_PROTOCOLS;
            case ETH_TYPE_VAL_COBRANET:
                return ETH_TYPE_COBRANET;
            case ETH_TYPE_VAL_MPLS_UNICAST:
                return ETH_TYPE_MPLS_UNICAST;
            case ETH_TYPE_VAL_MPLS_MULTICAST:
                return ETH_TYPE_MPLS_MULTICAST;
            case ETH_TYPE_VAL_PPPoE_DISCOVERY:
                return ETH_TYPE_PPPoE_DISCOVERY;
            case ETH_TYPE_VAL_PPPoE_SESSION:
                return ETH_TYPE_PPPoE_SESSION;
            case ETH_TYPE_VAL_JUMBO_FRAMES:
                return ETH_TYPE_JUMBO_FRAMES;
            case ETH_TYPE_VAL_HOMEPLUG_10:
                return ETH_TYPE_HOMEPLUG_10;
            case ETH_TYPE_VAL_EAP_OVER_LAN:
                return ETH_TYPE_EAP_OVER_LAN;
            case ETH_TYPE_VAL_PROFINET:
                return ETH_TYPE_PROFINET;
            case ETH_TYPE_VAL_HYPERSCSI:
                return ETH_TYPE_HYPERSCSI;
            case ETH_TYPE_VAL_ATA_OVER_ETH:
                return ETH_TYPE_ATA_OVER_ETH;
            case ETH_TYPE_VAL_ETHERCAT:
                return ETH_TYPE_ETHERCAT;
            case ETH_TYPE_VAL_BRIDGING:
                return ETH_TYPE_BRIDGING;
            case ETH_TYPE_VAL_POWERLINK:
                return ETH_TYPE_POWERLINK;
            case ETH_TYPE_VAL_LLDP:
                return ETH_TYPE_LLDP;
            case ETH_TYPE_VAL_SERCOS:
                return ETH_TYPE_SERCOS;
            case ETH_TYPE_VAL_HOMEPLUG_AV:
                return ETH_TYPE_HOMEPLUG_AV;
            case ETH_TYPE_VAL_MRP:
                return ETH_TYPE_MRP;
            case ETH_TYPE_VAL_MAC_SEC:
                return ETH_TYPE_MAC_SEC;
            case ETH_TYPE_VAL_PTP:
                return ETH_TYPE_PTP;
            case ETH_TYPE_VAL_CFM:
                return ETH_TYPE_CFM;
            case ETH_TYPE_VAL_FCoE:
                return ETH_TYPE_FCoE;
            case ETH_TYPE_VAL_FCoE_INIT:
                return ETH_TYPE_FCoE_INIT;
            case ETH_TYPE_VAL_RoCE:
                return ETH_TYPE_RoCE;
            case ETH_TYPE_VAL_HSR:
                return ETH_TYPE_HSR;
            case ETH_TYPE_VAL_CONF_TEST:
                return ETH_TYPE_CONF_TEST;
            case ETH_TYPE_VAL_Q_IN_Q:
                return ETH_TYPE_Q_IN_Q;
            case ETH_TYPE_VAL_LLT:
                return ETH_TYPE_LLT;
            default:
                // TODO: What's here?
                return new EthType(type);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EthType))
            return false;
        EthType o = (EthType)obj;
        if (o.rawValue != this.rawValue)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + rawValue;
        return result;
    }

    @Override
    public String toString() {
        return Integer.toHexString(rawValue);
    }

    public static final Serializer<EthType> SERIALIZER_V10 = new SerializerV10();
    public static final Serializer<EthType> SERIALIZER_V11 = SERIALIZER_V10;
    public static final Serializer<EthType> SERIALIZER_V12 = SERIALIZER_V10;
    public static final Serializer<EthType> SERIALIZER_V13 = SERIALIZER_V10;

    private static class SerializerV10 implements OFValueType.Serializer<EthType> {

        @Override
        public void writeTo(EthType value, ChannelBuffer c) {
            c.writeShort(value.rawValue);
        }

        @Override
        public EthType readFrom(ChannelBuffer c) throws OFParseError {
            return EthType.of(c.readUnsignedShort());
        }

    }

}

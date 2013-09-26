    final public static int OFPFW_ALL = ((1 << 22) - 1);

    final public static int OFPFW_IN_PORT = 1 << 0; /* Switch input port. */
    final public static int OFPFW_DL_VLAN = 1 << 1; /* VLAN id. */
    final public static int OFPFW_DL_SRC = 1 << 2; /* Ethernet source address. */
    final public static int OFPFW_DL_DST = 1 << 3; /*
                                                    * Ethernet destination
                                                    * address.
                                                    */
    final public static int OFPFW_DL_TYPE = 1 << 4; /* Ethernet frame type. */
    final public static int OFPFW_NW_PROTO = 1 << 5; /* IP protocol. */
    final public static int OFPFW_TP_SRC = 1 << 6; /* TCP/UDP source port. */
    final public static int OFPFW_TP_DST = 1 << 7; /* TCP/UDP destination port. */

    /*
     * IP source address wildcard bit count. 0 is exact match, 1 ignores the
     * LSB, 2 ignores the 2 least-significant bits, ..., 32 and higher wildcard
     * the entire field. This is the *opposite* of the usual convention where
     * e.g. /24 indicates that 8 bits (not 24 bits) are wildcarded.
     */
    final public static int OFPFW_NW_SRC_SHIFT = 8;
    final public static int OFPFW_NW_SRC_BITS = 6;
    final public static int OFPFW_NW_SRC_MASK = ((1 << OFPFW_NW_SRC_BITS) - 1) << OFPFW_NW_SRC_SHIFT;
    final public static int OFPFW_NW_SRC_ALL = 32 << OFPFW_NW_SRC_SHIFT;

    /* IP destination address wildcard bit count. Same format as source. */
    final public static int OFPFW_NW_DST_SHIFT = 14;
    final public static int OFPFW_NW_DST_BITS = 6;
    final public static int OFPFW_NW_DST_MASK = ((1 << OFPFW_NW_DST_BITS) - 1) << OFPFW_NW_DST_SHIFT;
    final public static int OFPFW_NW_DST_ALL = 32 << OFPFW_NW_DST_SHIFT;

    final public static int OFPFW_DL_VLAN_PCP = 1 << 20; /* VLAN priority. */
    final public static int OFPFW_NW_TOS = 1 << 21; /* IP ToS (DSCP field, 6bits) */

    @SuppressWarnings("unchecked")
    @Override
    public <F extends OFValueType<F>> F get(MatchField<F> field)
            throws UnsupportedOperationException {
        if (isFullyWildcarded(field))
            return null;
        if (!field.arePrerequisitesOK(this))
            return null;

        Object result;
        switch (field.id) {
            case IN_PORT:
                result = inPort;
                break;
            case ETH_DST:
                result = ethDst;
                break;
            case ETH_SRC:
                result = ethSrc;
                break;
            case ETH_TYPE:
                result = ethType;
                break;
            case VLAN_VID:
                result = vlanVid;
                break;
            case VLAN_PCP:
                result = vlanPcp;
                break;
            case IP_DSCP:
                result = ipDscp;
                break;
            case IP_PROTO:
                result = ipProto;
                break;
            case IPV4_SRC:
                result = ipv4Dst;
                break;
            case IPV4_DST:
                result = ipv4Dst;
                break;
            case TCP_SRC:
                result = ipv4Src;
                break;
            case TCP_DST:
                result = tcpDst;
                break;
            case UDP_SRC:
                result = tcpSrc;
                break;
            case UDP_DST:
                result = tcpDst;
                break;
            case SCTP_SRC:
                result = tcpSrc;
                break;
            case SCTP_DST:
                result = tcpDst;
                break;
            case ICMPV4_TYPE:
                result = tcpSrc;
                break;
            case ICMPV4_CODE:
                result = tcpDst;
                break;
            // NOT SUPPORTED:
            default:
                throw new UnsupportedOperationException("OFMatch does not support matching on field " + field.getName());
        }
        return (F)result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field)
            throws UnsupportedOperationException {
        if (!isPartiallyMasked(field))
            return null;
        if (!field.arePrerequisitesOK(this))
            return null;
        Object result;
        switch (field.id) {
            case IPV4_SRC:
                int srcBitMask = (-1) << (32 - getIpv4SrcCidrMaskLen());
                result = IPv4AddressWithMask.of(ipv4Src, IPv4Address.of(srcBitMask));
                break;
            case IPV4_DST:
                int dstBitMask = (-1) << (32 - getIpv4DstCidrMaskLen());

                result = IPv4AddressWithMask.of(ipv4Dst, IPv4Address.of(dstBitMask));
                break;
            default:
                throw new UnsupportedOperationException("OFMatch does not support masked matching on field " + field.getName());
        }
        return (Masked<F>)result;
    }

    @Override
    public boolean supports(MatchField<?> field) {
        switch (field.id) {
            case IN_PORT:
            case ETH_DST:
            case ETH_SRC:
            case ETH_TYPE:
            case VLAN_VID:
            case VLAN_PCP:
            case IP_DSCP:
            case IP_PROTO:
            case IPV4_SRC:
            case IPV4_DST:
            case TCP_SRC:
            case TCP_DST:
            case UDP_SRC:
            case UDP_DST:
            case SCTP_SRC:
            case SCTP_DST:
            case ICMPV4_TYPE:
            case ICMPV4_CODE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean supportsMasked(MatchField<?> field) {
        switch (field.id) {
            case IPV4_SRC:
            case IPV4_DST:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isExact(MatchField<?> field) {
        if (!field.arePrerequisitesOK(this))
            return false;

        switch (field.id) {
            case IN_PORT:
                return (this.wildcards & OFPFW_IN_PORT) == 0;
            case ETH_DST:
                return (this.wildcards & OFPFW_DL_DST) == 0;
            case ETH_SRC:
                return (this.wildcards & OFPFW_DL_SRC) == 0;
            case ETH_TYPE:
                return (this.wildcards & OFPFW_DL_TYPE) == 0;
            case VLAN_VID:
                return (this.wildcards & OFPFW_DL_VLAN) == 0;
            case VLAN_PCP:
                return (this.wildcards & OFPFW_DL_VLAN_PCP) == 0;
            case IP_DSCP:
                return (this.wildcards & OFPFW_NW_TOS) == 0;
            case IP_PROTO:
                return (this.wildcards & OFPFW_NW_PROTO) == 0;
            case IPV4_SRC:
                return this.getIpv4SrcCidrMaskLen() >= 32;
            case IPV4_DST:
                return this.getIpv4DstCidrMaskLen() >= 32;
            case TCP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) == 0;
            case TCP_DST:
                return (this.wildcards & OFPFW_TP_DST) == 0;
            case UDP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) == 0;
            case UDP_DST:
                return (this.wildcards & OFPFW_TP_DST) == 0;
            case SCTP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) == 0;
            case SCTP_DST:
                return (this.wildcards & OFPFW_TP_DST) == 0;
            case ICMPV4_TYPE:
                return (this.wildcards & OFPFW_TP_SRC) == 0;
            case ICMPV4_CODE:
                return (this.wildcards & OFPFW_TP_DST) == 0;
            default:
                throw new UnsupportedOperationException("OFMatch does not support matching on field " + field.getName());
        }
    }

    /**
     * Parse this match's wildcard fields and return the number of significant
     * bits in the IP destination field. NOTE: this returns the number of bits
     * that are fixed, i.e., like CIDR, not the number of bits that are free
     * like OpenFlow encodes.
     *
     * @return A number between 0 (matches all IPs) and 32 (exact match)
     */
    public int getIpv4DstCidrMaskLen() {
        return Math.max(32 - ((wildcards & OFPFW_NW_DST_MASK) >> OFPFW_NW_DST_SHIFT),
                        0);
    }

    /**
     * Parse this match's wildcard fields and return the number of significant
     * bits in the IP destination field. NOTE: this returns the number of bits
     * that are fixed, i.e., like CIDR, not the number of bits that are free
     * like OpenFlow encodes.
     *
     * @return A number between 0 (matches all IPs) and 32 (exact match)
     */
    public int getIpv4SrcCidrMaskLen() {
        return Math.max(32 - ((wildcards & OFPFW_NW_SRC_MASK) >> OFPFW_NW_SRC_SHIFT),
                        0);
    }


    @Override
    public boolean isFullyWildcarded(MatchField<?> field) {
        if (!field.arePrerequisitesOK(this))
            return true;

        switch (field.id) {
            case IN_PORT:
                return (this.wildcards & OFPFW_IN_PORT) != 0;
            case ETH_DST:
                return (this.wildcards & OFPFW_DL_DST) != 0;
            case ETH_SRC:
                return (this.wildcards & OFPFW_DL_SRC) != 0;
            case ETH_TYPE:
                return (this.wildcards & OFPFW_DL_TYPE) != 0;
            case VLAN_VID:
                return (this.wildcards & OFPFW_DL_VLAN) != 0;
            case VLAN_PCP:
                return (this.wildcards & OFPFW_DL_VLAN_PCP) != 0;
            case IP_DSCP:
                return (this.wildcards & OFPFW_NW_TOS) != 0;
            case IP_PROTO:
                return (this.wildcards & OFPFW_NW_PROTO) != 0;
            case TCP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) != 0;
            case TCP_DST:
                return (this.wildcards & OFPFW_TP_DST) != 0;
            case UDP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) != 0;
            case UDP_DST:
                return (this.wildcards & OFPFW_TP_DST) != 0;
            case SCTP_SRC:
                return (this.wildcards & OFPFW_TP_SRC) != 0;
            case SCTP_DST:
                return (this.wildcards & OFPFW_TP_DST) != 0;
            case ICMPV4_TYPE:
                return (this.wildcards & OFPFW_TP_SRC) != 0;
            case ICMPV4_CODE:
                return (this.wildcards & OFPFW_TP_DST) != 0;
            case IPV4_SRC:
                return this.getIpv4SrcCidrMaskLen() <= 0;
            case IPV4_DST:
                return this.getIpv4DstCidrMaskLen() <= 0;
            default:
                throw new UnsupportedOperationException("OFMatch does not support matching on field " + field.getName());
        }
    }

    @Override
    public boolean isPartiallyMasked(MatchField<?> field) {
        if (!field.arePrerequisitesOK(this))
            return false;

        switch (field.id) {
            case IPV4_SRC:
                int srcCidrLen = getIpv4SrcCidrMaskLen();
                return srcCidrLen > 0 && srcCidrLen < 32;
            case IPV4_DST:
                int dstCidrLen = getIpv4SrcCidrMaskLen();
                return dstCidrLen > 0 && dstCidrLen < 32;
            default:
                throw new UnsupportedOperationException("OFMatch does not support masked matching on field " + field.getName());
        }
    }

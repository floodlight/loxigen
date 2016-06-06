        @SuppressWarnings("unchecked")
        @Override
        public <F extends OFValueType<F>> F get(MatchField<F> field)
                throws UnsupportedOperationException {
            if (isFullyWildcarded(field))
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
                case ARP_OP:
                    result = ArpOpcode.of(ipProto.getIpProtocolNumber());
                    break;
                case ARP_SPA:
                    result = ipv4Src;
                    break;
                case ARP_TPA:
                    result = ipv4Dst;
                    break;
                case IP_DSCP:
                    result = ipDscp;
                    break;
                case IP_PROTO:
                    result = ipProto;
                    break;
                case IPV4_SRC:
                    result = ipv4Src;
                    break;
                case IPV4_DST:
                    result = ipv4Dst;
                    break;
                case TCP_SRC:
                    result = tcpSrc;
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
                    result = ICMPv4Type.of((short) tcpSrc.getPort());
                    break;
               case ICMPV4_CODE:
                    result = ICMPv4Code.of((short) tcpDst.getPort());
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
            Object result;
            switch (field.id) {
                case IPV4_SRC:
                case ARP_SPA:
                    int srcBitMask = (-1) << (32 - getIpv4SrcCidrMaskLen());
                    result = IPv4AddressWithMask.of(ipv4Src, IPv4Address.of(srcBitMask));
                    break;
                case IPV4_DST:
                case ARP_TPA:
                    int dstMaskedBits = Math.min(32, (wildcards & OFPFW_NW_DST_MASK) >> OFPFW_NW_DST_SHIFT);
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
                case ARP_OP:
                case ARP_SPA:
                case ARP_TPA:
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
                case ARP_SPA:
                case ARP_TPA:
                case IPV4_SRC:
                case IPV4_DST:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean isExact(MatchField<?> field) {
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
                case ARP_OP:
                    return (this.wildcards & OFPFW_NW_PROTO) == 0;
                case ARP_SPA:
                    return this.getIpv4SrcCidrMaskLen() >= 32;
                case ARP_TPA:
                    return this.getIpv4DstCidrMaskLen() >= 32;
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
                case ARP_OP:
                    return (this.wildcards & OFPFW_NW_PROTO) != 0;
                case ARP_SPA:
                    return this.getIpv4SrcCidrMaskLen() <= 0;
                case ARP_TPA:
                    return this.getIpv4DstCidrMaskLen() <= 0;
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
            switch (field.id) {
                case ARP_SPA:
                case IPV4_SRC:
                    int srcCidrLen = getIpv4SrcCidrMaskLen();
                    return srcCidrLen > 0 && srcCidrLen < 32;
                case ARP_TPA:
                case IPV4_DST:
                    int dstCidrLen = getIpv4DstCidrMaskLen();
                    return dstCidrLen > 0 && dstCidrLen < 32;
                default:
                    throw new UnsupportedOperationException("OFMatch does not support masked matching on field " + field.getName());
            }
        }

        private final void initWildcards() {
            if(!wildcardsSet) {
            //:: if has_parent:
                wildcards = parentMessage.wildcards;
            //:: else:
                wildcards = OFPFW_ALL;
            //:: #endif
                wildcardsSet = true;
            }
        }

        @Override
        public <F extends OFValueType<F>> Match.Builder setExact(MatchField<F> field,
                F value) {
            initWildcards();
            Object val = value;
            switch (field.id) {
                case ETH_DST:
                    setEthDst((MacAddress) value);
                    wildcards &= ~OFPFW_DL_DST;
                    break;
                case ETH_SRC:
                    setEthSrc((MacAddress) value);
                    wildcards &= ~OFPFW_DL_SRC;
                    break;
                case ETH_TYPE:
                    setEthType((EthType) value);
                    wildcards &= ~OFPFW_DL_TYPE;
                    break;
                case ICMPV4_CODE:
                    setTcpDst(TransportPort.of(((ICMPv4Code)value).getCode()));
                    wildcards &= ~OFPFW_TP_DST;
                    break;
                case ICMPV4_TYPE:
                    setTcpSrc(TransportPort.of(((ICMPv4Type)value).getType()));
                    wildcards &= ~OFPFW_TP_SRC;
                    break;
                case IN_PORT:
                    setInPort((OFPort) value);
                    wildcards &= ~OFPFW_IN_PORT;
                    break;
                case ARP_OP:
                    setIpProto(IpProtocol.of((short)((ArpOpcode)value).getOpcode()));
                    wildcards &= ~OFPFW_NW_PROTO;
                    break;
                case ARP_TPA:
                case IPV4_DST:
                    setIpv4Dst((IPv4Address) value);
                    wildcards &= ~OFPFW_NW_DST_MASK;
                    break;
                case ARP_SPA:
                case IPV4_SRC:
                    setIpv4Src((IPv4Address) value);
                    wildcards &= ~OFPFW_NW_SRC_MASK;
                    break;
                case IP_DSCP:
                    setIpDscp((IpDscp) value);
                    wildcards &= ~OFPFW_NW_TOS;
                    break;
                case IP_PROTO:
                    setIpProto((IpProtocol) value);
                    wildcards &= ~OFPFW_NW_PROTO;
                    break;
                case SCTP_DST:
                    setTcpDst((TransportPort) value);
                    wildcards &= ~OFPFW_TP_DST;
                    break;
                case SCTP_SRC:
                    setTcpSrc((TransportPort) value);
                    wildcards &= ~OFPFW_TP_SRC;
                    break;
                case TCP_DST:
                    setTcpDst((TransportPort) value);
                    wildcards &= ~OFPFW_TP_DST;
                    break;
                case TCP_SRC:
                    setTcpSrc((TransportPort) value);
                    wildcards &= ~OFPFW_TP_SRC;
                    break;
                case UDP_DST:
                    setTcpDst((TransportPort) value);
                    wildcards &= ~OFPFW_TP_DST;
                    break;
                case UDP_SRC:
                    setTcpSrc((TransportPort) value);
                    wildcards &= ~OFPFW_TP_SRC;
                    break;
                case VLAN_PCP:
                    setVlanPcp((VlanPcp) value);
                    wildcards &= ~OFPFW_DL_VLAN_PCP;
                    break;
                case VLAN_VID:
                    setVlanVid((OFVlanVidMatch) value);
                    wildcards &= ~OFPFW_DL_VLAN;
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "OFMatch does not support matching on field " + field.getName());
            }
            return this;
        }

        @Override
        public <F extends OFValueType<F>> Match.Builder setMasked(MatchField<F> field,
                F value, F mask) {
            initWildcards();
            switch (field.id) {
                case ARP_SPA:
                case ARP_TPA:
                case IPV4_DST:
                case IPV4_SRC:
                    Object valObj = value;
                    Object masObj = mask;
                    IPv4Address ip = ((IPv4Address)valObj);
                    int maskval = ((IPv4Address)masObj).getInt();
                    if (Integer.bitCount(~maskval + 1) != 1)
                        throw new UnsupportedOperationException("OFMatch only supports CIDR masks for IPv4");
                    int maskLen = 32 - Integer.bitCount(maskval);
                    switch(field.id) {
                        case ARP_TPA:
                        case IPV4_DST:
                            setIpv4Dst(ip);
                            wildcards = (wildcards &~OFPFW_NW_DST_MASK) | (maskLen << OFPFW_NW_DST_SHIFT);
                            break;
                        case ARP_SPA:
                        case IPV4_SRC:
                            setIpv4Src(ip);
                            wildcards = (wildcards &~OFPFW_NW_SRC_MASK) | (maskLen << OFPFW_NW_SRC_SHIFT);
                            break;
                        default:
                            // Cannot really get here
                            break;
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("OFMatch does not support masked matching on field " + field.getName());
            }
            return this;
        }

        @Override
        public <F extends OFValueType<F>> Match.Builder setMasked(MatchField<F> field, Masked<F> valueWithMask)
                                                                       throws UnsupportedOperationException {
            return this.setMasked(field, valueWithMask.getValue(), valueWithMask.getMask());
        }

        @Override
        public <F extends OFValueType<F>> Match.Builder wildcard(MatchField<F> field) {
            initWildcards();
            switch (field.id) {
                case ETH_DST:
                    setEthDst(MacAddress.NONE);
                    wildcards |= OFPFW_DL_DST;
                    break;
                case ETH_SRC:
                    setEthSrc(MacAddress.NONE);
                    wildcards |= OFPFW_DL_SRC;
                    break;
                case ETH_TYPE:
                    setEthType(EthType.NONE);
                    wildcards |= OFPFW_DL_TYPE;
                    break;
                case ICMPV4_CODE:
                case TCP_DST:
                case UDP_DST:
                case SCTP_DST:
                    setTcpDst(TransportPort.NONE);
                    wildcards |= OFPFW_TP_DST;
                    break;
                case ICMPV4_TYPE:
                case TCP_SRC:
                case UDP_SRC:
                case SCTP_SRC:
                    setTcpSrc(TransportPort.NONE);
                    wildcards |= OFPFW_TP_SRC;
                    break;
                case IN_PORT:
                    setInPort(OFPort.of(0)); // NOTE: not 'NONE' -- that is 0xFF for ports
                    wildcards |= OFPFW_IN_PORT;
                    break;
                case ARP_TPA:
                case IPV4_DST:
                    setIpv4Dst(IPv4Address.NONE);
                    wildcards |= OFPFW_NW_DST_MASK;
                    break;
                case ARP_SPA:
                case IPV4_SRC:
                    setIpv4Src(IPv4Address.NONE);
                    wildcards |= OFPFW_NW_SRC_MASK;
                    break;
                case IP_DSCP:
                    setIpDscp(IpDscp.NONE);
                    wildcards |= OFPFW_NW_TOS;
                    break;
                case IP_PROTO:
                    setIpProto(IpProtocol.NONE);
                    wildcards |= OFPFW_NW_PROTO;
                    break;
                case VLAN_PCP:
                    setVlanPcp(VlanPcp.NONE);
                    wildcards |= OFPFW_DL_VLAN_PCP;
                    break;
                case VLAN_VID:
                    setVlanVid(OFVlanVidMatch.NONE);
                    wildcards |= OFPFW_DL_VLAN;
                    break;
                default:
                    throw new UnsupportedOperationException("OFMatch does not support matching on field " + field.getName());
            }
            return this;
        }

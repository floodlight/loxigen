
    @Override
    public <F extends OFValueType<F>> F get(MatchField<F> field)
            throws UnsupportedOperationException {
        if (!supports(field))
            throw new UnsupportedOperationException("OFMatchV3Ver13 does not support matching on field " + field.getName());

        OFOxm<F> oxm = this.oxmList.get(field);

        if (oxm == null || !field.arePrerequisitesOK(this))
            return null;

        return oxm.getValue();
    }

    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field)
            throws UnsupportedOperationException {
        if (!supportsMasked(field))
            throw new UnsupportedOperationException("OFMatchV3Ver13 does not support masked matching on field " + field.getName());

        OFOxm<F> oxm = this.oxmList.get(field);

        if (oxm == null || !field.arePrerequisitesOK(this))
            return null;

        if (oxm.getMask() == null)
            return null;

        // TODO: Make OfOxm extend Masked and just return the OXM?
        return Masked.of(oxm.getValue(), oxm.getMask());
    }

    private static boolean supportsField(MatchField<?> field) {
        switch (field.id) {
            case IN_PORT:
            case IN_PHY_PORT:
            case METADATA:
            case ETH_DST:
            case ETH_SRC:
            case ETH_TYPE:
            case VLAN_VID:
            case VLAN_PCP:
            case IP_DSCP:
            case IP_ECN:
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
            case ARP_OP:
            case ARP_SPA:
            case ARP_TPA:
            case ARP_SHA:
            case ARP_THA:
            case IPV6_SRC:
            case IPV6_DST:
            case IPV6_FLABEL:
            case BSN_IN_PORTS_128:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean supports(MatchField<?> field) {
        return supportsField(field);
    }

    @Override
    public boolean supportsMasked(MatchField<?> field) {
        return supportsField(field);
    }

    @Override
    public boolean isExact(MatchField<?> field) {
        if (!supports(field))
            throw new UnsupportedOperationException("OFMatchV3Ver13 does not support matching on field " + field.getName());

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm != null && !oxm.isMasked();
    }

    @Override
    public boolean isFullyWildcarded(MatchField<?> field) {
        if (!supports(field))
            throw new UnsupportedOperationException("OFMatchV3Ver13 does not support matching on field " + field.getName());

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm == null;
    }

    @Override
    public boolean isPartiallyMasked(MatchField<?> field) {
        if (!supports(field))
            throw new UnsupportedOperationException("OFMatchV3Ver13 does not support matching on field " + field.getName());

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm != null && oxm.isMasked();
    }

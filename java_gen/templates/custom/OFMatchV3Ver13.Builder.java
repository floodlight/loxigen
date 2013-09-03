
    private OFOxmList.Builder oxmListBuilder;

    private synchronized void initBuilder() {
        if (oxmListBuilder != null)
            return;
        oxmListBuilder = new OFOxmList.Builder();
    }

    private synchronized void updateOxmList() {
        this.oxmList = this.oxmListBuilder.build();
        this.oxmListSet = true;
    }

    private <F extends OFValueType<F>> OFOxm<F> getOxm(MatchField<F> field) {
//:: if has_parent:
        return this.oxmListSet ? this.oxmList.get(field) : parentMessage.oxmList.get(field);
//:: else:
        return this.oxmListSet ? this.oxmList.get(field) : null;
//:: #endif
    }

    @Override
    public synchronized <F extends OFValueType<F>> F get(MatchField<F> field)
            throws UnsupportedOperationException {
        OFOxm<F> value = getOxm(field);
        if (value == null)
            return null;
        return value.getValue();
    }

    @Override
    public synchronized <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field)
            throws UnsupportedOperationException {
        OFOxm<F> value = getOxm(field);
        if (value == null || !value.isMasked())
            return null;
        // TODO: If changing OXMs to extend Masked, then use it here
        return Masked.of(value.getValue(), value.getMask());
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
    public synchronized boolean isExact(MatchField<?> field) {
        OFOxm<?> value = getOxm(field);
        return (value != null && !value.isMasked());
    }

    @Override
    public synchronized boolean isFullyWildcarded(MatchField<?> field) {
        OFOxm<?> value = getOxm(field);
        return (value == null);
    }

    @Override
    public synchronized boolean isPartiallyMasked(MatchField<?> field) {
        OFOxm<?> value = getOxm(field);
        return (value != null && value.isMasked());
    }

    @Override
    public synchronized <F extends OFValueType<F>> Match.Builder setExact(
            MatchField<F> field, F value) {
        initBuilder();
        OFOxm<F> oxm = OFFactories.getFactory(OFVersion.OF_13).oxms().fromValue(value, field);
        this.oxmListBuilder.set(oxm);
        updateOxmList();
        return this;
    }

    @Override
    public synchronized <F extends OFValueType<F>> Match.Builder setMasked(
            MatchField<F> field, F value, F mask) {
        initBuilder();
        OFOxm<F> oxm = OFFactories.getFactory(OFVersion.OF_13).oxms().fromValueAndMask(value, mask, field);
        this.oxmListBuilder.set(oxm);
        updateOxmList();
        return this;
    }

    @Override
    public synchronized <F extends OFValueType<F>> Match.Builder setMasked(
            MatchField<F> field, Masked<F> valueWithMask) {
        initBuilder();
        OFOxm<F> oxm = OFFactories.getFactory(OFVersion.OF_13).oxms().fromMasked(valueWithMask, field);
        this.oxmListBuilder.set(oxm);
        updateOxmList();
        return this;
    }

    @Override
    public synchronized <F extends OFValueType<F>> Match.Builder wildcard(MatchField<F> field) {
        initBuilder();
        this.oxmListBuilder.unset(field);
        updateOxmList();
        return this;
    }


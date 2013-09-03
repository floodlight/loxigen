
    @Override
    public <F extends OFValueType<F>> F get(MatchField<F> field)
            throws UnsupportedOperationException {
        // FIXME yotam - please replace with real implementation
        return null;
    }

    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field)
            throws UnsupportedOperationException {
        // FIXME yotam - please replace with real implementation
        return null;
    }

    @Override
    public boolean supports(MatchField<?> field) {
        // FIXME yotam - please replace with real implementation
        return false;
    }

    @Override
    public boolean supportsMasked(MatchField<?> field) {
        // FIXME yotam - please replace with real implementation
        return false;
    }

    @Override
    public boolean isExact(MatchField<?> field) {
        // FIXME yotam - please replace with real implementation
        return false;
    }

    @Override
    public boolean isFullyWildcarded(MatchField<?> field) {
        // FIXME yotam - please replace with real implementation
        return false;
    }

    @Override
    public boolean isPartiallyMasked(MatchField<?> field) {
        // FIXME yotam - please replace with real implementation
        return false;
    }

    @Override
    public <F extends OFValueType<F>> Match.Builder setExact(
            MatchField<F> field, F value) {
        // FIXME yotam - please replace with real implementation
        return null;
    }

    @Override
    public <F extends OFValueType<F>> Match.Builder setMasked(
            MatchField<F> field, F value, F mask) {
        // FIXME yotam - please replace with real implementation
        return null;
    }

    @Override
    public <F extends OFValueType<F>> Match.Builder setMasked(
            MatchField<F> field, Masked<F> valueWithMask) {
        // FIXME yotam - please replace with real implementation
        return null;
    }

    @Override
    public <F extends OFValueType<F>> Match.Builder wildcard(MatchField<F> field) {
        // FIXME yotam - please replace with real implementation
        return null;
    }

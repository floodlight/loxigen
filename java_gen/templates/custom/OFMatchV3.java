//:: from generic_utils import OrderedSet
//:: from java_gen.java_model import model
    @Override
    public <F extends OFValueType<F>> F get(MatchField<F> field)
            throws UnsupportedOperationException {
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support matching on field " + field.getName());

        OFOxm<F> oxm = this.oxmList.get(field);

        if (oxm == null || !field.arePrerequisitesOK(this))
            return null;

        return oxm.getValue();
    }

    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(MatchField<F> field)
            throws UnsupportedOperationException {
        if (!supportsMasked(field))
            throw new UnsupportedOperationException("${msg.name} does not support masked matching on field " + field.getName());

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
            //:: for id_constant in sorted(set(id_constant for _, id_constant, _ in model.oxm_map.values())):
            case ${id_constant}:
            //:: #endfor
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
            throw new UnsupportedOperationException("${msg.name} does not support matching on field " + field.getName());

        if(!field.arePrerequisitesOK(this))
            return false;

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm != null && !oxm.isMasked();
    }

    @Override
    public boolean isFullyWildcarded(MatchField<?> field) {
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support matching on field " + field.getName());
        if(!field.arePrerequisitesOK(this))
            return true;

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm == null;
    }

    @Override
    public boolean isPartiallyMasked(MatchField<?> field) {
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support matching on field " + field.getName());
        if(!field.arePrerequisitesOK(this))
            return false;

        OFOxm<?> oxm = this.oxmList.get(field);

        return oxm != null && oxm.isMasked();
    }

    private class MatchFieldIterator extends AbstractIterator<MatchField<?>> {
        private Iterator<OFOxm<?>> oxmIterator;

        MatchFieldIterator() {
            oxmIterator = oxmList.iterator();
        }

        @Override
        protected MatchField<?> computeNext() {
            while(oxmIterator.hasNext()) {
                OFOxm<?> oxm = oxmIterator.next();
                if(oxm.getMatchField().arePrerequisitesOK(${msg.name}.this))
                   return oxm.getMatchField();
            }
            endOfData();
            return null;
        }
    }

    @Override
    public Iterable<MatchField<?>> getMatchFields() {
        return new Iterable<MatchField<?>>() {
            public Iterator<MatchField<?>> iterator() {
                return new MatchFieldIterator();
            }
        };
    }


//:: from generic_utils import OrderedSet
//:: from java_gen.java_model import model
    @Override
    public <F extends OFValueType<F>> F get(StatFields<F> Fields)
            throws UnsupportedOperationException {
        if (!supports(Fields))
            throw new UnsupportedOperationException("${msg.name} does not support statistics on Fields " + Fields.getName());

        OFOxs<F> oxs = this.oxsFields.get(Fields);

        if (oxs == null)
            return null;

        return oxs.getValue();
    }

    @Override
    public Iterable<StatFields<?>> getStatFieldss() {
        return new Iterable<StatFields<?>>() {
            public Iterator<StatFields<?>> iterator() {
                return new StatFieldsIterator();
            }
        };
    }
    
    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(StatFields<F> Fields)
            throws UnsupportedOperationException {
        if (!supportsMasked(Fields))
            throw new UnsupportedOperationException("${msg.name} does not support masked statistics on Fields " + Fields.getName());

        OFOxs<F> oxs = this.oxsFields.get(Fields);

        if (oxs == null)
            return null;

        if (oxs.getMask() == null)
            return null;

        // TODO: Make OfOxs extend Masked and just return the OXS?
        return Masked.of(oxs.getValue(), oxs.getMask());
    }
    
    @Override
    public boolean supports(StatFields<?> Fields) {
        return supportsFields(Fields);
    }

    @Override
    public boolean supportsMasked(StatFields<?> Fields) {
        return supportsFields(Fields);
    }
    
    private static boolean supportsFields(StatFields<?> Fields) {
        switch (Fields.id) {
            //:: for id_constant in sorted(set(id_constant for _, id_constant, _ in model.oxs_map.values())):
            case ${id_constant}:
            //:: #endfor
                return true;
            default:
                return false;
        }
    }
    
    private class StatFieldsIterator extends AbstractIterator<StatFields<?>> {
        private Iterator<OFOxs<?>> oxsIterator;

        StatFieldsIterator() {
            oxsIterator = oxsFields.iterator();
        }

        @Override
        protected StatFields<?> computeNext() {
            while(oxsIterator.hasNext()) {
                OFOxs<?> oxs = oxsIterator.next();
                return oxs.getStatFields();
            }
            endOfData();
            return null;
        }
        
        @Override
        public boolean isPartiallyMasked(StatField<?> field) {
            OFOxs<?> value = getOxs(field);
            return (value != null && value.isMasked());
        }


        
    }

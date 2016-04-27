
//:: from generic_utils import OrderedSet
//:: from java_gen.java_model import model
    @Override
    public <F extends OFValueType<F>> F get(StatField<F> field)
            throws UnsupportedOperationException {
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support statistics on field " + field.getName());

        OFOxs<F> oxs = this.oxsFields.get(field);

        if (oxs == null)
            return null;

        return oxs.getValue();
    }

    @Override
    public Iterable<StatField<?>> getStatFields() {
        return new Iterable<StatField<?>>() {
            public Iterator<StatField<?>> iterator() {
                return new StatFieldIterator();
            }
        };
    }

    @Override
    public boolean supports(StatField<?> field) {
        return supportsField(field);
    }

    private static boolean supportsField(StatField<?> field) {
        switch (field.id) {
            //:: for id_constant in sorted(set(id_constant for _, id_constant, _ in model.oxs_map.values())):
            case ${id_constant}:
            //:: #endfor
                return true;
            default:
                return false;
        }
    }

    private class StatFieldIterator extends AbstractIterator<StatField<?>> {
        private Iterator<OFOxs<?>> oxsIterator;

        StatFieldIterator() {
            oxsIterator = oxsFields.iterator();
        }

        @Override
        protected StatField<?> computeNext() {
            while(oxsIterator.hasNext()) {
                OFOxs<?> oxs = oxsIterator.next();
                return oxs.getStatField();
            }
            endOfData();
            return null;
        }
    }

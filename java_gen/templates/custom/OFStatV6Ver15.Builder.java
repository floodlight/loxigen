
    @Override
    public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException{
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support statistics on field " + field.getName());

        OFOxs<F> oxs = this.oxsFields.get(field);

        if (oxs == null)
            return null;

        return oxs.getValue();
    }
 
    @Override
    public <F extends OFValueType<F>> Masked<F> getMasked(StatField<F> field) throws UnsupportedOperationException{
        if (!supportsMasked(field))
            throw new UnsupportedOperationException("${msg.name} does not support masked statistics on field " + field.getName());

        OFOxs<F> oxs = this.oxsFields.get(field);

        if (oxs == null)
            return null;

        if (oxs.getMask() == null)
            return null;

        // TODO: Make OfOxs extend Masked and just return the OXS?
        return Masked.of(oxs.getValue(), oxs.getMask());
    }
 
    @Override
    public boolean supports(StatField<?> field){
        return supportsField(field);
    }

    @Override
    public boolean supportsMasked(StatField<?> field) throws UnsupportedOperationException{
        return supportsField(field);
    }

    @Override
    public boolean isPartiallyMasked(StatField<?> field) {
        OFOxs<?> value = getOxs(field);
        return (value != null && value.isMasked());
    }

    private <F extends OFValueType<F>> OFOxs<F> getOxs(StatField<F> field) {
//:: if has_parent:
        return this.oxsFieldsSet ? this.oxsFields.get(field) : parentMessage.oxsFields.get(field);
//:: else:
        return this.oxsFieldsSet ? this.oxsFields.get(field) : null;
//:: #endif
    }
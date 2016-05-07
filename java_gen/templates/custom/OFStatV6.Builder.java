    private OFOxsList.Builder oxsFieldsBuilder;

    private void initBuilder() {
        if (oxsFieldsBuilder != null)
            return;
        oxsFieldsBuilder = new OFOxsList.Builder();
    }

    private void updateOxsList() {
        this.oxsFields = this.oxsFieldsBuilder.build();
        this.oxsFieldsSet = true;
    }



    @Override
    public <F extends OFValueType<F>> F get(StatField<F> field) throws UnsupportedOperationException{
        if (!supports(field))
            throw new UnsupportedOperationException("${msg.name} does not support statistics on field " + field.getName());

        OFOxs<F> oxs = getOxs(field);

        if (oxs == null)
            return null;

        return oxs.getValue();
    }

    @Override
    public <F extends OFValueType<F>> Stat.Builder set(StatField<F> field, F value) {
        initBuilder();
        OFOxs<F> oxs = OFFactories.getFactory(OFVersion.${version.constant_version}).oxss().fromValue(value, field);
        this.oxsFieldsBuilder.set(oxs);
        updateOxsList();
        return this;
    }


    @Override
    public boolean supports(StatField<?> field){
        return supportsField(field);
    }

    private <F extends OFValueType<F>> OFOxs<F> getOxs(StatField<F> field) {
//:: if has_parent:
        return this.oxsFieldsSet ? this.oxsFields.get(field) : parentMessage.oxsFields.get(field);
//:: else:
        return this.oxsFieldsSet ? this.oxsFields.get(field) : null;
//:: #endif
    }

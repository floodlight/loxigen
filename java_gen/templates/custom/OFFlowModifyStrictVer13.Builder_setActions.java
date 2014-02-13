
    @Override
    public OFFlowModifyStrict.Builder setActions(List<OFAction> actions) throws UnsupportedOperationException {
        OFInstructionApplyActionsVer13.Builder builder = new OFInstructionApplyActionsVer13.Builder();
        builder.setActions(actions);
        this.instructions = Collections.singletonList((OFInstruction)builder.build());
        this.instructionsSet = true;
        return this;
    }

    @Override
    public ${msg.interface.name}.Builder setActions(List<OFAction> actions) throws UnsupportedOperationException {
        OFInstructionApplyActionsVer${version.dotless_version}.Builder builder = new OFInstructionApplyActionsVer${version.dotless_version}.Builder();
        builder.setActions(actions);
        this.instructions = Collections.singletonList((OFInstruction)builder.build());
        this.instructionsSet = true;
        return this;
    }


    @Override
    public List<OFAction> getActions()throws UnsupportedOperationException {
//:: if builder:
        if (!this.instructionsSet)
//:: if has_parent:
            return parentMessage.getActions();
//:: else:
            return Collections.emptyList();
//:: #endif
//:: #endif
        ImmutableList.Builder<OFAction> builder = ImmutableList.builder();
        for (OFInstruction inst : this.instructions) {
            if (inst instanceof OFInstructionApplyActions) {
                OFInstructionApplyActions iap = (OFInstructionApplyActions)inst;
                builder.addAll(iap.getActions());
            }
        }
        return builder.build();
    }

    @Override
    public List<OFAction> getActions()throws UnsupportedOperationException {
        ImmutableList.Builder<OFAction> builder = ImmutableList.builder();
        for (OFInstruction inst : this.instructions) {
            if (inst instanceof OFInstructionApplyActions) {
                OFInstructionApplyActions iap = (OFInstructionApplyActions)inst;
                builder.addAll(iap.getActions());
            }
        }
        return builder.build();
    }
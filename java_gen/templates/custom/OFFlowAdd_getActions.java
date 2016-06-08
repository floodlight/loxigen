
    @Override
    public List<OFAction> getActions()throws UnsupportedOperationException {
        for (OFInstruction inst : this.instructions) {
            if (inst instanceof OFInstructionApplyActions) {
                OFInstructionApplyActions iap = (OFInstructionApplyActions)inst;
                return iap.getActions();
            }
        }
        return Collections.emptyList();
    }

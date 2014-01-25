    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("${msg.name}(");
        boolean first = true;
        for(MatchField<?> field : getMatchFields()) {
            if(first)
                first = false;
            else
                b.append(", ");
            String name = field.getName();
            b.append(name).append('=').append(this.get(field));
            if(isPartiallyMasked(field)) {
                b.append('/').append(this.getMasked(field).getMask());
            }
        }
        b.append(")");
        return b.toString();
    }

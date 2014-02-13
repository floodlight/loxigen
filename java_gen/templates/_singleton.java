
    private ${msg.name}() {}

    private final static class Holder {
        private static final ${msg.name} INSTANCE = new ${msg.name}();
    }

    public static ${msg.name} getInstance() {
        return Holder.INSTANCE;
    }

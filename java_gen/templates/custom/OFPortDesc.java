
    /**
     * Returns true if the port is up, i.e., it's neither administratively
     * down nor link down. It currently does NOT take STP state into
     * consideration
     * @return whether the port is up
     */
    public boolean isEnabled() {
        return (!state.contains(OFPortState.LINK_DOWN) && !config.contains(OFPortConfig.PORT_DOWN));
    }

    /**
     * Returns the current generation ID of this port.
     *
     * The generationId is reported by the switch as a @{link OFPortDescProp} in
     * {@link OFPortDescStatsReply} and {@link OFPortStatus} messages. If the
     * current OFPortDesc does not contain a generation Id, returns U64.ZERO;
     *
     * For OpenFlow versions earlier than 1.4, always returns U64.ZERO;
     *
     * @return the generation ID or U64.NULL if not reported
     * @since 1.4
     */
     @Nonnull
     public U64 getBsnGenerationId() {
         //:: if msg.member_by_name("properties"):
         for(OFPortDescProp prop: getProperties()) {
            if(prop instanceof OFPortDescPropBsnGenerationId) {
                return ((OFPortDescPropBsnGenerationId) prop).getGenerationId();
            }
         }
         //:: #endif
         return U64.ZERO;
     }


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
     * Return a brief String describing this port containing the port number
     * and port name
     * @return a brief description string 
     */
    public String toBriefString() {
        return String.format("%s (%d)", name, portNo);
    }
    
package org.projectfloodlight.openflow.types;

/**
 * Represents the speed of a port
 */
public enum PortSpeed {
    /** no speed set */
    SPEED_NONE(0),
    SPEED_10MB(10),
    SPEED_100MB(100),
    SPEED_1GB(1000),
    SPEED_10GB(10000),
    SPEED_40GB(40000),
    SPEED_100GB(100000),
    SPEED_1TB(1000000);

    private long speedInBps;
    private PortSpeed(int speedInMbps) {
        this.speedInBps = speedInMbps * 1000L*1000L;
    }

    public long getSpeedBps() {
        return this.speedInBps;
    }

    public static PortSpeed max(PortSpeed s1, PortSpeed s2) {
        return (s1.getSpeedBps() > s2.getSpeedBps()) ? s1 : s2;
    }

    public static PortSpeed min(PortSpeed s1, PortSpeed s2) {
        return (s1.getSpeedBps() < s2.getSpeedBps()) ? s1 : s2;
    }
}

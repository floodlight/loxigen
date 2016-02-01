package org.projectfloodlight.openflow.protocol.stat;

import org.projectfloodlight.openflow.types.OFValueType;
import org.projectfloodlight.openflow.types.OFDuration;
import org.projectfloodlight.openflow.types.OFIdleTime;
import org.projectfloodlight.openflow.types.OFFlowCount;
import org.projectfloodlight.openflow.types.OFPacketCount;
import org.projectfloodlight.openflow.types.OFByteCount;
import org.projectfloodlight.openflow.protocol.stat.StatFields;

public class StatField<F extends OFValueType<F>> {
	
    private final String name;
    public final StatFields id;
    
    private StatField(final String name, final StatFields id) {
        this.name = name;
        this.id = id;
    }
    
    public final static StatField<OFDuration> DURATION =
            new StatField<OFDuration>("of_duration", StatFields.DURATION);
    
    public final static StatField<OFIdleTime> IDLE_TIME =
            new StatField<OFIdleTime>("of_duration", StatFields.IDLE_TIME);
    
    public final static StatField<OFFlowCount> FLOW_COUNT =
            new StatField<OFFlowCount>("of_duration", StatFields.FLOW_COUNT);
    
    public final static StatField<OFPacketCount> PACKET_COUNT =
            new StatField<OFPacketCount>("of_duration", StatFields.PACKET_COUNT);
    
    public final static StatField<OFByteCount> BYTE_COUNT =
            new StatField<OFByteCount>("of_duration", StatFields.BYTE_COUNT);
    
    public String getName() {
        return name;
    }
    
}
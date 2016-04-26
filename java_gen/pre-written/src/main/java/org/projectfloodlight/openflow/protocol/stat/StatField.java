package org.projectfloodlight.openflow.protocol.stat;

import org.projectfloodlight.openflow.types.OFValueType;
import org.projectfloodlight.openflow.types.U32;
import org.projectfloodlight.openflow.types.U64;

public class StatField<F extends OFValueType<F>> {

    private final String name;
    public final StatFields id;

    private StatField(final String name, final StatFields id) {
        this.name = name;
        this.id = id;
    }

    public final static StatField<U64> DURATION =
            new StatField<U64>("of_duration", StatFields.DURATION);

    public final static StatField<U64> IDLE_TIME =
            new StatField<U64>("of_idle_time", StatFields.IDLE_TIME);

    public final static StatField<U32> FLOW_COUNT =
            new StatField<U32>("of_flow_count", StatFields.FLOW_COUNT);

    public final static StatField<U64> PACKET_COUNT =
            new StatField<U64>("of_packet_count", StatFields.PACKET_COUNT);

    public final static StatField<U64> BYTE_COUNT =
            new StatField<U64>("of_byte_count", StatFields.BYTE_COUNT);

    public String getName() {
        return name;
    }

}

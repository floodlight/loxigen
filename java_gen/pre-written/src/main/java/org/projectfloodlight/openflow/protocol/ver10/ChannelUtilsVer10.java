package org.projectfloodlight.openflow.protocol.ver10;

import java.util.EnumSet;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.ver10.OFMatchV1Ver10;
import org.projectfloodlight.openflow.protocol.OFActionType;
import org.projectfloodlight.openflow.protocol.OFBsnVportQInQ;

/**
 * Collection of helper functions for reading and writing into ChannelBuffers
 *
 * @author capveg
 */

public class ChannelUtilsVer10 {
    public static Match readOFMatch(final ChannelBuffer bb) throws OFParseError {
        return OFMatchV1Ver10.READER.readFrom(bb);
    }

    // TODO these need to be figured out / removed
    public static OFBsnVportQInQ readOFBsnVportQInQ(ChannelBuffer bb) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void writeOFBsnVportQInQ(ChannelBuffer bb,
            OFBsnVportQInQ vport) {
        throw new UnsupportedOperationException("not implemented");

    }

    public static Set<OFActionType> readSupportedActions(ChannelBuffer bb) {
        int actions = bb.readInt();
        EnumSet<OFActionType> supportedActions = EnumSet.noneOf(OFActionType.class);
        if ((actions & (1 << OFActionTypeSerializerVer10.OUTPUT_VAL)) != 0)
            supportedActions.add(OFActionType.OUTPUT);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_VLAN_VID_VAL)) != 0)
            supportedActions.add(OFActionType.SET_VLAN_VID);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_VLAN_PCP_VAL)) != 0)
            supportedActions.add(OFActionType.SET_VLAN_PCP);
        if ((actions & (1 << OFActionTypeSerializerVer10.STRIP_VLAN_VAL)) != 0)
            supportedActions.add(OFActionType.STRIP_VLAN);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_DL_SRC_VAL)) != 0)
            supportedActions.add(OFActionType.SET_DL_SRC);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_DL_DST_VAL)) != 0)
            supportedActions.add(OFActionType.SET_DL_DST);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_NW_SRC_VAL)) != 0)
            supportedActions.add(OFActionType.SET_NW_SRC);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_NW_DST_VAL)) != 0)
            supportedActions.add(OFActionType.SET_NW_DST);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_NW_TOS_VAL)) != 0)
            supportedActions.add(OFActionType.SET_NW_TOS);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_TP_SRC_VAL)) != 0)
            supportedActions.add(OFActionType.SET_TP_SRC);
        if ((actions & (1 << OFActionTypeSerializerVer10.SET_TP_DST_VAL)) != 0)
            supportedActions.add(OFActionType.SET_TP_DST);
        if ((actions & (1 << OFActionTypeSerializerVer10.ENQUEUE_VAL)) != 0)
            supportedActions.add(OFActionType.ENQUEUE);
        return supportedActions;
    }

    public static void writeSupportedActions(ChannelBuffer bb,
            Set<OFActionType> supportedActions) {
        int supportedActionsVal = 0;
        if (supportedActions.contains(OFActionType.OUTPUT))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.OUTPUT_VAL);
        if (supportedActions.contains(OFActionType.SET_VLAN_VID))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_VLAN_VID_VAL);
        if (supportedActions.contains(OFActionType.SET_VLAN_PCP))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_VLAN_PCP_VAL);
        if (supportedActions.contains(OFActionType.STRIP_VLAN))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.STRIP_VLAN_VAL);
        if (supportedActions.contains(OFActionType.SET_DL_SRC))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_DL_SRC_VAL);
        if (supportedActions.contains(OFActionType.SET_DL_DST))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_DL_DST_VAL);
        if (supportedActions.contains(OFActionType.SET_NW_SRC))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_NW_SRC_VAL);
        if (supportedActions.contains(OFActionType.SET_NW_DST))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_NW_DST_VAL);
        if (supportedActions.contains(OFActionType.SET_NW_TOS))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_NW_TOS_VAL);
        if (supportedActions.contains(OFActionType.SET_TP_SRC))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_TP_SRC_VAL);
        if (supportedActions.contains(OFActionType.SET_TP_DST))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.SET_TP_DST_VAL);
        if (supportedActions.contains(OFActionType.ENQUEUE))
            supportedActionsVal |= (1 << OFActionTypeSerializerVer10.ENQUEUE_VAL);
        bb.writeInt(supportedActionsVal);
    }
}

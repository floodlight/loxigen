package org.projectfloodlight.openflow.protocol.ver10;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.OFActionType;
import org.projectfloodlight.openflow.protocol.OFBsnVportQInQ;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.Match;

import com.google.common.hash.PrimitiveSink;

/**
 * Collection of helper functions for reading and writing into ChannelBuffers
 *
 * @author capveg
 */

public class ChannelUtilsVer10 {
    public static Match readOFMatch(final ChannelBuffer bb) throws OFParseError {
        return OFMatchV1Ver10.READER.readFrom(bb);
    }

    public static OFBsnVportQInQ readOFBsnVportQInQ(ChannelBuffer bb) {
        OFFactory factory = OFFactories.getFactory(OFVersion.OF_10);
        OFBsnVportQInQ.Builder builder = factory.buildBsnVportQInQ();
        builder.setPortNo(bb.readInt())
            .setIngressTpid(bb.readShort())
            .setIngressVlanId(bb.readShort())
            .setEgressTpid(bb.readShort())
            .setEgressVlanId(bb.readShort());
        byte[] name = new byte[16];
        int index = 0;
        for (byte b : name) {
            if (0 == b) break;
            ++index;
        }
        builder.setIfName(new String(Arrays.copyOf(name, index),
                Charset.forName("ascii")));
        return builder.build();
    }

    public static void writeOFBsnVportQInQ(ChannelBuffer bb,
            OFBsnVportQInQ vport) {
        bb.writeInt((int)vport.getPortNo());
        bb.writeShort(vport.getIngressTpid());
        bb.writeShort(vport.getIngressVlanId());
        bb.writeShort(vport.getEgressTpid());
        bb.writeShort(vport.getEgressVlanId());
        String name = vport.getIfName();
        byte[] nameInBytes = new byte[16];
        for (int i = 0; i < nameInBytes.length; i++) {
            nameInBytes[i] = 0;
        }
        System.arraycopy(name.getBytes(), 0, nameInBytes, 0, name.length());
        bb.writeBytes(nameInBytes);
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

    public static int supportedActionsToWire(Set<OFActionType> supportedActions) {
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
        return supportedActionsVal;
    }

    public static void putSupportedActionsTo(Set<OFActionType> supportedActions, PrimitiveSink sink) {
        sink.putInt(supportedActionsToWire(supportedActions));
    }

    public static void writeSupportedActions(ChannelBuffer bb, Set<OFActionType> supportedActions) {
        bb.writeInt(supportedActionsToWire(supportedActions));
    }

}

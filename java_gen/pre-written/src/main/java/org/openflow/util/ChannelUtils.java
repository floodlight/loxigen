package org.openflow.util;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.actions.OFAction;
import org.openflow.protocol.instructions.OFInstruction;
import org.openflow.protocol.match.Match;
import org.openflow.types.OFBsnInterface;
import org.openflow.types.OFBucket;
import org.openflow.types.OFFlowModCmd;
import org.openflow.types.OFHelloElement;
import org.openflow.types.OFMeterBand;
import org.openflow.types.OFPacketQueue;
import org.openflow.types.OFPhysicalPort;

/**
 * Collection of helper functions for reading and writing into ChannelBuffers
 *
 * @author capveg
 */

public class ChannelUtils {

    static public byte[] readBytes(final ChannelBuffer bb, final int length) {
        byte byteArray[] = new byte[length];
        bb.readBytes(byteArray);
        return byteArray;
    }

    static public void writeBytes(final ChannelBuffer bb, final byte byteArray[]) {
        bb.writeBytes(byteArray);
    }

    public static List<OFPhysicalPort> readPhysicalPortList(final ChannelBuffer bb,
            final int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFInstruction> readInstructionsList(final ChannelBuffer bb,
            final int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static Match readOFMatch(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static OFFlowModCmd readOFFlowModCmd(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFAction> readActionsList(final ChannelBuffer bb, final int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFBsnInterface> readBsnInterfaceList(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static OFPhysicalPort readPhysicalPort(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFPacketQueue> readPacketQueueList(final ChannelBuffer bb,
            final int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFHelloElement> readHelloElementList(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFBucket> readBucketList(final ChannelBuffer bb, final int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFMeterBand> readMeterBandList(final ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

}

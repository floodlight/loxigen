package org.openflow.util;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.OFBsnInterface;
import org.openflow.protocol.OFBsnVportQInQ;
import org.openflow.protocol.OFBsnVportQInQT;
import org.openflow.protocol.OFBucket;
import org.openflow.protocol.OFFlowStatsEntry;
import org.openflow.protocol.OFGroupDescStatsEntry;
import org.openflow.protocol.OFGroupStatsEntry;
import org.openflow.protocol.OFHelloElem;
import org.openflow.protocol.OFMeterFeatures;
import org.openflow.protocol.OFMeterStats;
import org.openflow.protocol.OFPacketQueue;
import org.openflow.protocol.OFPortStatsEntry;
import org.openflow.protocol.OFQueueStatsEntry;
import org.openflow.protocol.OFTableFeature;
import org.openflow.protocol.OFTableFeatures;
import org.openflow.protocol.OFTableStatsEntry;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.match.Match;
import org.openflow.types.OFFlowModCmd;
import org.openflow.types.OFHelloElement;
import org.openflow.types.OFMeterBand;
import org.openflow.types.OFPhysicalPort;

import com.google.common.base.Charsets;

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

    public static void writeOFMatch(ChannelBuffer bb, Match match) {
        // TODO Auto-generated method stub

    }

    public static void writeList(ChannelBuffer bb, List<?> ports) {
        // TODO Auto-generated method stub

    }

    public static void writeOFFlowModCmd(ChannelBuffer bb, OFFlowModCmd command) {
        // TODO Auto-generated method stub

    }

    public static String readFixedLengthString(ChannelBuffer bb, int length) {
        byte[] dst = new byte[length];
        bb.readBytes(dst, 0, length);
        return new String(dst, Charsets.US_ASCII);
    }

    public static void writeFixedLengthString(ChannelBuffer bb, String string, int length) {
        int l = string.length();
        if(l > length) {
            throw new IllegalArgumentException("Error writing string: length="+l+" > max Length="+length);
        }
        bb.writeBytes(string.getBytes(Charsets.US_ASCII));
        if(l < length) {
            bb.writeZero(length - l);
        }
    }

    public static void writeBsnInterfaceList(ChannelBuffer bb, List<OFBsnInterface> interfaces) {
        // TODO Auto-generated method stub

    }

    public static void writeTableFeatureList(ChannelBuffer bb,
            List<OFTableFeature> entries) {
        // TODO Auto-generated method stub

    }

    public static void writeBsnInterface(ChannelBuffer bb,
            List<OFBsnInterface> interfaces) {
        // TODO Auto-generated method stub

    }

    public static void writeFlowStatsEntry(ChannelBuffer bb,
            List<OFFlowStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static List<OFFlowStatsEntry> readFlowStatsEntry(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeTableStatsEntryList(ChannelBuffer bb,
            List<OFTableStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static List<OFTableStatsEntry> readTableStatsEntryList(
            ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<OFFlowStatsEntry> readFlowStatsEntryList(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeFlowStatsEntryList(ChannelBuffer bb,
            List<OFFlowStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static void writeGroupDescStatsEntryList(ChannelBuffer bb,
            List<OFGroupDescStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static List<OFGroupDescStatsEntry> readGroupDescStatsEntryList(
            ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeOFBsnVportQInQT(ChannelBuffer bb,
            OFBsnVportQInQT vport) {
        // TODO Auto-generated method stub

    }

    public static void writeMeterBandList(ChannelBuffer bb,
            List<OFMeterBand> entries) {
        // TODO Auto-generated method stub

    }

    public static int writeActionsList(ChannelBuffer bb, List<OFAction> actions) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static OFBsnVportQInQ readOFBsnVportQInQ(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writePortStatsEntryList(ChannelBuffer bb,
            List<OFPortStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static void write(ChannelBuffer bb, OFPhysicalPort desc) {
        // TODO Auto-generated method stub

    }

    public static List<OFPortStatsEntry> readPortStatsEntryList(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeOFBsnVportQInQ(ChannelBuffer bb,
            OFBsnVportQInQ vport) {
        // TODO Auto-generated method stub

    }

    public static List<OFHelloElem> readHelloElemList(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeHelloElemList(ChannelBuffer bb,
            List<OFHelloElem> elements) {
        // TODO Auto-generated method stub

    }

    public static List<OFGroupStatsEntry> readGroupStatsEntryList(
            ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeGroupStatsEntryList(ChannelBuffer bb,
            List<OFGroupStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static List<OFQueueStatsEntry> readQueueStatsEntryList(
            ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeQueueStatsEntryList(ChannelBuffer bb,
            List<OFQueueStatsEntry> entries) {
        // TODO Auto-generated method stub

    }

    public static OFMeterFeatures readOFMeterFeatures(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeOFMeterFeatures(ChannelBuffer bb,
            OFMeterFeatures features) {
        // TODO Auto-generated method stub

    }

    public static List<OFMeterStats> readMeterStatsList(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeMeterStatsList(ChannelBuffer bb,
            List<OFMeterStats> entries) {
        // TODO Auto-generated method stub

    }

    public static List<OFTableFeatures> readTableFeaturesList(ChannelBuffer bb) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void writeTableFeaturesList(ChannelBuffer bb,
            List<OFTableFeatures> entries) {
        // TODO Auto-generated method stub

    }


}

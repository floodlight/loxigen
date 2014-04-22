package org.projectfloodlight.openflow.util;

import java.util.Set;

import org.projectfloodlight.openflow.protocol.OFBsnPktinFlag;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.ver13.OFBsnPktinFlagSerializerVer13;
import org.projectfloodlight.openflow.types.OFMetadata;

public class MultiplePktInReasonUtil {
    private MultiplePktInReasonUtil() {}

    public static Set<OFBsnPktinFlag> getOFBsnPktinFlags(OFPacketIn pktIn) {
        if(pktIn.getVersion() != OFVersion.OF_13) {
            throw new IllegalArgumentException("multiple pkt in reasons are "
                                               + "only supported by BVS using "
                                               + "openflow 1.3");
        }
        OFMetadata metaData = pktIn.getMatch().get(MatchField.METADATA);
        return OFBsnPktinFlagSerializerVer13.ofWireValue(metaData.getValue()
                                                                 .getValue());
    }
}

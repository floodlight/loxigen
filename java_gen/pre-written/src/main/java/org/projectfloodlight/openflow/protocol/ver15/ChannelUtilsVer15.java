package org.projectfloodlight.openflow.protocol.ver15;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.OFMatchBmap;
import org.projectfloodlight.openflow.protocol.OFMessageReaderContext;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.stat.Stat;

import io.netty.buffer.ByteBuf;
/**
 * Collection of helper functions for reading and writing into ByteBufs
 *
 * @author capveg
 */

public class ChannelUtilsVer15 {
    public static Match readOFMatch(final OFMessageReaderContext context, final ByteBuf bb) throws OFParseError {
        return OFMatchV3Ver15.READER.readFrom(context, bb);
    }

    public static Stat readOFStat(final OFMessageReaderContext context, final ByteBuf bb) throws OFParseError {
        return OFStatV6Ver15.READER.readFrom(context, bb);
    }

    public static OFMatchBmap readOFMatchBmap(ByteBuf bb) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void writeOFMatchBmap(ByteBuf bb, OFMatchBmap match) {
        throw new UnsupportedOperationException("not implemented");
    }
}
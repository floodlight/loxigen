package org.projectfloodlight.openflow.protocol.ver14;

import io.netty.buffer.ByteBuf;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.OFMatchBmap;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.stat.Stat;

/**
 * Collection of helper functions for reading and writing into ByteBufs
 *
 * @author capveg
 */

public class ChannelUtilsVer14 {
    public static Match readOFMatch(final ByteBuf bb) throws OFParseError {
        return OFMatchV3Ver14.READER.readFrom(bb);
    }

    public static OFMatchBmap readOFMatchBmap(ByteBuf bb) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void writeOFMatchBmap(ByteBuf bb, OFMatchBmap match) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static Stat readOFStat(final ByteBuf bb) throws OFParseError {
        throw new UnsupportedOperationException("not supported");
    }
}

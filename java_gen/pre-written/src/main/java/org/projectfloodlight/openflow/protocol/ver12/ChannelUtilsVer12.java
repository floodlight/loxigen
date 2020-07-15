package org.projectfloodlight.openflow.protocol.ver12;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.OFBsnVportQInQ;
import org.projectfloodlight.openflow.protocol.OFMatchBmap;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.stat.Stat;

import io.netty.buffer.ByteBuf;

/**
 * Collection of helper functions for reading and writing into Unpooled
 *
 * @author capveg
 */

public class ChannelUtilsVer12 {
    public static Match readOFMatch(final ByteBuf bb) throws OFParseError {
        return OFMatchV3Ver12.READER.readFrom(bb);
    }

    // TODO these need to be figured out / removed

    public static OFBsnVportQInQ readOFBsnVportQInQ(ByteBuf bb) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void writeOFBsnVportQInQ(ByteBuf bb,
            OFBsnVportQInQ vport) {
        throw new UnsupportedOperationException("not implemented");

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

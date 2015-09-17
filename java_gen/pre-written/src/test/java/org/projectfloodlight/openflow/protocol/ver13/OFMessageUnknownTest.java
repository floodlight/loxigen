package org.projectfloodlight.openflow.protocol.ver13;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.handler.UnparsedHandlers;
import org.projectfloodlight.openflow.protocol.OFEchoReply;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFMessageReader;
import org.projectfloodlight.openflow.protocol.OFMessageReaderContext;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.oxm.OFOxm;
import org.projectfloodlight.openflow.protocol.oxm.OFOxmInPhyPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class OFMessageUnknownTest {
    private static final Logger logger =
            LoggerFactory.getLogger(OFMessageUnknownTest.class);

    final static byte[] UNKNOWN_MESSAGE_SERIALIZED =
            new byte[] { 0x4, 077, 0x0, 0xb, 0x12, 0x34, 0x56, 0x78, 0x61, 0x62, 0x63 };

    final static byte[] OXM_UNKNOWN_SERIALIZED =
            new byte[] { (byte) 0x80, 0x0, /** tcp flags, introduced in OF1.5 */ 42 << 1, 0x2, 0x0, 0x0};

    @Test
    public void testSkipUnknownMessage() throws OFParseError {
        OFMessageReaderContext context = OFMessageReaderContext.of(UnparsedHandlers.log(logger));
        OFMessageReader<OFMessage> reader = OFFactories.getFactory(OFVersion.OF_13).getReader();
        ByteBuf buffer = Unpooled.wrappedBuffer(UNKNOWN_MESSAGE_SERIALIZED, OFEchoReplyVer13Test.ECHO_REPLY_SERIALIZED);
        OFMessage message = reader.readFrom(context, buffer);
        assertThat(message, CoreMatchers.nullValue());
        assertThat(buffer.readerIndex(), equalTo(UNKNOWN_MESSAGE_SERIALIZED.length));
        OFMessage message2 = reader.readFrom(context, buffer);
        assertThat(message2, CoreMatchers.instanceOf(OFEchoReply.class));
    }

    @Test
    public void testSkipUnknownTlv() throws OFParseError {
        OFMessageReaderContext context = OFMessageReaderContext.of(UnparsedHandlers.log(logger));
        OFMessageReader<OFOxm<?>> reader = OFOxmVer13.READER;
        ByteBuf buffer = Unpooled.wrappedBuffer(OXM_UNKNOWN_SERIALIZED, OFOxmInPhyPortVer13Test.OXM_IN_PHY_PORT_SERIALIZED);
        OFOxm<?> message = reader.readFrom(context, buffer);
        assertThat(message, CoreMatchers.nullValue());
        assertThat(buffer.readerIndex(), equalTo(OXM_UNKNOWN_SERIALIZED.length));
        OFOxm<?> message2 = reader.readFrom(context, buffer);
        assertThat(message2, CoreMatchers.instanceOf(OFOxmInPhyPort.class));
    }
}

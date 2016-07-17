package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.exceptions.OFParseError;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import io.netty.buffer.ByteBuf;
import static io.netty.buffer.Unpooled.*;

import org.junit.Test;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class OFValueTypeWriteableTest {

	final int NUM_BYTES = 256;

	byte[] byteStream;
	List<OFValueType<?>> values;

	public OFValueTypeWriteableTest() throws OFParseError {
		byteStream = new byte[NUM_BYTES];
		for (int i = 0; i < NUM_BYTES; ++i) {
			byteStream[i] = (byte) i;
		}

		ByteBuf bb = wrappedBuffer(byteStream);

		values = new ArrayList<>();

		// Values that must be "low" due to range checks
		values.add(IpEcn.readByte(bb));
		values.add(OFVlanVidMatch.read2Bytes(bb));
		values.add(VlanVid.read2Bytes(bb));
		values.add(VlanPcp.readByte(bb));

		// Values with less restrictive range checking
		values.add(ArpOpcode.read2Bytes(bb));
		values.add(BundleId.read4Bytes(bb));
		values.add(ClassId.read4Bytes(bb));
		values.add(EthType.read2Bytes(bb));
		values.add(GenTableId.read2Bytes(bb));
		values.add(ICMPv4Code.readByte(bb));
		values.add(ICMPv4Type.readByte(bb));
		values.add(IpDscp.readByte(bb));
		values.add(IpProtocol.readByte(bb));
		values.add(IPv4Address.read4Bytes(bb));
		values.add(IPv6Address.read16Bytes(bb));
		values.add(IPv6FlowLabel.read4Bytes(bb));
		values.add(LagId.read4Bytes(bb));
		values.add(MacAddress.read6Bytes(bb));
		values.add(OFBitMask128.read16Bytes(bb));
		values.add(OFBitMask512.read64Bytes(bb));
		values.add(OFConnectionIndex.read4Bytes(bb));
		values.add(OFGroup.read4Bytes(bb));
		values.add(OFMetadata.read8Bytes(bb));
		values.add(OFPort.read4Bytes(bb));
		values.add(PacketType.read4Bytes(bb));
		values.add(TableId.readByte(bb));
		values.add(TransportPort.read2Bytes(bb));
		values.add(U128.read16Bytes(bb));
		values.add(U16.of(bb.readShort()));
		values.add(U32.of(bb.readInt()));
		values.add(U64.of(bb.readLong()));
		values.add(UDF.read4Bytes(bb));
		values.add(VFI.read2Bytes(bb));
		values.add(VRF.read4Bytes(bb));
	}

	/**
	 * Preconditions: All OFValueType ByteBuf read methods function correctly.
	 */
	@Test
	public void testWriteTo() {
		ByteBuf expected_stream = wrappedBuffer(byteStream);

		for (OFValueType<?> value : values) {
			// TODO: Get rid of the instanceof check once PacketType is fixed
			// (Issue #504)
			final int LEN = (value instanceof PacketType) ? 6 : value.getLength();
			byte[] expected = new byte[LEN];
			expected_stream.readBytes(expected);

			testSingleOFValueType(value, expected);
		}

		// OFBooleanValue special case: Squashes byte values to "1" or "0"
		testSingleOFValueType(
				OFBooleanValue.of(true),
				new byte[] { 1 });

		// Masked<V> special case... Constructor will apply the mask to the
		// value before storing, so we can't use arbitrary value/mask pairs
		// and expect the result to be the same.
		testSingleOFValueType(
				Masked.of(IPv4Address.of("16.17.0.0"), IPv4Address.of("255.255.0.0")),
				new byte[] { 0x10, 0x11, 0, 0, (byte) 0xff, (byte) 0xff, 0, 0 });

		// VxlanNI special case... Must comply with the mask.
		testSingleOFValueType(
				VxlanNI.ofVni(0x00fedcba),
				new byte[] { 0, (byte) 0xfe, (byte) 0xdc, (byte) 0xba });
	}

	protected void testSingleOFValueType(OFValueType<?> value, final byte[] expected) {
		// TODO: Get rid of the instanceof check once PacketType is fixed (Issue #504)
		final int LEN = (value instanceof PacketType) ? 6 : value.getLength();
		byte[] result = new byte[LEN];
		ByteBuf result_bb = wrappedBuffer(result).setIndex(0, 0);

		try {
			value.writeTo(result_bb);
		} catch (IndexOutOfBoundsException ex) {
			fail("Wrote more bytes than expected for class " + value.getClass().getSimpleName() + ": "
					+ ex.getMessage());
		}

		assertThat("Incorrect number of bytes written by " + value.getClass().getSimpleName() + ".writeTo(): "
				+ result_bb.readableBytes() + " (should be " + LEN + ")",
				result_bb.readableBytes(), is(LEN));
		assertThat("Bad result from " + value.getClass().getSimpleName() + ".writeTo(): " + Arrays.toString(result)
				+ " (should be " + Arrays.toString(expected) + ")",
				result, is(expected));
	}
}

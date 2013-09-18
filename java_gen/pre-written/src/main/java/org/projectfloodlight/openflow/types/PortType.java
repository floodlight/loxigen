package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.annotations.Immutable;

@Immutable
public enum PortType implements OFValueType<PortType> {
    PHYSICA((byte)0),
    LAG((byte)1);

    private final byte type;
    static final int LENGTH = 1;

    public static final PortType NO_MASK = PortType.of((byte)0xFF);
    public static final PortType FULL_MASK = PortType.of((byte)0x00);

    private PortType(byte type) {
        this.type = type;
    }

    public static PortType of(byte type) {
        switch (type) {
            case 0:
                return PHYSICA;
            case 1:
                return LAG;
            default:
                throw new IllegalArgumentException("Illegal Port Type: " + type);
        }
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    @Override
    public String toString() {
        return Integer.toHexString(type);
    }

    @Override
    public PortType applyMask(PortType mask) {
        return PortType.of((byte)(this.type & mask.type));
    }

    public void writeByte(ChannelBuffer c) {
        c.writeByte(this.type);
    }

    public static PortType readByte(ChannelBuffer c) throws OFParseError {
        return PortType.of((byte)(c.readUnsignedByte()));
    }

    public byte getPortType() {
        return type;
    }
}

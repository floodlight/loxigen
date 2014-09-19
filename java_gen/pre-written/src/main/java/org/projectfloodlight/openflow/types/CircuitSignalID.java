package org.projectfloodlight.openflow.types;

import com.google.common.collect.ComparisonChain;
import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;

import com.google.common.hash.PrimitiveSink;


public class CircuitSignalID implements OFValueType<CircuitSignalID> {
    static final int length = 6;

    private byte gridType;
    private byte channelSpacing ;
    private short channelNumber;
    private short spectralWidth;

    public static final CircuitSignalID NONE = new CircuitSignalID((byte)0,
                                                                   (byte)0,
                                                                   (short)0,
                                                                   (short)0);


    public CircuitSignalID(byte gridType, byte channelSpacing,
                            short channelNumber,
                            short spectralWidth)
    {
        this.gridType = gridType;
        this.channelSpacing = channelSpacing;
        this.channelNumber = channelNumber;
        this.spectralWidth = spectralWidth;

    }

    @Override
    public int getLength() {
        return length;
    }


    public void write6Bytes(ChannelBuffer c) {
        c.writeByte(gridType);
        c.writeByte(channelSpacing);
        c.writeShort(channelNumber);
        c.writeShort(spectralWidth);
    }

    public static CircuitSignalID read6Bytes(ChannelBuffer c) throws OFParseError {
        return new CircuitSignalID((byte)c.readUnsignedByte(),
                                   (byte)c.readUnsignedByte(),
                                   (short)c.readUnsignedShort(),
                                   (short)c.readUnsignedShort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircuitSignalID that = (CircuitSignalID) o;

        if (channelNumber != that.channelNumber) return false;
        if (channelSpacing != that.channelSpacing) return false;
        if (gridType != that.gridType) return false;
        if (spectralWidth != that.spectralWidth) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) gridType;
        result = 31 * result + (int) channelSpacing;
        result = 31 * result + (int) channelNumber;
        result = 31 * result + (int) spectralWidth;
        return result;
    }

    @Override
    public String toString() {
        return "CircuitSignalID{" +
                "gridType=" + gridType +
                ", channelSpacing=" + channelSpacing +
                ", channelNumber=" + channelNumber +
                ", spectralWidth=" + spectralWidth +
                '}';
    }

    @Override
    public CircuitSignalID applyMask(CircuitSignalID mask) {
        return new CircuitSignalID((byte) (this.gridType & mask.gridType),
                                   (byte) (this.channelSpacing & mask
                                           .channelSpacing),
                                   (short) (this.channelNumber & mask
                                           .channelNumber),
                                   (short) (this.spectralWidth & mask
                                           .spectralWidth));
    }


    @Override
    public int compareTo(CircuitSignalID o) {
        return ComparisonChain.start()
                              .compare(gridType,o.gridType)
                              .compare(channelSpacing,o.channelSpacing)
                              .compare(channelNumber,o.channelNumber)
                              .compare(spectralWidth,o.spectralWidth)
                              .result();
    }


    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putByte(gridType);
        sink.putByte(channelSpacing);
        sink.putShort(channelNumber);
        sink.putShort(spectralWidth);
    }



}

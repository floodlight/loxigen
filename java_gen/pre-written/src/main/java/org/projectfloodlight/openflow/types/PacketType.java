package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.UnsignedInts;

public class PacketType implements OFValueType<PacketType> {

    private final int namespace;
    private final int nsType;

    static final int IPv4_ns_type   = 0x800;
    static final int IPv6_ns_type   = 0x86dd;
    static final int Experimenter_ns_type   = 0xFFFF;


    public static final PacketType EthernetPacket = new PacketType(0,0);
    public static final PacketType IPv4Packet = new PacketType(0,IPv4_ns_type);
    public static final PacketType IPv6Packet = new PacketType(0,IPv6_ns_type);
    public static final PacketType NoPacket = new PacketType(0,1);
    public static final PacketType ExperimenterPacket = new PacketType(0,Experimenter_ns_type);

    public static final PacketType NO_MASK = PacketType.of(0xFFFF); //?
    public static final PacketType FULL_MASK = PacketType.of(0); //??

    private PacketType(int namespace, int nsType) {
        this.namespace = namespace;
        this.nsType = nsType;
    }

    public static PacketType of(int nstype) {
        switch (nstype) {
            case 0:
                return EthernetPacket;
            case IPv4_ns_type:
                return IPv4Packet;
            case IPv6_ns_type:
                return IPv6Packet;
            case 1:
                return NoPacket;
            case Experimenter_ns_type:
                return ExperimenterPacket;
            default:
                throw new IllegalArgumentException("Illegal ns type:" + nstype);
        }
    }

    @Override
    public PacketType applyMask(PacketType mask) {
        return null;
    }

    @Override
    public int compareTo(PacketType o) {
        return UnsignedInts.compare(this.nsType, o.nsType);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(nsType);
    }   
    
    @Override
    public int getLength() {
        //4 byte
        return 4;
    }

    public void write4Bytes(ByteBuf c) {
        c.writeInt(nsType);
    }

    public static PacketType read4Bytes(ByteBuf c) {
        return PacketType.of(c.readInt());
    }
}
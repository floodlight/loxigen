package org.projectfloodlight.openflow.types;

import org.projectfloodlight.openflow.protocol.OFHeaderTypeNamespace;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;

import io.netty.buffer.ByteBuf;

public class PacketType implements OFValueType<PacketType> {

    private final int namespace;
    private final int nsType;

    static final int NAMESPACE_ONF = 0;
    static final int NAMESPACE_ETHERTYPE = 1;

    static final int NS_TYPE_ONF_ETHERNET = 0;
    static final int NS_TYPE_ONF_NO_PACKET = 1;
    static final int NS_TYPE_ONF_EXPERIMENTER   = 0xFFFF;

    static final int NS_TYPE_ETHER_IPv4   = 0x800;
    static final int NS_TYPE_ETHER_IPv6   = 0x86dd;

    public static final PacketType ETHERNET = new PacketType(NAMESPACE_ONF, NS_TYPE_ONF_ETHERNET);
    public static final PacketType NO_PACKET = new PacketType(NAMESPACE_ONF, NS_TYPE_ONF_NO_PACKET);
    public static final PacketType EXPERIMENTER = new PacketType(NAMESPACE_ONF, NS_TYPE_ONF_EXPERIMENTER);

    public static final PacketType IPV4 = new PacketType(NAMESPACE_ETHERTYPE, NS_TYPE_ETHER_IPv4);
    public static final PacketType IPV6 = new PacketType(NAMESPACE_ETHERTYPE, NS_TYPE_ETHER_IPv6);

    public static final PacketType NO_MASK = PacketType.of(0xFFFF, 0x0FFFF);
    public static final PacketType FULL_MASK = PacketType.of(0, 0);

    private PacketType(int namespace, int nsType) {
        this.namespace = namespace;
        this.nsType = nsType;
    }

    public static PacketType of(int namespace, int nsType) {
        switch(namespace) {
            case NAMESPACE_ONF:
                switch (nsType) {
                    case NS_TYPE_ONF_ETHERNET:
                        return ETHERNET;
                    case NS_TYPE_ONF_NO_PACKET:
                        return NO_PACKET;
                    case NS_TYPE_ONF_EXPERIMENTER:
                        return EXPERIMENTER;
                }
                break;
            case NAMESPACE_ETHERTYPE:
                switch (nsType) {
                    case NS_TYPE_ETHER_IPv4:
                        return IPV4;
                    case NS_TYPE_ETHER_IPv6:
                        return IPV6;
                }
                break;
        }
        return new PacketType(namespace, nsType);
    }

    public static PacketType of(OFHeaderTypeNamespace namespace, int nsType) {
        return of(namespace.getStableValue(), nsType);

    }

    @Override
    public PacketType applyMask(PacketType mask) {
        return PacketType.of(this.namespace & mask.namespace, this.nsType & mask.nsType);
    }

    @Override
    public int compareTo(PacketType o) {
        int res = UnsignedInts.compare(this.namespace, o.namespace);
        if(res != 0)
            return res;

        return UnsignedInts.compare(this.nsType, o.nsType);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(namespace);
        sink.putInt(nsType);
    }

    @Override
    public int getLength() {
        //4 byte
        return 4;
    }

    public void write4Bytes(ByteBuf c) {
        c.writeShort(namespace);
        c.writeInt(nsType);
    }

    public static PacketType read4Bytes(ByteBuf c) {
        return PacketType.of(c.readShort(), c.readInt());
    }

    public int getNamespace() {
        return namespace;
    }

    public int getNsType() {
        return nsType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + namespace;
        result = prime * result + nsType;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PacketType other = (PacketType) obj;
        if (namespace != other.namespace)
            return false;
        if (nsType != other.nsType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PacketType [namespace=" + namespace + ", nsType=" + nsType + "]";
    }


}
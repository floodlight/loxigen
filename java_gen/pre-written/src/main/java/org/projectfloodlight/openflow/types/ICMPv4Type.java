package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class ICMPv4Type implements OFValueType<ICMPv4Type> {

    final static int LENGTH = 1;

    private static final short ICMPV4_TYPE_VAL_ECHO_REPLY    = 0;
    private static final short ICMPV4_TYPE_VAL_DESTINATION_UNREACHABLE   = 3;
    private static final short ICMPV4_TYPE_VAL_SOURCE_QUENCH = 4;
    private static final short ICMPV4_TYPE_VAL_REDIRECT  = 5;
    private static final short ICMPV4_TYPE_VAL_ALTERNATE_HOST_ADDRESS    = 6;
    private static final short ICMPV4_TYPE_VAL_ECHO  = 8;
    private static final short ICMPV4_TYPE_VAL_ROUTER_ADVERTISEMENT  = 9;
    private static final short ICMPV4_TYPE_VAL_ROUTER_SOLICITATION   = 10;
    private static final short ICMPV4_TYPE_VAL_TIME_EXCEEDED = 11;
    private static final short ICMPV4_TYPE_VAL_PARAMETER_PROBLEM = 12;
    private static final short ICMPV4_TYPE_VAL_TIMESTAMP = 13;
    private static final short ICMPV4_TYPE_VAL_TIMESTAMP_REPLY   = 14;
    private static final short ICMPV4_TYPE_VAL_INFORMATION_REQUEST   = 15;
    private static final short ICMPV4_TYPE_VAL_INFORMATION_REPLY = 16;
    private static final short ICMPV4_TYPE_VAL_ADDRESS_MASK_REQUEST  = 17;
    private static final short ICMPV4_TYPE_VAL_ADDRESS_MASK_REPLY    = 18;
    private static final short ICMPV4_TYPE_VAL_TRACEROUTE    = 30;
    private static final short ICMPV4_TYPE_VAL_DATAGRAM_CONVERSION_ERROR = 31;
    private static final short ICMPV4_TYPE_VAL_MOBILE_HOST_REDIRECT  = 32;
    private static final short ICMPV4_TYPE_VAL_IPV6_WHERE_ARE_YOU    = 33;
    private static final short ICMPV4_TYPE_VAL_IPV6_I_AM_HERE    = 34;
    private static final short ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REQUEST   = 35;
    private static final short ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REPLY = 36;
    private static final short ICMPV4_TYPE_VAL_DOMAIN_NAME_REQUEST   = 37;
    private static final short ICMPV4_TYPE_VAL_DOMAIN_NAME_REPLY = 38;
    private static final short ICMPV4_TYPE_VAL_SKIP  = 39;
    private static final short ICMPV4_TYPE_VAL_PHOTURIS  = 40;
    private static final short ICMPV4_TYPE_VAL_EXPERIMENTAL_MOBILITY = 41;

    public static final ICMPv4Type ICMPV4_TYPE_ECHO_REPLY   = new ICMPv4Type(ICMPV4_TYPE_VAL_ECHO_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_DESTINATION_UNREACHABLE  = new ICMPv4Type(ICMPV4_TYPE_VAL_DESTINATION_UNREACHABLE);
    public static final ICMPv4Type ICMPV4_TYPE_SOURCE_QUENCH    = new ICMPv4Type(ICMPV4_TYPE_VAL_SOURCE_QUENCH);
    public static final ICMPv4Type ICMPV4_TYPE_REDIRECT = new ICMPv4Type(ICMPV4_TYPE_VAL_REDIRECT);
    public static final ICMPv4Type ICMPV4_TYPE_ALTERNATE_HOST_ADDRESS   = new ICMPv4Type(ICMPV4_TYPE_VAL_ALTERNATE_HOST_ADDRESS);
    public static final ICMPv4Type ICMPV4_TYPE_ECHO = new ICMPv4Type(ICMPV4_TYPE_VAL_ECHO);
    public static final ICMPv4Type ICMPV4_TYPE_ROUTER_ADVERTISEMENT = new ICMPv4Type(ICMPV4_TYPE_VAL_ROUTER_ADVERTISEMENT);
    public static final ICMPv4Type ICMPV4_TYPE_ROUTER_SOLICITATION  = new ICMPv4Type(ICMPV4_TYPE_VAL_ROUTER_SOLICITATION);
    public static final ICMPv4Type ICMPV4_TYPE_TIME_EXCEEDED    = new ICMPv4Type(ICMPV4_TYPE_VAL_TIME_EXCEEDED);
    public static final ICMPv4Type ICMPV4_TYPE_PARAMETER_PROBLEM    = new ICMPv4Type(ICMPV4_TYPE_VAL_PARAMETER_PROBLEM);
    public static final ICMPv4Type ICMPV4_TYPE_TIMESTAMP    = new ICMPv4Type(ICMPV4_TYPE_VAL_TIMESTAMP);
    public static final ICMPv4Type ICMPV4_TYPE_TIMESTAMP_REPLY  = new ICMPv4Type(ICMPV4_TYPE_VAL_TIMESTAMP_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_INFORMATION_REQUEST  = new ICMPv4Type(ICMPV4_TYPE_VAL_INFORMATION_REQUEST);
    public static final ICMPv4Type ICMPV4_TYPE_INFORMATION_REPLY    = new ICMPv4Type(ICMPV4_TYPE_VAL_INFORMATION_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_ADDRESS_MASK_REQUEST = new ICMPv4Type(ICMPV4_TYPE_VAL_ADDRESS_MASK_REQUEST);
    public static final ICMPv4Type ICMPV4_TYPE_ADDRESS_MASK_REPLY   = new ICMPv4Type(ICMPV4_TYPE_VAL_ADDRESS_MASK_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_TRACEROUTE   = new ICMPv4Type(ICMPV4_TYPE_VAL_TRACEROUTE);
    public static final ICMPv4Type ICMPV4_TYPE_DATAGRAM_CONVERSION_ERROR    = new ICMPv4Type(ICMPV4_TYPE_VAL_DATAGRAM_CONVERSION_ERROR);
    public static final ICMPv4Type ICMPV4_TYPE_MOBILE_HOST_REDIRECT = new ICMPv4Type(ICMPV4_TYPE_VAL_MOBILE_HOST_REDIRECT);
    public static final ICMPv4Type ICMPV4_TYPE_IPV6_WHERE_ARE_YOU  = new ICMPv4Type(ICMPV4_TYPE_VAL_IPV6_WHERE_ARE_YOU);
    public static final ICMPv4Type ICMPV4_TYPE_IPV6_I_AM_HERE = new ICMPv4Type(ICMPV4_TYPE_VAL_IPV6_I_AM_HERE);
    public static final ICMPv4Type ICMPV4_TYPE_MOBILE_REGISTRATION_REQUEST  = new ICMPv4Type(ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REQUEST);
    public static final ICMPv4Type ICMPV4_TYPE_MOBILE_REGISTRATION_REPLY    = new ICMPv4Type(ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_DOMAIN_NAME_REQUEST  = new ICMPv4Type(ICMPV4_TYPE_VAL_DOMAIN_NAME_REQUEST);
    public static final ICMPv4Type ICMPV4_TYPE_DOMAIN_NAME_REPLY    = new ICMPv4Type(ICMPV4_TYPE_VAL_DOMAIN_NAME_REPLY);
    public static final ICMPv4Type ICMPV4_TYPE_SKIP = new ICMPv4Type(ICMPV4_TYPE_VAL_SKIP);
    public static final ICMPv4Type ICMPV4_TYPE_PHOTURIS = new ICMPv4Type(ICMPV4_TYPE_VAL_PHOTURIS);
    public static final ICMPv4Type ICMPV4_TYPE_EXPERIMENTAL_MOBILITY    = new ICMPv4Type(ICMPV4_TYPE_VAL_EXPERIMENTAL_MOBILITY);

    // HACK alert - we're disapproriating ICMPV4_TYPE_ECHO_REPLY (value 0) as 'none' as well
    public static final ICMPv4Type NONE   = ICMPV4_TYPE_ECHO_REPLY;

    public static final ICMPv4Type NO_MASK = new ICMPv4Type((short)0xFFFF);
    public static final ICMPv4Type FULL_MASK = new ICMPv4Type((short)0x0000);

    private final short type;

    private static final int MIN_TYPE = 0;
    private static final int MAX_TYPE = 0xFF;

    private ICMPv4Type(short type) {
        this.type = type;
    }

    public static ICMPv4Type of(short type) {
        if (type < MIN_TYPE || type > MAX_TYPE)
            throw new IllegalArgumentException("Invalid ICMPv4 type: " + type);
        switch (type) {
            case ICMPV4_TYPE_VAL_ECHO_REPLY:
                return ICMPV4_TYPE_ECHO_REPLY;
            case ICMPV4_TYPE_VAL_DESTINATION_UNREACHABLE:
                return ICMPV4_TYPE_DESTINATION_UNREACHABLE;
            case ICMPV4_TYPE_VAL_SOURCE_QUENCH:
                return ICMPV4_TYPE_SOURCE_QUENCH;
            case ICMPV4_TYPE_VAL_REDIRECT:
                return ICMPV4_TYPE_REDIRECT;
            case ICMPV4_TYPE_VAL_ALTERNATE_HOST_ADDRESS:
                return ICMPV4_TYPE_ALTERNATE_HOST_ADDRESS;
            case ICMPV4_TYPE_VAL_ECHO:
                return ICMPV4_TYPE_ECHO;
            case ICMPV4_TYPE_VAL_ROUTER_ADVERTISEMENT:
                return ICMPV4_TYPE_ROUTER_ADVERTISEMENT;
            case ICMPV4_TYPE_VAL_ROUTER_SOLICITATION:
                return ICMPV4_TYPE_ROUTER_SOLICITATION;
            case ICMPV4_TYPE_VAL_TIME_EXCEEDED:
                return ICMPV4_TYPE_TIME_EXCEEDED;
            case ICMPV4_TYPE_VAL_PARAMETER_PROBLEM:
                return ICMPV4_TYPE_PARAMETER_PROBLEM;
            case ICMPV4_TYPE_VAL_TIMESTAMP:
                return ICMPV4_TYPE_TIMESTAMP;
            case ICMPV4_TYPE_VAL_TIMESTAMP_REPLY:
                return ICMPV4_TYPE_TIMESTAMP_REPLY;
            case ICMPV4_TYPE_VAL_INFORMATION_REQUEST:
                return ICMPV4_TYPE_INFORMATION_REQUEST;
            case ICMPV4_TYPE_VAL_INFORMATION_REPLY:
                return ICMPV4_TYPE_INFORMATION_REPLY;
            case ICMPV4_TYPE_VAL_ADDRESS_MASK_REQUEST:
                return ICMPV4_TYPE_ADDRESS_MASK_REQUEST;
            case ICMPV4_TYPE_VAL_ADDRESS_MASK_REPLY:
                return ICMPV4_TYPE_ADDRESS_MASK_REPLY;
            case ICMPV4_TYPE_VAL_TRACEROUTE:
                return ICMPV4_TYPE_TRACEROUTE;
            case ICMPV4_TYPE_VAL_DATAGRAM_CONVERSION_ERROR:
                return ICMPV4_TYPE_DATAGRAM_CONVERSION_ERROR;
            case ICMPV4_TYPE_VAL_MOBILE_HOST_REDIRECT:
                return ICMPV4_TYPE_MOBILE_HOST_REDIRECT;
            case ICMPV4_TYPE_VAL_IPV6_WHERE_ARE_YOU:
                return ICMPV4_TYPE_IPV6_WHERE_ARE_YOU;
            case ICMPV4_TYPE_VAL_IPV6_I_AM_HERE:
                return ICMPV4_TYPE_IPV6_I_AM_HERE;
            case ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REQUEST:
                return ICMPV4_TYPE_MOBILE_REGISTRATION_REQUEST;
            case ICMPV4_TYPE_VAL_MOBILE_REGISTRATION_REPLY:
                return ICMPV4_TYPE_MOBILE_REGISTRATION_REPLY;
            case ICMPV4_TYPE_VAL_DOMAIN_NAME_REQUEST:
                return ICMPV4_TYPE_DOMAIN_NAME_REQUEST;
            case ICMPV4_TYPE_VAL_DOMAIN_NAME_REPLY:
                return ICMPV4_TYPE_DOMAIN_NAME_REPLY;
            case ICMPV4_TYPE_VAL_SKIP:
                return ICMPV4_TYPE_SKIP;
            case ICMPV4_TYPE_VAL_PHOTURIS:
                return ICMPV4_TYPE_PHOTURIS;
            case ICMPV4_TYPE_VAL_EXPERIMENTAL_MOBILITY:
                return ICMPV4_TYPE_EXPERIMENTAL_MOBILITY;
            default:
                return new ICMPv4Type(type);
        }
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    public short getType() {
        return type;
    }

    public void writeByte(ChannelBuffer c) {
        c.writeByte(this.type);
    }

    public static ICMPv4Type readByte(ChannelBuffer c) {
        return ICMPv4Type.of(c.readUnsignedByte());
    }

    @Override
    public ICMPv4Type applyMask(ICMPv4Type mask) {
        return ICMPv4Type.of((short)(this.type & mask.type));
    }


}

package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

public class ArpOpcode implements OFValueType<ArpOpcode> {

    final static int LENGTH = 2;

    private static final int ARP_OPCODE_VAL_REQUEST   = 1;
    private static final int ARP_OPCODE_VAL_REPLY = 2;
    private static final int ARP_OPCODE_VAL_REQUEST_REVERSE   = 3;
    private static final int ARP_OPCODE_VAL_REPLY_REVERSE = 4;
    private static final int ARP_OPCODE_VAL_DRARP_REQUEST = 5;
    private static final int ARP_OPCODE_VAL_DRARP_REPLY   = 6;
    private static final int ARP_OPCODE_VAL_DRARP_ERROR   = 7;
    private static final int ARP_OPCODE_VAL_INARP_REQUEST = 8;
    private static final int ARP_OPCODE_VAL_INARP_REPLY   = 9;
    private static final int ARP_OPCODE_VAL_ARP_NAK   = 10;
    private static final int ARP_OPCODE_VAL_MARS_REQUEST  = 11;
    private static final int ARP_OPCODE_VAL_MARS_MULTI    = 12;
    private static final int ARP_OPCODE_VAL_MARS_MSERV    = 13;
    private static final int ARP_OPCODE_VAL_MARS_JOIN = 14;
    private static final int ARP_OPCODE_VAL_MARS_LEAVE    = 15;
    private static final int ARP_OPCODE_VAL_MARS_NAK  = 16;
    private static final int ARP_OPCODE_VAL_MARS_UNSERV   = 17;
    private static final int ARP_OPCODE_VAL_MARS_SJOIN    = 18;
    private static final int ARP_OPCODE_VAL_MARS_SLEAVE   = 19;
    private static final int ARP_OPCODE_VAL_MARS_GROUPLIST_REQUEST    = 20;
    private static final int ARP_OPCODE_VAL_MARS_GROUPLIST_REPLY  = 21;
    private static final int ARP_OPCODE_VAL_MARS_REDIRECT_MAP = 22;
    private static final int ARP_OPCODE_VAL_MAPOS_UNARP   = 23;
    private static final int ARP_OPCODE_VAL_OP_EXP1   = 24;
    private static final int ARP_OPCODE_VAL_OP_EXP2   = 25;

    public static final ArpOpcode ARP_OPCODE_REQUEST  = new ArpOpcode(ARP_OPCODE_VAL_REQUEST);
    public static final ArpOpcode ARP_OPCODE_REPLY    = new ArpOpcode(ARP_OPCODE_VAL_REPLY);
    public static final ArpOpcode ARP_OPCODE_REQUEST_REVERSE  = new ArpOpcode(ARP_OPCODE_VAL_REQUEST_REVERSE);
    public static final ArpOpcode ARP_OPCODE_REPLY_REVERSE    = new ArpOpcode(ARP_OPCODE_VAL_REPLY_REVERSE);
    public static final ArpOpcode ARP_OPCODE_DRARP_REQUEST    = new ArpOpcode(ARP_OPCODE_VAL_DRARP_REQUEST);
    public static final ArpOpcode ARP_OPCODE_DRARP_REPLY  = new ArpOpcode(ARP_OPCODE_VAL_DRARP_REPLY);
    public static final ArpOpcode ARP_OPCODE_DRARP_ERROR  = new ArpOpcode(ARP_OPCODE_VAL_DRARP_ERROR);
    public static final ArpOpcode ARP_OPCODE_INARP_REQUEST    = new ArpOpcode(ARP_OPCODE_VAL_INARP_REQUEST);
    public static final ArpOpcode ARP_OPCODE_INARP_REPLY  = new ArpOpcode(ARP_OPCODE_VAL_INARP_REPLY);
    public static final ArpOpcode ARP_OPCODE_ARP_NAK  = new ArpOpcode(ARP_OPCODE_VAL_ARP_NAK);
    public static final ArpOpcode ARP_OPCODE_MARS_REQUEST = new ArpOpcode(ARP_OPCODE_VAL_MARS_REQUEST);
    public static final ArpOpcode ARP_OPCODE_MARS_MULTI   = new ArpOpcode(ARP_OPCODE_VAL_MARS_MULTI);
    public static final ArpOpcode ARP_OPCODE_MARS_MSERV   = new ArpOpcode(ARP_OPCODE_VAL_MARS_MSERV);
    public static final ArpOpcode ARP_OPCODE_MARS_JOIN    = new ArpOpcode(ARP_OPCODE_VAL_MARS_JOIN);
    public static final ArpOpcode ARP_OPCODE_MARS_LEAVE   = new ArpOpcode(ARP_OPCODE_VAL_MARS_LEAVE);
    public static final ArpOpcode ARP_OPCODE_MARS_NAK = new ArpOpcode(ARP_OPCODE_VAL_MARS_NAK);
    public static final ArpOpcode ARP_OPCODE_MARS_UNSERV  = new ArpOpcode(ARP_OPCODE_VAL_MARS_UNSERV);
    public static final ArpOpcode ARP_OPCODE_MARS_SJOIN   = new ArpOpcode(ARP_OPCODE_VAL_MARS_SJOIN);
    public static final ArpOpcode ARP_OPCODE_MARS_SLEAVE  = new ArpOpcode(ARP_OPCODE_VAL_MARS_SLEAVE);
    public static final ArpOpcode ARP_OPCODE_MARS_GROUPLIST_REQUEST   = new ArpOpcode(ARP_OPCODE_VAL_MARS_GROUPLIST_REQUEST);
    public static final ArpOpcode ARP_OPCODE_MARS_GROUPLIST_REPLY = new ArpOpcode(ARP_OPCODE_VAL_MARS_GROUPLIST_REPLY);
    public static final ArpOpcode ARP_OPCODE_MARS_REDIRECT_MAP    = new ArpOpcode(ARP_OPCODE_VAL_MARS_REDIRECT_MAP);
    public static final ArpOpcode ARP_OPCODE_MAPOS_UNARP  = new ArpOpcode(ARP_OPCODE_VAL_MAPOS_UNARP);
    public static final ArpOpcode ARP_OPCODE_OP_EXP1  = new ArpOpcode(ARP_OPCODE_VAL_OP_EXP1);
    public static final ArpOpcode ARP_OPCODE_OP_EXP2  = new ArpOpcode(ARP_OPCODE_VAL_OP_EXP2);

    private static final int MIN_OPCODE = 0;
    private static final int MAX_OPCODE = 0xFFFF;
    
    public static final ArpOpcode FULL_MASK = new ArpOpcode(0xFFFFFFFF);
    public static final ArpOpcode NO_MASK = new ArpOpcode(0x00000000);

    private final int opcode;

    private ArpOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public static ArpOpcode of(int opcode) {
        if (opcode < MIN_OPCODE || opcode > MAX_OPCODE)
            throw new IllegalArgumentException("Invalid ARP opcode: " + opcode);
        switch (opcode) {
            case ARP_OPCODE_VAL_REQUEST:
                return ARP_OPCODE_REQUEST;
            case ARP_OPCODE_VAL_REPLY:
                return ARP_OPCODE_REPLY;
            case ARP_OPCODE_VAL_REQUEST_REVERSE:
                return ARP_OPCODE_REQUEST_REVERSE;
            case ARP_OPCODE_VAL_REPLY_REVERSE:
                return ARP_OPCODE_REPLY_REVERSE;
            case ARP_OPCODE_VAL_DRARP_REQUEST:
                return ARP_OPCODE_DRARP_REQUEST;
            case ARP_OPCODE_VAL_DRARP_REPLY:
                return ARP_OPCODE_DRARP_REPLY;
            case ARP_OPCODE_VAL_DRARP_ERROR:
                return ARP_OPCODE_DRARP_ERROR;
            case ARP_OPCODE_VAL_INARP_REQUEST:
                return ARP_OPCODE_INARP_REQUEST;
            case ARP_OPCODE_VAL_INARP_REPLY:
                return ARP_OPCODE_INARP_REPLY;
            case ARP_OPCODE_VAL_ARP_NAK:
                return ARP_OPCODE_ARP_NAK;
            case ARP_OPCODE_VAL_MARS_REQUEST:
                return ARP_OPCODE_MARS_REQUEST;
            case ARP_OPCODE_VAL_MARS_MULTI:
                return ARP_OPCODE_MARS_MULTI;
            case ARP_OPCODE_VAL_MARS_MSERV:
                return ARP_OPCODE_MARS_MSERV;
            case ARP_OPCODE_VAL_MARS_JOIN:
                return ARP_OPCODE_MARS_JOIN;
            case ARP_OPCODE_VAL_MARS_LEAVE:
                return ARP_OPCODE_MARS_LEAVE;
            case ARP_OPCODE_VAL_MARS_NAK:
                return ARP_OPCODE_MARS_NAK;
            case ARP_OPCODE_VAL_MARS_UNSERV:
                return ARP_OPCODE_MARS_UNSERV;
            case ARP_OPCODE_VAL_MARS_SJOIN:
                return ARP_OPCODE_MARS_SJOIN;
            case ARP_OPCODE_VAL_MARS_SLEAVE:
                return ARP_OPCODE_MARS_SLEAVE;
            case ARP_OPCODE_VAL_MARS_GROUPLIST_REQUEST:
                return ARP_OPCODE_MARS_GROUPLIST_REQUEST;
            case ARP_OPCODE_VAL_MARS_GROUPLIST_REPLY:
                return ARP_OPCODE_MARS_GROUPLIST_REPLY;
            case ARP_OPCODE_VAL_MARS_REDIRECT_MAP:
                return ARP_OPCODE_MARS_REDIRECT_MAP;
            case ARP_OPCODE_VAL_MAPOS_UNARP:
                return ARP_OPCODE_MAPOS_UNARP;
            case ARP_OPCODE_VAL_OP_EXP1:
                return ARP_OPCODE_OP_EXP1;
            case ARP_OPCODE_VAL_OP_EXP2:
                return ARP_OPCODE_OP_EXP2;
            default:
                return new ArpOpcode(opcode);
        }
    }
    
    public void write2Bytes(ChannelBuffer c) {
        c.writeShort(this.opcode);
    }
    
    public static ArpOpcode read2Bytes(ChannelBuffer c) {
        return ArpOpcode.of(c.readUnsignedShort());
    }

    @Override
    public ArpOpcode applyMask(ArpOpcode mask) {
        return ArpOpcode.of(this.opcode & mask.opcode);
    }
    
    public int getOpCode() {
        return opcode;
    }
    
}
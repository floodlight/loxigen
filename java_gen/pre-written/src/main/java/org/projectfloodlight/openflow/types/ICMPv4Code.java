package org.projectfloodlight.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 *
 */
public class ICMPv4Code implements OFValueType<ICMPv4Code> {

    final static int LENGTH = 1;
    final static short MAX_CODE = 0xFF;

    private final short code;
    
    public static final ICMPv4Code NO_MASK = new ICMPv4Code((short)0xFFFF);
    public static final ICMPv4Code FULL_MASK = new ICMPv4Code((short)0x0000);

    private ICMPv4Code(short code) {
        this.code = code;
    }

    public static ICMPv4Code of(short code) {
        if (code > MAX_CODE || code < 0)
            throw new IllegalArgumentException("Illegal ICMPv4 code: " + code);
        return new ICMPv4Code(code);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }
    
    public short getCode() {
        return code;
    }
    
    public void writeByte(ChannelBuffer c) {
        c.writeByte(this.code);
    }
    
    public static ICMPv4Code readByte(ChannelBuffer c) {
        return ICMPv4Code.of(c.readUnsignedByte());
    }

    @Override
    public ICMPv4Code applyMask(ICMPv4Code mask) {
        return ICMPv4Code.of((short)(this.code & mask.code));
    }

    
}

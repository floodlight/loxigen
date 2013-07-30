package org.openflow.types;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 *
 */
public class ICMPv4Code implements OFValueType {

    final static int LENGTH = 1;
    final static short MAX_CODE = 0xFF;

    private final short code;

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

}

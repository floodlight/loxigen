package org.projectfloodlight.openflow.types;


public class OFPortBitMap extends Masked<OFBitMask128> {

    private OFPortBitMap(OFBitMask128 mask) {
        super(OFBitMask128.NONE, mask);
    }

    public boolean isOn(OFPort port) {
        return !(this.mask.isOn(port.getPortNumber()));
    }

    public static OFPortBitMap ofPorts(OFPort... ports) {
        Builder builder = new Builder();
        for (OFPort port: ports) {
            builder.set(port);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OFPortBitMap))
            return false;
        OFPortBitMap other = (OFPortBitMap)obj;
        return (other.value.equals(this.value) && other.mask.equals(this.mask));
    }

    @Override
    public int hashCode() {
        return 619 * mask.hashCode() + 257 * value.hashCode();
    }

    public static class Builder {
        private long raw1 = -1, raw2 = -1;

        public Builder() {

        }

        public boolean isOn(OFPort port) {
            return !(OFBitMask128.isBitOn(raw1, raw2, port.getPortNumber()));
        }

        public Builder unset(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 127) // MAX PORT IS 127
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw2 |= ((long)1 << bit);
            } else {
                raw1 |= ((long)1 << (bit - 64));
            }
            return this;
        }

        public Builder set(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 127)
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw2 &= ~((long)1 << bit);
            } else {
                raw1 &= ~((long)1 << (bit - 64));
            }
            return this;
        }

        public OFPortBitMap build() {
            return new OFPortBitMap(OFBitMask128.of(raw1, raw2));
        }
    }

}

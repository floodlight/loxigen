package org.projectfloodlight.openflow.types;


public class OFPortMap extends Masked<OFPortBitmap> {

    private OFPortMap(OFPortBitmap mask) {
        super(OFPortBitmap.NONE, mask);
    }

    public boolean isOn(OFPort port) {
        return this.mask.isOn(port);
    }

    public static OFPortMap ofPorts(OFPort... ports) {
        Builder builder = new Builder();
        for (OFPort port: ports) {
            builder.set(port);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OFPortMap))
            return false;
        OFPortMap other = (OFPortMap)obj;
        return (other.value.equals(this.value) && other.mask.equals(this.mask));
    }

    @Override
    public int hashCode() {
        return 619 * mask.hashCode() + 257 * value.hashCode();
    }

    public static class Builder {
        private long raw1, raw2;

        public Builder() {

        }

        public boolean isOn(OFPort port) {
            return OFPortBitmap.isBitOn(raw1, raw2, port.getPortNumber());
        }

        public Builder set(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 128)
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw2 |= ((long)1 << bit);
            } else {
                raw1 |= ((long)1 << (bit - 64));
            }
            return this;
        }

        public Builder unset(OFPort port) {
            int bit = port.getPortNumber();
            if (bit < 0 || bit >= 128)
                throw new IndexOutOfBoundsException("Port number is out of bounds");
            if (bit < 64) {
                raw2 &= ~((long)1 << bit);
            } else {
                raw1 &= ~((long)1 << (bit - 64));
            }
            return this;
        }

        public OFPortMap build() {
            return new OFPortMap(OFPortBitmap.of(raw1, raw2));
        }
    }

}

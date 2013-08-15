package org.openflow.types;

public class IPv4WithMask extends Masked<IPv4> {

    private IPv4WithMask(int rawValue, int rawMask) {
        super(IPv4.of(rawValue), IPv4.of(rawMask));
    }
    
    private IPv4WithMask(IPv4 value, IPv4 mask) {
        super(value, mask);
    }
    
    public static IPv4WithMask of(int rawValue, int rawMask) {
        return new IPv4WithMask(rawValue, rawMask);
    }
    
    public static IPv4WithMask of(IPv4 value, IPv4 mask) {
        return new IPv4WithMask(value, mask);
    }
    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(((IPv4)value).toString());
        
        int maskint = ((IPv4)mask).getInt();
        res.append('/');
        if (Integer.bitCount((~maskint) + 1) == 1) {
            // CIDR notation
            res.append(Integer.bitCount(maskint));
        } else {
            // Full address mask
            res.append(((IPv4)mask).toString());
        }
        
        return res.toString();
    }
    
    public static IPv4WithMask of(final String string) {
        int slashPos;
        String ip = string;
        int maskBits = 0;
        IPv4 maskAddress = null;

        // Read mask suffix
        if ((slashPos = string.indexOf('/')) != -1) {
            ip = string.substring(0, slashPos);
            try {
                String suffix = string.substring(slashPos + 1);
                if (suffix.length() == 0)
                    throw new IllegalArgumentException("IP Address not well formed: " + string);
                if (suffix.indexOf('.') != -1) {
                    // Full mask
                    maskAddress = IPv4.of(suffix);
                } else {
                    // CIDR Suffix
                    maskBits = Integer.parseInt(suffix);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
            if (maskBits < 0 || maskBits > 32) {
                throw new IllegalArgumentException("IP Address not well formed: " + string);
            }
        }
        
        // Read IP
        IPv4 ipv4 = IPv4.of(ip);
        
        if (maskAddress != null) {
            // Full address mask
            return IPv4WithMask.of(ipv4, maskAddress);
        } else if (maskBits == 0) {
            // No mask
            return IPv4WithMask.of(ipv4, IPv4.of(0xFFFFFFFF));
        } else {
            // With mask
            int mask = (-1) << (32 - maskBits);
            return IPv4WithMask.of(ipv4, IPv4.of(mask));
        }
    }

}

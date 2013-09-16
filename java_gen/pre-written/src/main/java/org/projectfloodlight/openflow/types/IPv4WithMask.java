package org.projectfloodlight.openflow.types;

public class IPv4WithMask extends Masked<IPv4Address> {

    private IPv4WithMask(int rawValue, int rawMask) {
        super(IPv4Address.of(rawValue), IPv4Address.of(rawMask));
    }
    
    private IPv4WithMask(IPv4Address value, IPv4Address mask) {
        super(value, mask);
    }
    
    public static IPv4WithMask of(int rawValue, int rawMask) {
        return new IPv4WithMask(rawValue, rawMask);
    }
    
    public static IPv4WithMask of(IPv4Address value, IPv4Address mask) {
        return new IPv4WithMask(value, mask);
    }
    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(((IPv4Address)value).toString());
        
        int maskint = ((IPv4Address)mask).getInt();
        res.append('/');
        if (Integer.bitCount((~maskint) + 1) == 1) {
            // CIDR notation
            res.append(Integer.bitCount(maskint));
        } else {
            // Full address mask
            res.append(((IPv4Address)mask).toString());
        }
        
        return res.toString();
    }
    
    public static IPv4WithMask of(final String string) {
        int slashPos;
        String ip = string;
        int maskBits = 0;
        IPv4Address maskAddress = null;

        // Read mask suffix
        if ((slashPos = string.indexOf('/')) != -1) {
            ip = string.substring(0, slashPos);
            try {
                String suffix = string.substring(slashPos + 1);
                if (suffix.length() == 0)
                    throw new IllegalArgumentException("IP Address not well formed: " + string);
                if (suffix.indexOf('.') != -1) {
                    // Full mask
                    maskAddress = IPv4Address.of(suffix);
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
        IPv4Address ipv4 = IPv4Address.of(ip);
        
        if (maskAddress != null) {
            // Full address mask
            return IPv4WithMask.of(ipv4, maskAddress);
        } else if (maskBits == 0) {
            // No mask
            return IPv4WithMask.of(ipv4, IPv4Address.NO_MASK);
        } else {
            // With mask
            int mask = (-1) << (32 - maskBits);
            return IPv4WithMask.of(ipv4, IPv4Address.of(mask));
        }
    }

}

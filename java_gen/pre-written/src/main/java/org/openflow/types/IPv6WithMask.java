package org.openflow.types;

import java.math.BigInteger;
import java.util.Arrays;

public class IPv6WithMask extends Masked<IPv6> {

    private IPv6WithMask(IPv6 value, IPv6 mask) {
        super(value, mask);
    }
    
    public static IPv6WithMask of(IPv6 value, IPv6 mask) {
        return new IPv6WithMask(value, mask);
    }
    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(((IPv6)value).toString());
        res.append('/');
        
        BigInteger maskint = new BigInteger(((IPv6)mask).getBytes());
        if (maskint.not().add(BigInteger.ONE).bitCount() == 1) {
            // CIDR notation
            res.append(maskint.bitCount());
        } else {
            // Full address mask
            res.append(((IPv6)mask).toString());
        }
        
        return res.toString();
    }
    
    public static OFValueType ofPossiblyMasked(final String string) {
        int slashPos;
        String ip = string;
        int maskBits = 0;
        IPv6 maskAddress = null;

        // Read mask suffix
        if ((slashPos = string.indexOf('/')) != -1) {
            ip = string.substring(0, slashPos);
            try {
                String suffix = string.substring(slashPos + 1);
                if (suffix.length() == 0)
                    throw new IllegalArgumentException("IPv6 Address not well formed: " + string);
                if (suffix.indexOf(':') != -1) {
                    // Full mask
                    maskAddress = IPv6.of(suffix);
                } else {
                    // CIDR Suffix
                    maskBits = Integer.parseInt(suffix);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("IPv6 Address not well formed: " + string);
            }
            if (maskBits < 0 || maskBits > 128) {
                throw new IllegalArgumentException("IPv6 Address not well formed: " + string);
            }
        }
        
        // Read IP
        IPv6 ipv6 = IPv6.of(ip);
        
        if (maskAddress != null) {
            // Full address mask
            return IPv6WithMask.of(ipv6, maskAddress);
        } else if (maskBits == 0) {
            // No mask
            return ipv6;
        } else {
            // With mask
            BigInteger mask = BigInteger.ONE.negate().shiftLeft(128 - maskBits);
            byte[] maskBytesTemp = mask.toByteArray();
            byte[] maskBytes;
            if (maskBytesTemp.length < 16) {
                maskBytes = new byte[16];
                System.arraycopy(maskBytesTemp, 0, maskBytes, 16 - maskBytesTemp.length, maskBytesTemp.length);
                Arrays.fill(maskBytes, 0, 16 - maskBytesTemp.length, (byte)(0xFF));
            } else if (maskBytesTemp.length > 16) {
                maskBytes = new byte[16];
                System.arraycopy(maskBytesTemp, 0, maskBytes, 0, maskBytes.length);
            } else {
                maskBytes = maskBytesTemp;
            }
            return IPv6WithMask.of(ipv6, IPv6.of(maskBytes));
        }
    }


}

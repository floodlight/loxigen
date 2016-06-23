package org.projectfloodlight.openflow.util;

import io.netty.util.internal.EmptyArrays;

/** Utility method to convert hexadecimal string from/to longs and byte arrays.
 *
 * @author Andreas Wundsam {@literal <}andreas.wundsam@bigswitch.com{@literal >}
 */
public final class HexString {

    private HexString() {}

    /* ================================================================================
     * Implementation note:
     * These implementations are optimized for small-O efficiency and thus rather ugly.
     *
     * When making changes, make sure *every line* is covered by a unit test.
     *
     * Do not use this as a blue print for normal code.
     * ================================================================================
     */

    private final static char[] CHARS =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Convert a string of bytes to a ':' separated hex string
     *
     * @param bytes the byte[] to convert
     * @return "0f:ca:fe:de:ad:be:ef"
     */
    public static String toHexString(final byte[] bytes) {
        int lenBytes = bytes.length;
        if (lenBytes == 0) {
            return "";
        }
        char arr[] = new char[lenBytes * 2 + (lenBytes -1)];

        int charPos = 0;
        int i = 0;

        for (;;) {
            arr[charPos++] = CHARS[ (bytes[i] >>> 4) & 0xF ];
            arr[charPos++] = CHARS[ bytes[i] & 0xF ];
            if (++i >= lenBytes) {
                break;
            }
            arr[charPos++] = ':';
        }

        return new String(arr, 0, arr.length);
    }

    public static String toHexString(long val, final int padTo) {
        int valBytes = (64 - Long.numberOfLeadingZeros(val) + 7)/8;
        int lenBytes = valBytes > padTo ? valBytes : padTo;

        char arr[] = new char[lenBytes * 2 + (lenBytes -1)];

        // fill char array from the last position, shifting val by 4 bits
        for (int charPos = arr.length - 1; charPos >= 0; charPos--) {
            if ((charPos + 1) % 3 == 0) {
                // every third char is a colon
                arr[charPos] = ':';
            } else {
                arr[charPos] = CHARS[((int) val) & 0xF];
                val >>>= 4;
            }
        }
        return new String(arr, 0, arr.length);
    }

    public static String toHexString(final long val) {
        return toHexString(val, 8);
    }


    /** Deprecated version of {@link #toBytes(String)}.
     *
     * @throws NumberFormatException upon values parse error
     * @param values the hexstring to parse into a byte[]
     * @return a byte[] representing the hexstring
     * {@link Deprecated} because of inconsistent naming
     */
    @Deprecated
    public static byte[] fromHexString(final String values) throws NumberFormatException {
        return toBytes(values);
    }

    /** state constants for toBytes */
    /** expecting first digit */
    private final static int FIRST_DIGIT = 1;
    /** expecting second digit or colon */
    private static final int SECOND_DIGIT_OR_COLON = 2;
    /** expecting colon */
    private static final int COLON = 3;
    /** save byte and move back to FIRST_DIGIT */
    private static final int SAVE_BYTE = 4;

    /**
     * Convert a string of hex values into a string of bytes.
     *
     * @param values
     *            "0f:ca:fe:de:ad:be:ef"
     * @return [15, 5 ,2, 5, 17]
     * @throws NumberFormatException
     *             If the string can not be parsed
     */
    public static byte[] toBytes(final String values) throws NumberFormatException {
        int start = 0;
        int len = values.length();

        if (len == 0) {
            return EmptyArrays.EMPTY_BYTES;
        }

        int numColons = 0;
        for (int i=0; i < len; i++) {
            if (values.charAt(i) == ':') {
                numColons++;
            }
        }

        byte[] res = new byte[numColons+1];
        int pos = 0;

        int state = FIRST_DIGIT;

        byte b = 0;
        while (start < len) {
            char c = values.charAt(start++);
            switch (state) {
                case FIRST_DIGIT:
                    int digit = Character.digit(c, 16);
                    if(digit < 0) {
                        throw new NumberFormatException("Invalid char at index " + start + ": " + values);
                    }
                    b = (byte) digit;
                    state = start < len ? SECOND_DIGIT_OR_COLON : SAVE_BYTE;
                    break;
                case SECOND_DIGIT_OR_COLON:
                    if(c != ':') {
                        int digit2 = Character.digit(c, 16);
                        if(digit2 < 0) {
                            throw new NumberFormatException("Invalid char at index " + start + ": " + values);
                        }
                        b =  (byte) ((b<<4) | digit2);
                        state = start < len ? COLON : SAVE_BYTE;
                    } else {
                        state = SAVE_BYTE;
                    }
                    break;
                case COLON:
                    if(c != ':') {
                        throw new NumberFormatException("Separator expected at index " + start + ": " + values);
                    }
                    state = SAVE_BYTE;
                    break;
                default:
                    throw new IllegalStateException("Should not be in state " + state);
            }
            if (state == SAVE_BYTE) {
                res[pos++] = b;
                b = 0;
                state = FIRST_DIGIT;
            }
        }
        if (pos != res.length) {
            // detects a mal-formed input string, e.g., "01:02:"
            throw new NumberFormatException("Invalid hex string: " + values);
        }

        return res;
    }

    public static long toLong(String value) throws NumberFormatException {
        int shift = 0;
        long result = 0L;

        int sinceLastSeparator = 0;
        for (int charPos=value.length() - 1; charPos >= 0; charPos--) {
            char c = value.charAt(charPos);
            if (c == ':') {
                if (sinceLastSeparator == 0) {
                    throw new NumberFormatException("Expected hex digit at index " + charPos +": " + value);
                } else if(sinceLastSeparator == 1) {
                    shift += 4;
                }
                sinceLastSeparator = 0;
            } else {
                int digit = Character.digit(c, 16);
                if (digit < 0) {
                    throw new NumberFormatException("Invalid hex digit at index " + charPos +": " + value);
                }
                result |= ((long) digit) << shift;
                shift +=4;
                sinceLastSeparator++;
                if (sinceLastSeparator > 2) {
                    throw new NumberFormatException("Expected colon at index " + charPos +": " + value);
                }
            }
            if (shift > 64) {
                throw new NumberFormatException("Too many bytes in hex string to convert to long: " + value);
            }
        }
        return result;
    }
}

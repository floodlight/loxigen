package org.projectfloodlight.openflow.types;

import com.google.common.base.Preconditions;

public class HashValueUtils {
    private HashValueUtils() { }

    public static long combineWithValue(long key, long value, int keyBits) {
        Preconditions.checkArgument(keyBits >= 0 && keyBits <= 64, "keyBits must be [0,64]");

        int valueBits = 64 - keyBits;
        long valueMask = valueBits == 64 ? 0xFFFFFFFFFFFFFFFFL : (1L << valueBits) - 1;

        return key ^ (value & valueMask);
    }

}

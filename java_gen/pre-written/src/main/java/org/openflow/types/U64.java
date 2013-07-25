/**
 *    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
 *    University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package org.openflow.types;

import java.math.BigInteger;

public class U64 {
    private static final long UNSIGNED_MASK = 0x7fffffffffffffffL;

    private final long raw;

    private U64(final long raw) {
        this.raw = raw;
    }

    public static U64 of(final long raw) {
        return new U64(raw);
    }

    public long getValue() {
        return raw;
    }

    public BigInteger getBigInteger() {
        BigInteger bigInt = BigInteger.valueOf(raw & UNSIGNED_MASK);
        if (raw < 0) {
          bigInt = bigInt.setBit(Long.SIZE - 1);
        }
        return bigInt;
    }

    @Override
    public String toString() {
        return getBigInteger().toString();
    }

    public static BigInteger f(final long value) {
        BigInteger bigInt = BigInteger.valueOf(value & UNSIGNED_MASK);
        if (value < 0) {
          bigInt = bigInt.setBit(Long.SIZE - 1);
        }
        return bigInt;
    }

    public static long t(final BigInteger l) {
        return l.longValue();
    }
}

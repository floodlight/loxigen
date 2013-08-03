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

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.exceptions.OFParseError;
import org.openflow.protocol.OFMessageReader;
import org.openflow.protocol.Writeable;

public class U32 implements Writeable {
    private final int raw;

    private U32(int raw) {
        this.raw = raw;
    }

    public static U32 of(long value) {
        return new U32(U32.t(value));
    }

    public static U32 ofRaw(int value) {
        return new U32(value);
    }

    long getValue() {
        return f(raw);
    }

    int getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "" + f(raw);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + raw;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        U32 other = (U32) obj;
        if (raw != other.raw)
            return false;
        return true;
    }

    public static long f(final int i) {
        return i & 0xffffffffL;
    }

    public static int t(final long l) {
        return (int) l;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeInt(raw);
    }

    public final static Reader READER = new Reader();

    private static class Reader implements OFMessageReader<U32> {
        @Override
        public U32 readFrom(ChannelBuffer bb) throws OFParseError {
            return new U32(bb.readInt());
        }
    }

}

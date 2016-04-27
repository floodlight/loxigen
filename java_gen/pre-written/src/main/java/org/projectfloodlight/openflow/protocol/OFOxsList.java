package org.projectfloodlight.openflow.protocol;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.oxs.OFOxs;
import org.projectfloodlight.openflow.protocol.stat.StatField;
import org.projectfloodlight.openflow.protocol.stat.StatFields;
import org.projectfloodlight.openflow.types.OFValueType;
import org.projectfloodlight.openflow.types.PrimitiveSinkable;
import org.projectfloodlight.openflow.util.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.PrimitiveSink;

import io.netty.buffer.ByteBuf;


public class OFOxsList implements Iterable<OFOxs<?>>, Writeable, PrimitiveSinkable {
    private static final Logger logger = LoggerFactory.getLogger(OFOxsList.class);

    private final Map<StatFields, OFOxs<?>> oxsMap;

    public final static OFOxsList EMPTY = new OFOxsList(ImmutableMap.<StatFields, OFOxs<?>>of());

    private OFOxsList(Map<StatFields, OFOxs<?>> oxsMap) {
        this.oxsMap = oxsMap;
    }

    @SuppressWarnings("unchecked")
    public <T extends OFValueType<T>> OFOxs<T> get(StatField<T> statField) {
        return (OFOxs<T>) oxsMap.get(statField.id);
    }

    public static class Builder {
        private final Map<StatFields, OFOxs<?>> oxsMap;

        public Builder() {
            oxsMap = new EnumMap<StatFields, OFOxs<?>>(StatFields.class);
        }

        public Builder(EnumMap<StatFields, OFOxs<?>> oxsMap) {
            this.oxsMap = oxsMap;
        }

        public <T extends OFValueType<T>> void set(OFOxs<T> oxs) {
            oxsMap.put(oxs.getStatField().id, oxs);
        }

        public <T extends OFValueType<T>> void unset(StatField<T> statField) {
            oxsMap.remove(statField.id);
        }

        public OFOxsList build() {
            return OFOxsList.ofList(oxsMap.values());
        }
    }

    @Override
    public Iterator<OFOxs<?>> iterator() {
        return oxsMap.values().iterator();
    }

    public static OFOxsList ofList(Iterable<OFOxs<?>> oxsList) {
        Map<StatFields, OFOxs<?>> map = new EnumMap<StatFields, OFOxs<?>>(
                StatFields.class);
        for (OFOxs<?> o : oxsList) {
            map.put(o.getStatField().id, o);
        }
        return new OFOxsList(map);
    }

    public static OFOxsList of(OFOxs<?>... oxss) {
        Map<StatFields, OFOxs<?>> map = new EnumMap<StatFields, OFOxs<?>>(
                StatFields.class);
        for (OFOxs<?> o : oxss) {
            map.put(o.getStatField().id, o);
        }
        return new OFOxsList(map);
    }

    public static OFOxsList readFrom(ByteBuf bb, int length,
            OFMessageReader<OFOxs<?>> reader) throws OFParseError {
        return ofList(ChannelUtils.readList(bb, length, reader));
    }

    @Override
    public void writeTo(ByteBuf bb) {
        for (OFOxs<?> o : this) {
            o.writeTo(bb);
        }
    }

    public OFOxsList.Builder createBuilder() {
        return new OFOxsList.Builder(new EnumMap<StatFields, OFOxs<?>>(oxsMap));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((oxsMap == null) ? 0 : oxsMap.hashCode());
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
        OFOxsList other = (OFOxsList) obj;
        if (oxsMap == null) {
            if (other.oxsMap != null)
                return false;
        } else if (!oxsMap.equals(other.oxsMap))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OFOxsList" + oxsMap;
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        for (OFOxs<?> o : this) {
            o.putTo(sink);
        }
    }


}

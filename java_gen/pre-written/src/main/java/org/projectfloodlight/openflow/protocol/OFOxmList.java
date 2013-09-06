package org.projectfloodlight.openflow.protocol;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.match.MatchFields;
import org.projectfloodlight.openflow.types.OFValueType;
import org.projectfloodlight.openflow.util.ChannelUtils;
import org.projectfloodlight.openflow.protocol.oxm.OFOxm;

import com.google.common.collect.ImmutableMap;

public class OFOxmList implements Iterable<OFOxm<?>>, Writeable {
    private final Map<MatchFields, OFOxm<?>> oxmMap;

    public final static OFOxmList EMPTY = new OFOxmList(ImmutableMap.<MatchFields, OFOxm<?>>of());

    private OFOxmList(Map<MatchFields, OFOxm<?>> oxmMap) {
        this.oxmMap = oxmMap;
    }

    @SuppressWarnings("unchecked")
    public <T extends OFValueType<T>> OFOxm<T> get(MatchField<T> matchField) {
        return (OFOxm<T>) oxmMap.get(matchField.id);
    }

    public static class Builder {
        private final Map<MatchFields, OFOxm<?>> oxmMap;

        public Builder() {
            oxmMap = new EnumMap<MatchFields, OFOxm<?>>(MatchFields.class);
        }

        public Builder(EnumMap<MatchFields, OFOxm<?>> oxmMap) {
            this.oxmMap = oxmMap;
        }

        public <T extends OFValueType<T>> void set(OFOxm<T> oxm) {
            oxmMap.put(oxm.getMatchField().id, oxm);
        }

        public <T extends OFValueType<T>> void unset(MatchField<T> matchField) {
            oxmMap.remove(matchField.id);
        }

        public OFOxmList build() {
            return new OFOxmList(oxmMap);
        }
    }

    @Override
    public Iterator<OFOxm<?>> iterator() {
        return oxmMap.values().iterator();
    }

    public static OFOxmList ofList(List<OFOxm<?>> oxmList) {
        Map<MatchFields, OFOxm<?>> map = new EnumMap<MatchFields, OFOxm<?>>(
                MatchFields.class);
        for (OFOxm<?> o : oxmList) {
            // TODO: if fully masked, ignore oxm.
            map.put(o.getMatchField().id, o);
        }
        return new OFOxmList(map);
    }

    public static OFOxmList of(OFOxm<?>... oxms) {
        Map<MatchFields, OFOxm<?>> map = new EnumMap<MatchFields, OFOxm<?>>(
                MatchFields.class);
        for (OFOxm<?> o : oxms) {
            // TODO: if fully masked, ignore oxm.
            map.put(o.getMatchField().id, o);
        }
        return new OFOxmList(map);
    }

    public static OFOxmList readFrom(ChannelBuffer bb, int length,
            OFMessageReader<OFOxm<?>> reader) throws OFParseError {
        return ofList(ChannelUtils.readList(bb, length, reader));
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        for (OFOxm<?> o : this) {
            o.writeTo(bb);
        }
    }

    public OFOxmList.Builder createBuilder() {
        return new OFOxmList.Builder(new EnumMap<MatchFields, OFOxm<?>>(oxmMap));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((oxmMap == null) ? 0 : oxmMap.hashCode());
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
        OFOxmList other = (OFOxmList) obj;
        if (oxmMap == null) {
            if (other.oxmMap != null)
                return false;
        } else if (!oxmMap.equals(other.oxmMap))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OFOxmList" + oxmMap;
    }


}

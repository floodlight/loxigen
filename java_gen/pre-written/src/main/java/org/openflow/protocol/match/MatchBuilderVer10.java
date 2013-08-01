package org.openflow.protocol.match;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.types.EthType;
import org.openflow.types.IPv4;
import org.openflow.types.IpDscp;
import org.openflow.types.IpProtocol;
import org.openflow.types.MacAddress;
import org.openflow.types.OFPort;
import org.openflow.types.OFValueType;
import org.openflow.types.U16;
import org.openflow.types.VlanPcp;
import org.openflow.types.VlanVid;

public class MatchBuilderVer10 implements MatchBuilder {

    interface BuilderParamHandler<T> {
        public T get(MatchBuilderVer10 builder);

        public void set(MatchBuilderVer10 builder, T value);
    }

    // public static Map<MatchField<?,?>, BuilderParamHandler<?>>
    // handlerMap = new HashMap();

    protected int wildcards;
    protected OFPort inputPort;
    protected MacAddress dataLayerSource;
    protected MacAddress dataLayerDestination;
    protected VlanVid dataLayerVirtualLan;
    protected VlanPcp dataLayerVirtualLanPriorityCodePoint;
    protected EthType dataLayerType;
    protected IpDscp ipDscp;
    protected IpProtocol networkProtocol;
    protected IPv4 networkSource;
    protected IPv4 networkDestination;
    protected U16 transportSource;
    protected U16 transportDestination;

    @SuppressWarnings("unchecked")
    @Override
    public <F extends OFValueType<F>> F get(final MatchField<F> match) {
        switch (match.id) {
            case IN_PORT:
                return (F) inputPort;
            case ETH_SRC:
                return (F) dataLayerSource;
            default:
                return null;
        }
    }

    @Override
    public <F extends OFValueType<F>> MatchBuilder set(final MatchField<F> match, final F value) {
        switch (match.id) {
            case IN_PORT:
                inputPort = (OFPort) value;
                break;
            case ETH_SRC:
                dataLayerSource = (MacAddress) value;
                break;
            default:
                break;
        }
        return this;
    }
    
    @Override
    public <F extends OFValueType<F>> MatchBuilder unset(final MatchField<F> match) {
        // TODO Auto-generated method stub
        return null;
    }

    public OFPort getInputPort() {
        return inputPort;
    }

    public void setInputPort(final OFPort inputPort) {
        this.inputPort = inputPort;
    }

    public MacAddress getDataLayerSource() {
        return dataLayerSource;
    }

    public void setDataLayerSource(final MacAddress dataLayerSource) {
        this.dataLayerSource = dataLayerSource;
    }

    @Override
    public boolean supports(final MatchField<?> field) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supportsMasked(final MatchField<?> field) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isExact(final MatchField<?> field) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFullyWildcarded(final MatchField<?> field) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPartiallyMasked(final MatchField<?> field) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MatchBuilder getBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeTo(final ChannelBuffer bb) {
        // TODO Auto-generated method stub

    }
/*
    @Override
    public <M> void setMasked(final MatchField<?, M> match, final M value) {
        // TODO Auto-generated method stub

    }
*/
    @Override
    public Match getMatch() {
        // TODO Auto-generated method stub
        return null;
    }

}

package org.projectfloodlight.protocol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFPortConfig;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortDescProp;
import org.projectfloodlight.openflow.protocol.OFPortState;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.types.U64;

import com.google.common.collect.ImmutableList;

/**
 * Tests auxiliary OFPortDesc methods for all versions of OpenFlow
 *
 * @author Jason Parraga {@literal <}jason.parraga@bigswitch.com{@literal >}
 */
public class OFPortDescTest {

    @Test
    public void testIsEnabled() {
       testIsEnabledForFactory(OFFactories.getFactory(OFVersion.OF_10));
       testIsEnabledForFactory(OFFactories.getFactory(OFVersion.OF_11));
       testIsEnabledForFactory(OFFactories.getFactory(OFVersion.OF_12));
       testIsEnabledForFactory(OFFactories.getFactory(OFVersion.OF_13));
       testIsEnabledForFactory(OFFactories.getFactory(OFVersion.OF_14));
    }

    public void testIsEnabledForFactory(OFFactory factory) {
        // Default
        OFPortDesc desc = factory.buildPortDesc()
                .build();
        assertThat(desc.isEnabled(), is(true));

        // Partially disabled
        desc = factory.buildPortDesc()
                .setConfig(new HashSet<OFPortConfig>(Arrays.asList(OFPortConfig.PORT_DOWN)))
                .build();
        assertThat(desc.isEnabled(), is(false));

        // Fully disabled
        desc = factory.buildPortDesc()
                .setConfig(new HashSet<OFPortConfig>(Arrays.asList(OFPortConfig.PORT_DOWN)))
                .setState(new HashSet<OFPortState>(Arrays.asList(OFPortState.LINK_DOWN)))
                .build();
        assertThat(desc.isEnabled(), is(false));
    }

    @Test
    public void testGenerationIdZeroIfUnset() {
       for(OFVersion v: OFVersion.values()) {
           OFFactory factory = OFFactories.getFactory(v);
           assertThat("For version "+v, factory.buildPortDesc().build().getBsnGenerationId(),
                    Matchers.equalTo(U64.ZERO));
       }
    }

    @Test
    public void testGenerationIdSet() {
        OFFactory factory = OFFactories.getFactory(OFVersion.OF_14);
        OFPortDesc desc = factory.buildPortDesc()
          .setProperties(ImmutableList.<OFPortDescProp>of(
                  factory.portDescPropBsnGenerationId(U64.of(1234))))
          .build();

       assertThat(desc.getBsnGenerationId(), equalTo(U64.of(1234)));
    }

}

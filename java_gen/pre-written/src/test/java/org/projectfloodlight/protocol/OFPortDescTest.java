package org.projectfloodlight.protocol;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFPortConfig;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortState;
import org.projectfloodlight.openflow.protocol.OFVersion;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

public class OFPortDescTest {

    OFFactory factory = OFFactories.getFactory(OFVersion.OF_13);

    @Test
    public void testIsEnabled() {
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
}

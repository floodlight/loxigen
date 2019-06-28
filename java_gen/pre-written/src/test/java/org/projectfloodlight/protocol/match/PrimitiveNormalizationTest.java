package org.projectfloodlight.protocol.match;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.types.OFPort;

/** Tests that primitives U8, U16, U32 are normalized in the constructor of our OFObjects. This is chosen
 * on three randomly picked examples of OFObjects that contain values of these types. */
public class PrimitiveNormalizationTest {
    private final OFFactory factory = OFFactories.getFactory(OFVersion.OF_14);

    @Test
    public void normalizeU8() {
        assertThat(factory.bsnTlvs().vlanPcp((short) -1), equalTo(factory.bsnTlvs().vlanPcp((short) 0xFF)));
        assertThat(factory.bsnTlvs().vlanPcp((short) -1), not(factory.bsnTlvs().vlanPcp((short) 0)));
        assertThat(factory.bsnTlvs().vlanPcp((short) -1), not(factory.bsnTlvs().vlanPcp((short) 1)));
        assertThat(factory.bsnTlvs().vlanPcp((short) 1), not(factory.bsnTlvs().vlanPcp((short) 2)));
        assertThat(factory.bsnTlvs().vlanPcp((short) -1), not(factory.bsnTlvs().vlanPcp((short) -2)));
        assertThat(factory.bsnTlvs().buildVlanPcp().setValue((short) -1).build(),
                equalTo(factory.bsnTlvs().vlanPcp((short) 0xFF)));
    }

    @Test
    public void normalizeU16() {
        assertThat(factory.actions().output(OFPort.of(1), -1), equalTo(factory.actions().output(OFPort.of(1), 0xFFFF)));
        assertThat(factory.actions().output(OFPort.of(1), -1), not(factory.actions().output(OFPort.of(1), 0)));
        assertThat(factory.actions().output(OFPort.of(1), -1), not(factory.actions().output(OFPort.of(1), 1)));

        assertThat(factory.actions().buildOutput().setPort(OFPort.of(1)).setMaxLen(-1).build(),
                equalTo(factory.actions().output(OFPort.of(1), 0xFFFF)));
    }


    @Test
    public void normalizeU32() {
        assertThat(factory.bsnTlvs().mplsLabel(-1), equalTo(factory.bsnTlvs().mplsLabel(0xFFFF_FFFFFL)));
        assertThat(factory.bsnTlvs().mplsLabel(-1), not(factory.bsnTlvs().mplsLabel(1)));
        assertThat(factory.bsnTlvs().mplsLabel(-1), not(factory.bsnTlvs().mplsLabel(0)));

        assertThat(factory.bsnTlvs().buildMplsLabel().setValue(-1).build(),
                equalTo(factory.bsnTlvs().mplsLabel(0xFFFF_FFFFFL)));
    }

}

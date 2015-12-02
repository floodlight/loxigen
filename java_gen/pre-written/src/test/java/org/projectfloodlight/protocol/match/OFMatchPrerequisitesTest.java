package org.projectfloodlight.protocol.match;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.projectfloodlight.openflow.protocol.match.MatchField.ETH_TYPE;
import static org.projectfloodlight.openflow.protocol.match.MatchField.IPV4_SRC;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;

@RunWith(Parameterized.class)
public class OFMatchPrerequisitesTest {
    private final OFFactory factory;

    @Parameters(name="{index}.ChannelHandlerVersion={0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {OFVersion.OF_10},
                {OFVersion.OF_13},
                {OFVersion.OF_14}
        });
    }

    public OFMatchPrerequisitesTest(OFVersion version) {
        factory = OFFactories.getFactory(version);
    }

    @Test
    public void testPreRequisitesNotMet() {
        Match match = factory.buildMatch()
           .setExact(ETH_TYPE, EthType.IPv6)
           .setExact(IPV4_SRC, IPv4Address.of("1.2.3.4"))
           .build();

        assertThat(match.get(ETH_TYPE), equalTo(EthType.IPv6));
        assertThat(match.isExact(ETH_TYPE), equalTo(true));
        assertThat(match.isPartiallyMasked(ETH_TYPE), equalTo(false));
        assertThat(match.isFullyWildcarded(ETH_TYPE), equalTo(false));

        assertThat(match.get(IPV4_SRC), nullValue());
        assertThat(match.isExact(IPV4_SRC), equalTo(false));
        assertThat(match.isPartiallyMasked(IPV4_SRC), equalTo(false));
        assertThat(match.isFullyWildcarded(IPV4_SRC), equalTo(true));

        Iterable<MatchField<?>> matchFields = match.getMatchFields();
        assertThat(matchFields, Matchers.<MatchField<?>>iterableWithSize(1));
        assertThat(matchFields, Matchers.<MatchField<?>>contains(MatchField.ETH_TYPE));
    }

    @Test
    public void testPreRequisitesMet() {
        Match match = factory.buildMatch()
           .setExact(ETH_TYPE, EthType.IPv4)
           .setExact(IPV4_SRC, IPv4Address.of("1.2.3.4"))
           .build();

        assertThat(match.get(ETH_TYPE), equalTo(EthType.IPv4));
        assertThat(match.isExact(ETH_TYPE), equalTo(true));
        assertThat(match.isPartiallyMasked(ETH_TYPE), equalTo(false));
        assertThat(match.isFullyWildcarded(ETH_TYPE), equalTo(false));

        assertThat(match.get(IPV4_SRC), equalTo(IPv4Address.of("1.2.3.4")));
        assertThat(match.isExact(IPV4_SRC), equalTo(true));
        assertThat(match.isPartiallyMasked(IPV4_SRC), equalTo(false));
        assertThat(match.isFullyWildcarded(IPV4_SRC), equalTo(false));

        Iterable<MatchField<?>> matchFields = match.getMatchFields();
        assertThat(matchFields, Matchers.<MatchField<?>>iterableWithSize(2));
        assertThat(matchFields, Matchers.<MatchField<?>>contains(MatchField.ETH_TYPE, MatchField.IPV4_SRC));
    }


}

package org.projectfloodlight.protocol.match;

import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.U8;

public class MatchFieldIteration13Test extends MatchFieldIterationBase {
    public MatchFieldIteration13Test() {
        super(OFFactories.getFactory(OFVersion.OF_13));
    }

    @Test
    public void matchCircuitFields()
    {
        Match.Builder builder = factory.buildMatchV3().setExact(MatchField
                                                                        .OCH_SIGTYPE, U8.ZERO);

    }
}

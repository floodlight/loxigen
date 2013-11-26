package org.projectfloodlight.protocol.match;

import org.junit.Ignore;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFVersion;

@Ignore
public class MatchFieldIteration10Test extends MatchFieldIterationBase {
    public MatchFieldIteration10Test() {
        super(OFFactories.getFactory(OFVersion.OF_10));
    }
}

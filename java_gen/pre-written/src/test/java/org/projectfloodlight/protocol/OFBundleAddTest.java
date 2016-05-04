package org.projectfloodlight.protocol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFBundleAddMsg;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.types.BundleId;

/** Custom tests that validate that BundleAdd messages inherit the XID from their
 *  contained message, as per OF Spec 1.4.0:
 *  <p>
 *  7.3.9.6 Adding messages to a bundle
 *  <p>
 *     Message added in a bundle should have a unique xid to help matching errors to messages,
 *     and the xid of the bundle add message must be the same.
 *  </p>
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class OFBundleAddTest {
    private final OFFactory factory = OFFactories.getFactory(OFVersion.OF_14);
    private OFFlowAdd flowAdd;

    @Before
    public void setup() {
        flowAdd = factory.buildFlowAdd().build();

    }

    @Test
    public void testBundleAddBuilder() {
        OFBundleAddMsg bundleAdd = createBundleAdd();
        assertThat(bundleAdd.getXid(), equalTo(flowAdd.getXid()));
    }

    private OFBundleAddMsg createBundleAdd() {
        return factory.buildBundleAddMsg()
                .setBundleId(BundleId.of(1))
                .setData(flowAdd)
                .build();
    }

    @Test
    public void testBundleAddBuilderWithParent() {
        OFBundleAddMsg bundleAdd = createBundleAdd();

        // validate BuilderWithParent
        OFBundleAddMsg builtFromOtherMessage = bundleAdd.createBuilder()
           .build();

        assertThat(builtFromOtherMessage.getXid(), equalTo(builtFromOtherMessage.getData().getXid()));
    }

    @Test
    public void testBundleAddBuilderWithParentOverwrite() {
        OFFlowAdd flowAdd2 = factory.buildFlowAdd().setXid(1234L).build();

        // BuilderWithParent, overwrite with new message
        OFBundleAddMsg bundleAdd = createBundleAdd();

        OFBundleAddMsg builtFromOtherMessage = bundleAdd.createBuilder()
           .setData(flowAdd2)
           .build();

        assertThat(builtFromOtherMessage.getXid(), equalTo(flowAdd2.getXid()));
    }

}

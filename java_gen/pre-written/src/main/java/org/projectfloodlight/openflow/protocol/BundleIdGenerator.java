package org.projectfloodlight.openflow.protocol;

import org.projectfloodlight.openflow.types.BundleId;

public interface BundleIdGenerator {
    BundleId nextBundleId();
}

package org.projectfloodlight.openflow.protocol;

import java.util.concurrent.atomic.AtomicInteger;

import org.projectfloodlight.openflow.types.BundleId;

public class BundleIdGenerators {
    private static final BundleIdGenerator GLOBAL_BUNDLE_ID_GENERATOR = create();

    public static BundleIdGenerator create() {
        return new StandardBundleIdGenerator();
    }

    public static BundleIdGenerator global() {
        return GLOBAL_BUNDLE_ID_GENERATOR;
    }
}

class StandardBundleIdGenerator implements BundleIdGenerator {

    private final AtomicInteger idGen = new AtomicInteger();

    @Override
    public BundleId nextBundleId() {
        return BundleId.of(idGen.incrementAndGet());
    }

}
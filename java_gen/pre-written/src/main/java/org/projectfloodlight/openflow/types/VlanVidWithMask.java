package org.projectfloodlight.openflow.types;

public class VlanVidWithMask extends Masked<VlanVid> {
    private VlanVidWithMask(VlanVid value, VlanVid mask) {
        super(value, mask);
    }

    /* a combination of Vlan Vid and mask that matches any tagged packet */
    public final static VlanVidWithMask ANY_TAGGED = new VlanVidWithMask(VlanVid.PRESENT, VlanVid.PRESENT);

}

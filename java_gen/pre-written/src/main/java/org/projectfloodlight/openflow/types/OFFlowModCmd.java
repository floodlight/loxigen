package org.projectfloodlight.openflow.types;

/**
 * Version agnostic view of commands to an OFFlowMod
 *
 * @author capveg
 */

public enum OFFlowModCmd {
    ADD, MODIFY, MODIFY_STRICT, DELETE, DELETE_STRICT
}

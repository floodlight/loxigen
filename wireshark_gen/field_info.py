# Copyright 2013, Big Switch Networks, Inc.
#
# LoxiGen is licensed under the Eclipse Public License, version 1.0 (EPL), with
# the following special exception:
#
# LOXI Exception
#
# As a special exception to the terms of the EPL, you may distribute libraries
# generated by LoxiGen (LoxiGen Libraries) under the terms of your choice, provided
# that copyright and licensing notices generated by LoxiGen are not altered or removed
# from the LoxiGen Libraries and the notice provided below is (i) included in
# the LoxiGen Libraries, if distributed in source code form and (ii) included in any
# documentation for the LoxiGen Libraries, if distributed in binary form.
#
# Notice: "Copyright 2013, Big Switch Networks, Inc. This library was generated by the LoxiGen Compiler."
#
# You may not use this file except in compliance with the EPL or LOXI Exception. You may obtain
# a copy of the EPL at:
#
# http://www.eclipse.org/legal/epl-v10.html
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# EPL for the specific language governing permissions and limitations
# under the EPL.

# Map from LOXI types to Wireshark types
oftype_to_wireshark_type = {
    "char": "int8",
    "uint8_t": "uint8",
    "uint16_t": "uint16",
    "uint32_t": "uint32",
    "uint64_t": "uint64",
    "of_mac_addr_t": "ether",
    "of_ipv4_t": "ipv4",
    "of_ipv6_t": "ipv6",
    "of_port_name_t": "stringz",
    "of_table_name_t": "stringz",
    "of_desc_str_t": "stringz",
    "of_serial_num_t": "stringz",
    "of_octets_t": "bytes",
    "of_port_no_t": "uint32",
    "of_port_desc_t": "stringz",
    "of_bsn_vport_t": "bytes",
    "of_bsn_vport_q_in_q_t": "bytes",
    "of_fm_cmd_t": "uint16",
    "of_wc_bmap_t": "uint64",
    "of_match_bmap_t": "uint64",
    "of_match_t": "bytes",
    "of_oxm_t": "bytes",
    "of_meter_features_t": "bytes",
    "of_bitmap_128_t": "bytes",
}

# Map from LOXI type to Wireshark base
oftype_to_base = {
    "char": "DEC",
    "uint8_t": "DEC",
    "uint16_t": "DEC",
    "uint32_t": "DEC",
    "uint64_t": "DEC",
    "of_mac_addr_t": "NONE",
    "of_ipv4_t": "NONE",
    "of_ipv6_t": "NONE",
    "of_port_name_t": "NONE",
    "of_table_name_t": "NONE",
    "of_desc_str_t": "NONE",
    "of_serial_num_t": "NONE",
    "of_octets_t": "NONE",
    "of_port_no_t": "DEC",
    "of_port_desc_t": "NONE",
    "of_bsn_vport_t": "NONE",
    "of_bsn_vport_q_in_q_t": "NONE",
    "of_fm_cmd_t": "DEC",
    "of_wc_bmap_t": "HEX",
    "of_match_bmap_t": "HEX",
    "of_match_t": "NONE",
    "of_oxm_t": "NONE",
    "of_meter_features_t": "NONE",
    "of_bitmap_128_t": "NONE",
}

# Use enums for certain fields where it isn't specified in the LOXI input
class_field_to_enum = {
    ('of_flow_mod', 'type'): 'ofp_type',
    ('of_error_msg', 'type'): 'ofp_type',
    ('of_stats_request', 'type'): 'ofp_type',
    ('of_stats_request', 'stats_type'): 'ofp_stats_type',
    ('of_stats_request', 'flags'): 'ofp_stats_request_flags',
    ('of_stats_reply', 'type'): 'ofp_type',
    ('of_stats_reply', 'stats_type'): 'ofp_stats_type',
    ('of_stats_reply', 'flags'): 'ofp_stats_reply_flags',
    ('of_flow_mod', 'table_id'): 'ofp_table',
    ('of_flow_mod', '_command'): 'ofp_flow_mod_command',
    ('of_flow_mod', 'out_port'): 'ofp_port',
    ('of_flow_mod', 'out_group'): 'ofp_group',
    ('of_error_msg', 'err_type'): 'ofp_error_type',
    ('of_port_mod', 'type'): 'ofp_type',
    ('of_hello', 'type'): 'ofp_type',
    ('of_features_request', 'type'): 'ofp_type',
    ('of_features_reply', 'type'): 'ofp_type',
    ('of_barrier_request', 'type'): 'ofp_type',
    ('of_barrier_reply', 'type'): 'ofp_type',
    ('of_echo_request', 'type'): 'ofp_type',
    ('of_echo_reply', 'type'): 'ofp_type',
    ('of_match_v3', 'type'): 'ofp_match_type'
}

# Override oftype_to_base for certain field names
field_to_base = {
    "eth_type": "HEX",
    "cookie": "HEX",
    "datapath_id": "HEX",
}

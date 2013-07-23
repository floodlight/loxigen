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

from collections import namedtuple

OFTypeData = namedtuple("OFTypeData", ["init", "pack", "unpack"])

# Map from LOXI type name to an object with templates for init, pack, and unpack
# Most types are defined using the convenience code below. This dict should
# only be used directly for special cases such as primitive types.
type_data_map = {
    'char': OFTypeData(
        init='0',
        pack='struct.pack("!B", %s)',
        unpack='%s.read("!B")[0]'),

    'uint8_t': OFTypeData(
        init='0',
        pack='struct.pack("!B", %s)',
        unpack='%s.read("!B")[0]'),

    'uint16_t': OFTypeData(
        init='0',
        pack='struct.pack("!H", %s)',
        unpack='%s.read("!H")[0]'),

    'uint32_t': OFTypeData(
        init='0',
        pack='struct.pack("!L", %s)',
        unpack='%s.read("!L")[0]'),

    'uint64_t': OFTypeData(
        init='0',
        pack='struct.pack("!Q", %s)',
        unpack='%s.read("!Q")[0]'),

    'of_port_no_t': OFTypeData(
        init='0',
        pack='util.pack_port_no(%s)',
        unpack='util.unpack_port_no(%s)'),

    'of_fm_cmd_t': OFTypeData(
        init='0',
        pack='util.pack_fm_cmd(%s)',
        unpack='util.unpack_fm_cmd(%s)'),

    'of_wc_bmap_t': OFTypeData(
        init='util.init_wc_bmap()',
        pack='util.pack_wc_bmap(%s)',
        unpack='util.unpack_wc_bmap(%s)'),

    'of_match_bmap_t': OFTypeData(
        init='util.init_match_bmap()',
        pack='util.pack_match_bmap(%s)',
        unpack='util.unpack_match_bmap(%s)'),

    'of_ipv6_t': OFTypeData(
        init="'\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00'",
        pack='struct.pack("!16s", %s)',
        unpack="%s.read('!16s')[0]"),

    'of_mac_addr_t': OFTypeData(
        init='[0,0,0,0,0,0]',
        pack='struct.pack("!6B", *%s)',
        unpack="list(%s.read('!6B'))"),

    'of_octets_t': OFTypeData(
        init="''",
        pack='%s',
        unpack='str(%s.read_all())'),

    # HACK need the match_v3 length field
    'list(of_oxm_t)': OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack='oxm.unpack_list(%s.slice(_length-4))'),

    'of_oxm_t': OFTypeData(
        init='None',
        pack='%s.pack()',
        unpack='oxm.unpack(%s)'),

    # TODO implement unpack
    'list(of_table_features_t)': OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack=None),

    # TODO implement unpack
    'list(of_action_id_t)': OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack=None),

    # TODO implement unpack
    'list(of_table_feature_prop_t)': OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack=None),
}

## Fixed length strings

# Map from class name to length
fixed_length_strings = {
    'of_port_name_t': 16,
    'of_table_name_t': 32,
    'of_serial_num_t': 32,
    'of_desc_str_t': 256,
}

for (cls, length) in fixed_length_strings.items():
    type_data_map[cls] = OFTypeData(
        init='""',
        pack='struct.pack("!%ds", %%s)' % length,
        unpack='%%s.read("!%ds")[0].rstrip("\\x00")' % length)

## Embedded structs

# Map from class name to Python class name
embedded_structs = {
    'of_match_t': 'common.match',
    'of_port_desc_t': 'common.port_desc',
    'of_meter_features_t': 'common.meter_features',
    'of_bsn_vport_q_in_q_t': 'common.bsn_vport_q_in_q',
}

for (cls, pyclass) in embedded_structs.items():
    type_data_map[cls] = OFTypeData(
        init='%s()' % pyclass,
        pack='%s.pack()',
        unpack='%s.unpack(%%s)' % pyclass)

## Variable element length lists

# Map from list class name to list deserializer
variable_elem_len_lists = {
    'list(of_action_t)': 'action.unpack_list',
    'list(of_bucket_t)': 'common.unpack_list_bucket',
    'list(of_flow_stats_entry_t)': 'common.unpack_list_flow_stats_entry',
    'list(of_group_desc_stats_entry_t)': 'common.unpack_list_group_desc_stats_entry',
    'list(of_group_stats_entry_t)': 'common.unpack_list_group_stats_entry',
    'list(of_hello_elem_t)': 'common.unpack_list_hello_elem',
    'list(of_instruction_t)': 'instruction.unpack_list',
    'list(of_meter_band_t)': 'meter_band.unpack_list',
    'list(of_meter_stats_t)': 'common.unpack_list_meter_stats',
    'list(of_packet_queue_t)': 'common.unpack_list_packet_queue',
    'list(of_queue_prop_t)': 'common.unpack_list_queue_prop',
}

for (cls, deserializer) in variable_elem_len_lists.items():
    type_data_map[cls] = OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack='%s(%%s)' % deserializer)

## Fixed element length lists

# Map from list class name to list element deserializer
fixed_elem_len_lists = {
    'list(of_bsn_interface_t)': 'common.bsn_interface.unpack',
    'list(of_bucket_counter_t)': 'common.bucket_counter.unpack',
    'list(of_meter_band_stats_t)': 'common.meter_band_stats.unpack',
    'list(of_port_desc_t)': 'common.port_desc.unpack',
    'list(of_port_stats_entry_t)': 'common.port_stats_entry.unpack',
    'list(of_queue_stats_entry_t)': 'common.queue_stats_entry.unpack',
    'list(of_table_stats_entry_t)': 'common.table_stats_entry.unpack',
    'list(of_uint32_t)': 'common.uint32.unpack',
    'list(of_uint8_t)': 'common.uint8.unpack',
}

for (cls, element_deserializer) in fixed_elem_len_lists.items():
    type_data_map[cls] = OFTypeData(
        init='[]',
        pack='util.pack_list(%s)',
        unpack='loxi.generic_util.unpack_list(%%s, %s)' % element_deserializer)

## Public interface

# Return an initializer expression for the given oftype
def gen_init_expr(oftype):
    type_data = type_data_map.get(oftype)
    if type_data and type_data.init:
        return type_data.init
    else:
        return "loxi.unimplemented('init %s')" % oftype

# Return a pack expression for the given oftype
#
# 'value_expr' is a string of Python code which will evaluate to
# the value to be packed.
def gen_pack_expr(oftype, value_expr):
    type_data = type_data_map.get(oftype)
    if type_data and type_data.pack:
        return type_data.pack % value_expr
    else:
        return "loxi.unimplemented('pack %s')" % oftype

# Return an unpack expression for the given oftype
#
# 'reader_expr' is a string of Python code which will evaluate to
# the OFReader instance used for deserialization.
def gen_unpack_expr(oftype, reader_expr):
    type_data = type_data_map.get(oftype)
    if type_data and type_data.unpack:
        return type_data.unpack % reader_expr
    else:
        return "loxi.unimplemented('unpack %s')" % oftype

## List of mixed data types
#
# This is a list of data types which require special treatment
# because the underlying datatype has changed between versions.
# The main example is port which went from 16 to 32 bits.  We
# define per-version accessors for these types and those are
# used in place of the normal ones.
#
# The wire protocol number is used to identify versions.  For now,
# the value is the name of the type to use for that version
#
# This is the map between the external type (like of_port_no_t)
# which is used by customers of this code and the internal
# datatypes (like uint16_t) that appear on the wire for a
# particular version.
#
from collections import namedtuple
import logging

import loxi_front_end.frontend_ir as fe
import loxi_ir.ir

ofp_constants = dict(
    OF_MAX_TABLE_NAME_LEN = 32,
    OF_MAX_PORT_NAME_LEN  = 16,
    OF_ETH_ALEN = 6,
    OF_DESC_STR_LEN   = 256,
    OF_SERIAL_NUM_LEN = 32,
    OF_CONTROLLER_URI_LEN = 32
)


of_mixed_types = dict(
    of_port_no_t = {
        1: "uint16_t",
        2: "uint32_t",
        3: "uint32_t",
        4: "uint32_t",
        5: "uint32_t",
        6: "uint32_t",
        "short_name":"port_no"
        },
    of_port_desc_t = {
        1: "of_port_desc_t",
        2: "of_port_desc_t",
        3: "of_port_desc_t",
        4: "of_port_desc_t",
        5: "of_port_desc_t",
        6: "of_port_desc_t",
        "short_name":"port_desc"
        },
    of_bsn_vport_t = {
        1: "of_bsn_vport_t",
        2: "of_bsn_vport_t",
        3: "of_bsn_vport_t",
        4: "of_bsn_vport_t",
        5: "of_bsn_vport_t",
        6: "of_bsn_vport_t",
        "short_name":"bsn_vport"
        },
    of_fm_cmd_t = { # Flow mod command went from u16 to u8
        1: "uint16_t",
        2: "uint8_t",
        3: "uint8_t",
        4: "uint8_t",
        5: "uint8_t",
        6: "uint8_t",
        "short_name":"fm_cmd"
        },
    of_wc_bmap_t = { # Wildcard bitmap
        1: "uint32_t",
        2: "uint32_t",
        3: "uint64_t",
        4: "uint64_t",
        5: "uint64_t",
        6: "uint64_t",
        "short_name":"wc_bmap"
        },
    of_match_bmap_t = { # Match bitmap
        1: "uint32_t",
        2: "uint32_t",
        3: "uint64_t",
        4: "uint64_t",
        5: "uint64_t",
        6: "uint64_t",
        "short_name":"match_bmap"
        },
    of_match_t = { # Match object
        1: "of_match_v1_t",
        2: "of_match_v2_t",
        3: "of_match_v3_t",
        4: "of_match_v3_t",  # Currently uses same match as 1.2 (v3).
        5: "of_match_v3_t",  # Currently uses same match as 1.2 (v3).
        6: "of_match_v3_t",
        "short_name":"match"
        },
    of_stat_t = { #Statistics object
        6: "of_stat_v6_t",
        "short_name":"stat"
        },
    of_time_t = { # time object
        6: "of_time_t",
        "short_name":"time"
        }
)

## basic lengths
of_base_lengths = dict(
    char     = (1, True),
    uint8_t  = (1, True),
    uint16_t = (2, True),
    uint32_t = (4, True),
    uint64_t = (8, True),
    of_mac_addr_t = (6, True),
    of_ipv4_t = (4, True),
    of_ipv6_t = (16, True),
    of_port_name_t = (ofp_constants["OF_MAX_PORT_NAME_LEN"], True),
    of_table_name_t = (ofp_constants["OF_MAX_TABLE_NAME_LEN"], True),
    of_desc_str_t = (ofp_constants["OF_DESC_STR_LEN"], True),
    of_serial_num_t = (ofp_constants["OF_SERIAL_NUM_LEN"], True),
    of_controller_uri_t = (ofp_constants["OF_CONTROLLER_URI_LEN"], True),
    of_str64_t = (64, True),
    of_match_v1_t = (40, True),
    of_match_v2_t = (88, True),
    of_match_v3_t = (8, False),
    of_stat_v6_t = (8, False),
    of_octets_t = (0, False),
    of_bitmap_128_t = (16, True),
    of_checksum_128_t = (16, True),
    of_bitmap_512_t = (64, True),
    of_time_t = (16, True)
)

def type_dec_to_count_base(m_type):
    """
    Resolve a type declaration like uint8_t[4] to a count (4) and base_type
    (uint8_t)

    @param m_type The string type declaration to process
    """
    count = 1
    chk_ar = m_type.split('[')
    if len(chk_ar) > 1:
        count_str = chk_ar[1].split(']')[0]
        if count_str in ofp_constants:
            count = ofp_constants[count_str]
        else:
            count = int(count_str)
        base_type = chk_ar[0]
    else:
        base_type = m_type
    return count, base_type


LengthInfo = namedtuple("LengthInfo", ("offset", "base_length", "is_fixed_length"))

def calc_lengths(version, fe_class, existing_classes, existing_enums):
    offset_fixed = True
    offset = 0

    member_infos = {}
    for member in fe_class.members:
        member_offset = offset if offset_fixed else None

        if isinstance(member, fe.OFPadMember):
            member_base_length = member.length
            member_fixed_length = True
        else:
            m_type = member.oftype
            name = member.name

            member_base_length = 0
            if m_type.find("list(") == 0:
                member_fixed_length = False
            elif m_type.find("struct") == 0:
                raise Exception("Error: recursive struct found: {}, {}"
                                    .format(fe_class.name, name))
            elif m_type == "octets":
                member_fixed_length = False
            else:
                member_base_length, member_fixed_length = member_length(version, fe_class, member, existing_classes, existing_enums)

        if not member_fixed_length:
            offset_fixed = False

        member_infos[member] = LengthInfo(member_offset, member_base_length,
                member_fixed_length)
        offset += member_base_length

    base_length = offset
    fixed_length = offset_fixed if not fe_class.virtual else False
    return (base_length, fixed_length, member_infos)

def member_length(version, fe_class, fe_member, existing_classes, existing_enums):
    """
    return the length of an ir member.

    @return tuple (base_length, length_fixed)
    """
    count, base_type = type_dec_to_count_base(fe_member.oftype)

    if base_type in of_mixed_types:
        base_type = of_mixed_types[base_type][version.wire_version]

    base_class = base_type[:-2]
    if base_class in existing_classes:
        member_ir_class = existing_classes[base_class]
        bytes = member_ir_class.base_length
        length_fixed = member_ir_class.is_fixed_length
        if member_ir_class.has_external_alignment:
            bytes = (bytes + 7) & ~7
    else:
        if base_type in existing_enums:
            enum = existing_enums[base_type]
            base_type = enum.wire_type

        if base_type in of_base_lengths:
            bytes, length_fixed = of_base_lengths[base_type]
        else:
            raise Exception("Unknown type for {}.{}: {}".format(fe_class.name, fe_member.name, base_type))

    return (count * bytes), length_fixed

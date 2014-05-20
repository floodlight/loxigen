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

# @brief Match data representation
#
# @fixme This still has lots of C specific code that should be moved into c_gen

import sys
import c_gen.of_g_legacy as of_g
from generic_utils import *
import c_gen.loxi_utils_legacy as loxi_utils

#
# Use 1.2 match semantics for common case
#
# Generate maps between generic match and version specific matches
# Generate dump functions for generic match
# Generate dump functions for version specific matches

## @var of_match_members
# The dictionary from unified match members to type and indexing info
#
# Keys:
#   name The unified name used for the member
#   m_type The data type used for the object in unified structure
#   print_type The id to use when printing
#   conditions The condition underwhich the field could occur TBD
#   takes_mask_in_spec Shown as taking mask in OF 1.2 spec; IGNORED NOW
#   order Used to define an order for readability
#   v1_wc_shift The WC shift in OF 1.0
#   v2_wc_shift The WC shift in OF 1.1
#
# Unless noted otherwise, class is 0x8000, OFPXMC_OPENFLOW_BASIC
# We use the 1.2 names and alias older names
# Conditions:
#  is_ipv4(_m):  ((_m)->eth_type == 0x0800)
#  is_ipv6(_m):  ((_m)->eth_type == 0x86dd)
#  is_ip(_m):    (is_ipv4(_m) || is_ipv6(_m))
#  is_arp(_m):   ((_m)->eth_type == 0x0806)
#  is_tcp(_m):   (is_ip(_m) && ((_m)->ip_proto == 6))
#  is_udp(_m):   (is_ip(_m) && ((_m)->ip_proto == 17))
#  is_sctp(_m):  (is_ip(_m) && ((_m)->ip_proto == 132))
#  is_icmpv4(_m):  (is_ipv4(_m) && ((_m)->ip_proto == 1))
#  is_icmpv6(_m):  (is_ipv6(_m) && ((_m)->ip_proto == 58))
#

of_match_members = dict(
    in_port = dict(
        name="in_port",
        m_type="of_port_no_t",
        print_type="PRIx32",
        conditions="",
        v1_wc_shift=0,
        v2_wc_shift=0,
        takes_mask_in_spec=False,
        order=100,
        ),
    in_phy_port = dict(
        name="in_phy_port",
        m_type="of_port_no_t",
        print_type="PRIx32",
        conditions="", # OXM_OF_IN_PORT must be present
        takes_mask_in_spec=False,
        order=101,
        ),
    metadata = dict(
        name="metadata",
        m_type="uint64_t",
        print_type="PRIx64",
        conditions="",
        takes_mask_in_spec=True,
        order=102,
        ),

    eth_dst = dict(
        name="eth_dst",
        m_type="of_mac_addr_t",
        v1_wc_shift=3,
        print_type="\"p\"",
        conditions="",
        takes_mask_in_spec=True,
        order=200,
        ),
    eth_src = dict(
        name="eth_src",
        m_type="of_mac_addr_t",
        v1_wc_shift=2,
        print_type="\"p\"",
        conditions="",
        takes_mask_in_spec=True,
        order=201,
        ),
    eth_type = dict(
        name="eth_type",
        m_type="uint16_t",
        v1_wc_shift=4,
        v2_wc_shift=3,
        print_type="PRIx16",
        conditions="",
        takes_mask_in_spec=False,
        order=203,
        ),
    vlan_vid = dict(  # FIXME: Semantics changed in 1.2
        # Use CFI bit to indicate tag presence
        name="vlan_vid",
        m_type="uint16_t",
        v1_wc_shift=1,
        v2_wc_shift=1,
        print_type="PRIx16",
        conditions="",
        takes_mask_in_spec=True,
        order=210,
        ),
    vlan_pcp = dict(
        name="vlan_pcp",
        m_type="uint8_t",
        v1_wc_shift=20,
        v2_wc_shift=2,
        print_type="PRIx8",
        conditions="",
        takes_mask_in_spec=False,
        order=211,
        ),

    ip_dscp = dict(
        name="ip_dscp",
        m_type="uint8_t",
        v1_wc_shift=21,
        v2_wc_shift=4,
        print_type="PRIx8",
        conditions="is_ip(match)",
        takes_mask_in_spec=False,
        order=310,
        ),
    ip_ecn = dict(
        name="ip_ecn",
        m_type="uint8_t",
        print_type="PRIx8",
        conditions="is_ip(match)",
        takes_mask_in_spec=False,
        order=311,
        ),
    ip_proto = dict(
        name="ip_proto",
        m_type="uint8_t",
        v1_wc_shift=5,
        v2_wc_shift=5,
        print_type="PRIx8",
        conditions="is_ip(match)",
        takes_mask_in_spec=False,
        order=320,
        ),
    ipv4_src = dict(
        name="ipv4_src",
        m_type="of_ipv4_t",
        v1_wc_shift=8,
        print_type="PRIx32",
        conditions="is_ipv4(match)",
        takes_mask_in_spec=True,
        order=330,
        ),
    ipv4_dst = dict(
        name="ipv4_dst",
        m_type="of_ipv4_t",
        v1_wc_shift=14,
        print_type="PRIx32",
        conditions="is_ipv4(match)",
        takes_mask_in_spec=True,
        order=331,
        ),

    tcp_dst = dict(
        name="tcp_dst",
        m_type="uint16_t",
        v1_wc_shift=7,
        v2_wc_shift=7,
        print_type="PRIx16",
        conditions="is_tcp(match)",
        takes_mask_in_spec=False,
        order=400,
        ),
    tcp_src = dict(
        name="tcp_src",
        m_type="uint16_t",
        v1_wc_shift=6,
        v2_wc_shift=6,
        print_type="PRIx16",
        conditions="is_tcp(match)",
        takes_mask_in_spec=False,
        order=401,
        ),

    udp_dst = dict(
        name="udp_dst",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="is_udp(match)",
        takes_mask_in_spec=False,
        order=410,
        ),
    udp_src = dict(
        name="udp_src",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="is_udp(match)",
        takes_mask_in_spec=False,
        order=411,
        ),

    sctp_dst = dict(
        name="sctp_dst",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="is_sctp(match)",
        takes_mask_in_spec=False,
        order=420,
        ),
    sctp_src = dict(
        name="sctp_src",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="is_sctp(match)",
        takes_mask_in_spec=False,
        order=421,
        ),

    icmpv4_type = dict(
        name="icmpv4_type",
        m_type="uint8_t",
        print_type="PRIx8",
        conditions="is_icmp_v4(match)",
        takes_mask_in_spec=False,
        order=430,
        ),
    icmpv4_code = dict(
        name="icmpv4_code",
        m_type="uint8_t",
        print_type="PRIx8",
        conditions="is_icmp_v4(match)",
        takes_mask_in_spec=False,
        order=431,
        ),

    arp_op = dict(
        name="arp_op",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="is_arp(match)",
        takes_mask_in_spec=False,
        order=450,
        ),

    arp_spa = dict(
        name="arp_spa",
        m_type="uint32_t",
        print_type="PRIx32",
        conditions="is_arp(match)",
        takes_mask_in_spec=True,
        order=451,
        ),
    arp_tpa = dict(
        name="arp_tpa",
        m_type="uint32_t",
        print_type="PRIx32",
        conditions="is_arp(match)",
        takes_mask_in_spec=True,
        order=452,
        ),

    arp_sha = dict(
        name="arp_sha",
        m_type="of_mac_addr_t",
        print_type="\"p\"",
        conditions="is_arp(match)",
        takes_mask_in_spec=False,
        order=453,
        ),
    arp_tha = dict(
        name="arp_tha",
        m_type="of_mac_addr_t",
        print_type="\"p\"",
        conditions="is_arp(match)",
        takes_mask_in_spec=False,
        order=454,
        ),

    ipv6_src = dict(
        name="ipv6_src",
        m_type="of_ipv6_t",
        print_type="\"p\"",
        conditions="is_ipv6(match)",
        takes_mask_in_spec=True,
        order=500,
        ),
    ipv6_dst = dict(
        name="ipv6_dst",
        m_type="of_ipv6_t",
        print_type="\"p\"",
        conditions="is_ipv6(match)",
        takes_mask_in_spec=True,
        order=501,
        ),

    ipv6_flabel = dict(
        name="ipv6_flabel",
        m_type="uint32_t",
        print_type="PRIx32",
        conditions="is_ipv6(match)",
        takes_mask_in_spec=False, # Comment in openflow.h says True
        order=502,
        ),

    icmpv6_type = dict(
        name="icmpv6_type",
        m_type="uint8_t",
        print_type="PRIx8",
        conditions="is_icmp_v6(match)",
        takes_mask_in_spec=False,
        order=510,
        ),
    icmpv6_code = dict(
        name="icmpv6_code",
        m_type="uint8_t",
        print_type="PRIx8",
        conditions="is_icmp_v6(match)",
        takes_mask_in_spec=False,
        order=511,
        ),

    ipv6_nd_target = dict(
        name="ipv6_nd_target",
        m_type="of_ipv6_t",
        print_type="\"p\"",
        conditions="", # fixme
        takes_mask_in_spec=False,
        order=512,
        ),

    ipv6_nd_sll = dict(
        name="ipv6_nd_sll",
        m_type="of_mac_addr_t",
        print_type="\"p\"",
        conditions="", # fixme
        takes_mask_in_spec=False,
        order=520,
        ),
    ipv6_nd_tll = dict(
        name="ipv6_nd_tll",
        m_type="of_mac_addr_t",
        print_type="\"p\"",
        conditions="", # fixme
        takes_mask_in_spec=False,
        order=521,
        ),

    mpls_label = dict(
        name="mpls_label",
        m_type="uint32_t",
        v2_wc_shift=8,
        print_type="PRIx32",
        conditions="",
        takes_mask_in_spec=False,
        order=600,
        ),
    mpls_tc = dict(
        name="mpls_tc",
        m_type="uint8_t",
        v2_wc_shift=9,
        print_type="PRIx8",
        conditions="",
        takes_mask_in_spec=False,
        order=601,
        ),

    bsn_in_ports_128 = dict(
        name="bsn_in_ports_128",
        m_type="of_bitmap_128_t",
        v2_wc_shift=9,
        print_type="p",
        conditions="",
        takes_mask_in_spec=True,
        order=1000,
        ),

    bsn_lag_id = dict(
        name="bsn_lag_id",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=False,
        order=1001,
        ),

    bsn_vrf = dict(
        name="bsn_vrf",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=False,
        order=1002,
        ),

    bsn_global_vrf_allowed = dict(
        name="bsn_global_vrf_allowed",
        m_type="uint8_t",
        print_type="PRIu8",
        conditions="",
        takes_mask_in_spec=False,
        order=1003,
        ),

    bsn_l3_interface_class_id = dict(
        name="bsn_l3_interface_class_id",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1003,
        ),

    bsn_l3_src_class_id = dict(
        name="bsn_l3_src_class_id",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1004,
        ),

    bsn_l3_dst_class_id = dict(
        name="bsn_l3_dst_class_id",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1005,
        ),

    bsn_egr_port_group_id = dict(
        name="bsn_egr_port_group_id",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1006,
        ),

    bsn_udf0 = dict(
        name="bsn_udf0",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf1 = dict(
        name="bsn_udf1",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf2 = dict(
        name="bsn_udf2",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf3 = dict(
        name="bsn_udf3",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf4 = dict(
        name="bsn_udf4",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf5 = dict(
        name="bsn_udf5",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf6 = dict(
        name="bsn_udf6",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_udf7 = dict(
        name="bsn_udf7",
        m_type="uint32_t",
        print_type="PRIu32",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),

    bsn_tcp_flags = dict(
        name="bsn_tcp_flags",
        m_type="uint16_t",
        print_type="PRIx16",
        conditions="",
        takes_mask_in_spec=True,
        order=1010,
        ),
)

match_keys_sorted = of_match_members.keys()
match_keys_sorted.sort(key=lambda entry:of_match_members[entry]["order"])

of_v1_keys = [
    "eth_dst",
    "eth_src",
    "eth_type",
    "in_port",
    "ipv4_dst",
    "ip_proto",
    "ipv4_src",
    "ip_dscp",
    "tcp_dst",  # Means UDP too for 1.0 and 1.1
    "tcp_src",  # Means UDP too for 1.0 and 1.1
    "vlan_pcp",
    "vlan_vid"
    ]

of_v2_keys = [
    "eth_dst",
    "eth_src",
    "eth_type",
    "in_port",
    "ipv4_dst",
    "ip_proto",
    "ipv4_src",
    "ip_dscp",
    "tcp_dst",  # Means UDP too for 1.0 and 1.1
    "tcp_src",  # Means UDP too for 1.0 and 1.1
    "vlan_pcp",
    "vlan_vid",
    "mpls_label",
    "mpls_tc",
    "metadata"
    ]

of_v2_full_mask = [
    "eth_dst",
    "eth_src",
    "ipv4_dst",
    "ipv4_src",
    "metadata"
    ]

##
# Check that all members in the hash are recognized as match keys
def match_sanity_check():
    count = 0
    for match_v in ["of_match_v1", "of_match_v2"]:
        count += 1
        for mm in of_g.unified[match_v][count]["members"]:
            key = mm["name"]
            if key.find("_mask") >= 0:
                continue
            if loxi_utils.skip_member_name(key):
                continue
            if key == "wildcards":
                continue
            if not key in of_match_members:
                print "Key %s not found in match struct, v %s" % (key, match_v)
                sys.exit(1)

    # Generate list of OXM names from the unified classes
    oxm_names = [x[7:] for x in of_g.unified.keys() if
                 x.startswith('of_oxm_') and
                 x.find('masked') < 0 and
                 x.find('header') < 0]

    # Check that all OXMs are in the match members
    for key in oxm_names:
        if not key in of_match_members:
            if not (key.find("_masked") > 0):
                debug("Key %s in OXM, not of_match_members" % key)
                sys.exit(1)
            if not key[:-7] in of_match_members:
                debug("Key %s in OXM, but %s not in of_match_members"
                      % (key, key[:-7]))
                sys.exit(1)

    # Check that all match members are in the OXMs
    for key in of_match_members:
        if not key in oxm_names:
            debug("Key %s in of_match_members, not in OXM" % key)
            sys.exit(1)
        oxm_type = of_g.unified['of_oxm_%s' % key]['union']['value']['m_type']
        if of_match_members[key]["m_type"] != oxm_type:
            debug("Type mismatch for key %s in oxm data: %s vs %s" %
                  (key, of_match_members[key]["m_type"], oxm_type))
            sys.exit(1)

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

##
# @brief Global data structs for LOXI code generation
#
# @fixme This needs to be refactored and brought into the 21st century.
#

import sys
from optparse import OptionParser
# @fixme Replace with argparse

################################################################
#
# Configuration global parameters
#
################################################################

##
# The map from wire protocol to enum identifier generated from input
# This is built from the version-specific structs file info.
# @fixme This should go away when the process structs file is updated
wire_ver_map = {}

##
# Command line options
options = {}

##
# Command line arguments
args = []

##@var config_default
# The default configuration dictionary for LOXI code generation
options_default = {
    "lang"               : "c",
    "version-list"       : "1.0 1.1 1.2 1.3",
    "install-dir"        : "loxi_output",
}

##
# The list of wire versions which are to be supported
target_version_list = []

def lang_normalize(lang):
    """
    Normalize the representation of the language
    """
    return lang.lower()

def version_list_normalize(vlist):
    """
    Normalize the version list and return as an array
    """
    out_list = []
    # @fixme Map to OF version references
    if vlist.find(',') > 0:
        vlist = vlist.split(',')
    else:
        vlist = vlist.split()
    vlist.sort()
    for ver in vlist:
        try:
            out_list.append(of_param_version_map[ver])
        except KeyError:
            sys.stderr.write("Bad version input, %s" % str(ver))
            sys.exit(1)

    return out_list

def process_commandline(default_vals=options_default):
    """
    Set up the options dictionary

    @param cfg_dflt The default configuration dictionary
    @return A pair (options, args) as per parser return
    """
    global options
    global args
    global target_version_list

    parser = OptionParser(version="%prog 0.1")

    #@todo Add options via dictionary
    parser.add_option("--list-files", action="store_true", default=False,
                      help="List output files generated")
    parser.add_option("-l", "--lang", "--language",
                      default=default_vals["lang"],
                      help="Select the target language: c, python")
    parser.add_option("-i", "--install-dir",
                      default=default_vals["install-dir"],
                      help="Directory to install generated files to (default %s)" % default_vals["install-dir"])
    parser.add_option("-v", "--version-list",
                      default=default_vals["version-list"],
                      help="Specify the versions to target as 1.0 1.1 etc")

    (options, args) = parser.parse_args()

    options.lang = lang_normalize(options.lang)
    target_version_list = version_list_normalize(options.version_list)
    target_version_list.sort()
    return (options, args)

##
# The dictionary of config variables related to code
#
# @param gen_unified_fns  Boolean; Generate top level function definitions for
# accessors which are independent of the version; the alternative is to only
# use the function pointers in the class definitions.  These functions support
# better inlining optimizations.
#
# @param gen_fn_ptrs Boolean; Generate the functions pointed to by pointer
# in the class (struct) definitions; the alternative is to only use the
# unified (use_) functions
#
# @param use_obj_id  Use object IDs in struct defns   CURRENTLY NOT SUPPORTED
#
# @param return_base_types For 'get' accessors, return values when possible.
# Otherwise all values are returned thru a call by variable parameter
#
# @param use_static_inlines Generate low level accessors as static inline
# and put them in header files rather than .c files.
#
# @param copy_semantics One of "read", "write" or "grow".  This defines the
# way that buffer references are managed.  Currently on "read" is supported.
#
# @param encode_typedefs Use object and member IDs (rather than names)
# when generating the names used for accessor function typedefs
#
# @param get_returns One of "error", "value", or "void";
# CURRENTLY ONLY "error" IS SUPPORTED.  "error" means
# all get operations return an error code.  "value" means return a base_type
# value when possible or void if not.  "void" means always return void
# and use a call-by-variable parameter
#

# @fixme These are still very C specific and should probably either
# go into lang_c.py or be swallowed by command line option parsing
code_gen_config = dict(
    gen_unified_fns=True,
#    gen_fn_ptrs=True,  # WARNING: Haven't tested with this in a while
    gen_fn_ptrs=False,
    use_obj_id=False,
    use_static_inlines=False,
    copy_semantics="read",  # Only read implemented: read, write, grow
    encoded_typedefs=False,
    get_returns="error",   # Only error implemented; error, value, void
)

## These members do not get normal accessors

skip_members = ["version", "type", "length", "stats_type", "len",
                "type_len", "actions_len", "_command"]

## Some OpenFlow string length constants
#
# These are a few length constants needed for array processing
ofp_constants = dict(
    OF_MAX_TABLE_NAME_LEN = 32,
    OF_MAX_PORT_NAME_LEN  = 16,
    OF_ETH_ALEN = 6,
    OF_DESC_STR_LEN   = 256,
    OF_SERIAL_NUM_LEN = 32
)

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
of_mixed_types = dict(
    of_port_no_t = {
        1: "uint16_t",
        2: "uint32_t",
        3: "uint32_t",
        4: "uint32_t",
        "short_name":"port_no"
        },
    of_port_desc_t = {
        1: "of_port_desc_t",
        2: "of_port_desc_t",
        3: "of_port_desc_t",
        4: "of_port_desc_t",
        "short_name":"port_desc"
        },
    of_fm_cmd_t = { # Flow mod command went from u16 to u8
        1: "uint16_t",
        2: "uint8_t",
        3: "uint8_t",
        4: "uint8_t",
        "short_name":"fm_cmd"
        },
    of_wc_bmap_t = { # Wildcard bitmap
        1: "uint32_t",
        2: "uint32_t",
        3: "uint64_t",
        4: "uint64_t",
        "short_name":"wc_bmap"
        },
    of_match_bmap_t = { # Match bitmap
        1: "uint32_t",
        2: "uint32_t",
        3: "uint64_t",
        4: "uint64_t",
        "short_name":"match_bmap"
        },
    of_match_t = { # Match object
        1: "of_match_v1_t",
        2: "of_match_v2_t",
        3: "of_match_v3_t",
        4: "of_match_v3_t",  # Currently uses same match as 1.2 (v3).
        "short_name":"match"
        },
)

## Base data types
#
# The basic types; Value is a list: bytes, to_wire, from_wire
# The accessors deal with endian, alignment and any other host/network
# considerations.  These are common across all versions
#
# For get accessors, assume we memcpy from wire buf and then apply ntoh
# For set accessors, assume we apply hton and then memcpy to wire buf
#
# to/from wire functions take a pointer to class and change in place
of_base_types = dict(
    char = dict(bytes=1, use_as_rv=1, short_name="char"),
    uint8_t = dict(bytes=1, use_as_rv=1, short_name="u8"),
    uint16_t = dict(bytes=2, to_w="u16_hton", from_w="u16_ntoh", use_as_rv=1,
                    short_name="u16"),
    uint32_t = dict(bytes=4, to_w="u32_hton", from_w="u32_ntoh", use_as_rv=1,
                    short_name="u32"),
    uint64_t = dict(bytes=8, to_w="u64_hton", from_w="u64_ntoh", use_as_rv=1,
                    short_name="u64"),
#    of_cookie_t = dict(bytes=8, to_w="u64_hton", from_w="u64_ntoh", use_as_rv=1#,
#                    short_name="cookie"),
#    of_counter_t = dict(bytes=8, to_w="u64_hton", from_w="u64_ntoh", use_as_rv=1,
#                    short_name="counter"),
    of_mac_addr_t = dict(bytes=6, short_name="mac"),
    of_ipv6_t = dict(bytes=16, short_name="ipv6"),
    of_port_name_t = dict(bytes=ofp_constants["OF_MAX_PORT_NAME_LEN"],
                          short_name="port_name"),
    of_table_name_t = dict(bytes=ofp_constants["OF_MAX_TABLE_NAME_LEN"],
                           short_name="tab_name"),
    of_desc_str_t = dict(bytes=ofp_constants["OF_DESC_STR_LEN"],
                         short_name="desc_str"),
    of_serial_num_t = dict(bytes=ofp_constants["OF_SERIAL_NUM_LEN"],
                           short_name="ser_num"),
    of_match_v1_t = dict(bytes=40, to_w="match_v1_hton",
                         from_w="match_v1_ntoh",
                         short_name="match_v1"),
    of_match_v2_t = dict(bytes=88, to_w="match_v2_hton",
                         from_w="match_v2_ntoh",
                         short_name="match_v2"),
    of_match_v3_t = dict(bytes=-1, to_w="match_v3_hton",
                         from_w="match_v3_ntoh",
                         short_name="match_v3"),
#    of_match_v4_t = dict(bytes=-1, to_w="match_v4_hton",
#                         from_w="match_v4_ntoh",
#                         short_name="match_v4"),
    of_octets_t = dict(bytes=-1, short_name="octets")
)

of_scalar_types = ["char", "uint8_t", "uint16_t", "uint32_t", "uint64_t",
                   "of_port_no_t", "of_fm_cmd_t", "of_wc_bmap_t",
                   "of_match_bmap_t", "of_port_name_t", "of_table_name_t",
                   "of_desc_str_t", "of_serial_num_t", "of_mac_addr_t",
                   "of_ipv6_t"]

base_object_members = """\
    /* The control block for the underlying data buffer */
    of_wire_object_t wire_object;
    /* The LOCI type enum value of the object */
    of_object_id_t object_id;

    /*
     * Objects need to track their "parent" so that updates to the
     * object that affect its length can be pushed to the parent.
     * Treat as private.
     */
    of_object_t *parent;

    /*
     * Not all objects have length and version on the wire so we keep
     * them here.  NOTE: Infrastructure manages length and version.
     * Treat length as private and version as read only.
     */
    int length;
    of_version_t version;

    /*
     * Many objects have a length and/or type represented in the wire buffer
     * These accessors get and set those value when present.  Treat as private.
     */
    of_wire_length_get_f wire_length_get;
    of_wire_length_set_f wire_length_set;
    of_wire_type_get_f wire_type_get;
    of_wire_type_set_f wire_type_set;

    of_object_track_info_t track_info;

    /*
     * Metadata available for applications.  Ensure 8-byte alignment, but
     * that buffer is at least as large as requested.  This data is not used
     * or inspected by LOCI.
     */
    uint64_t metadata[(OF_OBJECT_METADATA_BYTES + 7) / 8];
"""

# LOXI intermediate representation
# This is a hash from wire versions to OFProtocol objects.
# See loxi_ir.py

ir = {}

##
# LOXI identifiers
#
# Dict indexed by identifier name.  Each entry contains the information
# as a DotDict with the following keys:
# values: A dict indexed by wire version giving each verion's value or None
# common: The common value to use for this identifier at the LOXI top level (TBD)
# all_same: If True, all the values across all versions are the same
# ofp_name: The original name for the identifier
# ofp_group: The ofp enumerated type if defined

identifiers = {}

##
# Identifiers by original group
# Keys are the original group names.  Value is a list of LOXI identifiers

identifiers_by_group = {}

## Ordered list of class names
# This is per-wire-version and is a list of the classes in the order
# they appear in the file.  That is important because of the assumption
# that data members are defined before they are included in a superclass.
ordered_classes = {} # Indexed by wire version

## Per class ordered list of member names
ordered_members = {}

## Ordered list of message classes
ordered_messages = []

## Ordered list of non message classes
ordered_non_messages = []

## The objects that need list support
ordered_list_objects = []

## Stats request/reply are pseudo objects
ordered_pseudo_objects = []

## Standard order is normally messages followed by non-messages
standard_class_order = []

## All classes in order, including psuedo classes for which most code
# is not generated.
all_class_order = []

## Map from class, wire_version to size of fixed part of class
base_length = {}

## Boolean indication of variable length, per class, wire_version,
is_fixed_length = set()

## The global object ID counter
object_id = 1  # Reserve 0 for root object

## The unified view of all classes.  See internal readme.
unified = {}

## Indicates data members with non-fixed start offsets
# Indexed by (cls, version, member-name) and value is prev-member-name
special_offsets = {}

## Define Python variables with integer wire version values
VERSION_1_0 = 1
VERSION_1_1 = 2
VERSION_1_2 = 3
VERSION_1_3 = 4

# Ignore version for some functions
VERSION_ANY = -1

## @var supported_wire_protos
# The wire protocols this version of LoxiGen supports
supported_wire_protos = set([1, 2, 3, 4])
version_names = {1:"VERSION_1_0", 2:"VERSION_1_1", 3:"VERSION_1_2",
                 4:"VERSION_1_3"}
short_version_names = {1:"OF_1_0", 2:"OF_1_1", 3:"OF_1_2", 4:"OF_1_3"}
param_version_names = {1:"1.0", 2:"1.1", 3:"1.2", 4:"1.3"}

##
# Maps and ranges related to versioning

# For parameter version indications
of_param_version_map = {
    "1.0":VERSION_1_0,
    "1.1":VERSION_1_1,
    "1.2":VERSION_1_2,
    "1.3":VERSION_1_3
    }

# For parameter version indications
of_version_map = {
    "1.0":VERSION_1_0,
    "1.1":VERSION_1_1,
    "1.2":VERSION_1_2,
    "1.3":VERSION_1_3
    }

# The iteration object that gives the wire versions supported
of_version_range = [VERSION_1_0, VERSION_1_1, VERSION_1_2, VERSION_1_3]
of_version_max = VERSION_1_3


of_version_name2wire = dict(
    OF_VERSION_1_0=VERSION_1_0,
    OF_VERSION_1_1=VERSION_1_1,
    OF_VERSION_1_2=VERSION_1_2,
    OF_VERSION_1_3=VERSION_1_3
    )

of_version_wire2name = {
    VERSION_1_0:"OF_VERSION_1_0",
    VERSION_1_1:"OF_VERSION_1_1",
    VERSION_1_2:"OF_VERSION_1_2",
    VERSION_1_3:"OF_VERSION_1_3"
    }


################################################################
#
# Experimenters, vendors, extensions
#
# Although the term "experimenter" is used for identifying
# external extension definitions, we generally use the term
# extension when refering to the messages or objects themselves.
#
# Conventions:
#
# Extension messages should start with of_<experimenter>_
# Extension actions should start with of_<experimenter>_action_
# Extension instructions should start with of_<experimenter>_instructions_
#
# Currently, the above conventions are not enforced; the mapping
# is done brute force in type_maps.py
#
################################################################

# The map of known experimenters to their experimenter IDs
experimenter_name_to_id = dict(
    bsn = 0x005c16c7,
    nicira = 0x00002320,
    openflow = 0x000026e1
    )

def experimenter_name_lookup(experimenter_id):
    """
    Map an experimenter ID to its LOXI recognized name string
    """
    for name, id in of_g.experimenter_name_to_id.items():
        if id == experimenter_id:
            return name
    return None

################################################################
#
# Debug
#
################################################################

loxigen_dbg_file = sys.stdout
loxigen_log_file = sys.stdout

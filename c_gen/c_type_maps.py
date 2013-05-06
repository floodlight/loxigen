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
# @brief C code generation for LOXI type related maps
#

import of_g
import sys
from generic_utils import *
import loxi_front_end.oxm as oxm
import loxi_front_end.type_maps as type_maps


# Some number larger than small type values, but less then
# reserved values like 0xffff
max_type_value = 1000

def gen_object_id_to_type(out):
    out.write("""
/**
 * Map from object ID to primary wire type
 *
 * For messages, this is the header type; in particular for stats, this is
 * the common stats request/response type.  For per-stats types, use the
 * stats type map.  For things like actions, instructions or queue-props,
 * this gives the "sub type".
 */
""")
    for version in of_g.of_version_range:
        out.write("static const int\nof_object_to_type_map_v%d[OF_OBJECT_COUNT] = {\n"
                  %version)
        out.write("    -1, /* of_object, not a valid specific type */\n")
        for j, cls in enumerate(of_g.all_class_order):
            comma = ""
            if j < len(of_g.all_class_order) - 1: # Avoid ultimate comma
                comma = ","

            if cls in type_maps.stats_reply_list:
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_stats_reply", version)],
                           comma, cls))
            elif cls in type_maps.stats_request_list:
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_stats_request", version)],
                           comma, cls))
            elif cls in type_maps.flow_mod_list:
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_flow_mod", version)],
                           comma, cls))
            elif (cls, version) in type_maps.type_val:
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[(cls, version)], comma, cls))
            elif type_maps.message_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_experimenter", version)],
                           comma, cls))
            elif type_maps.action_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_action_experimenter",
                                               version)],
                           comma, cls))
            elif type_maps.action_id_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_action_id_experimenter",
                                               version)],
                           comma, cls))
            elif type_maps.instruction_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_instruction_experimenter",
                                               version)],
                           comma, cls))
            elif type_maps.queue_prop_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                          (type_maps.type_val[("of_queue_prop_experimenter",
                                               version)],
                           comma, cls))
            elif type_maps.table_feature_prop_is_extension(cls, version):
                out.write("    %d%s /* %s */\n" %
                    (type_maps.type_val[("of_table_feature_prop_experimenter",
                                         version)],
                     comma, cls))
            else:
                out.write("    -1%s /* %s (invalid) */\n" % (comma, cls))
        out.write("};\n\n")

    out.write("""
/**
 * Unified map, indexed by wire version which is 1-based.
 */
const int *const of_object_to_type_map[OF_VERSION_ARRAY_MAX] = {
    NULL,
""")
    for version in of_g.of_version_range:
        out.write("    of_object_to_type_map_v%d,\n" % version)
    out.write("""
};
""")

def gen_object_id_to_extension_data(out):
    out.write("""
/**
 * Extension data.
 * @fixme There must be a better way to represent this data
 */
""")
    for version in of_g.of_version_range:
        out.write("""
static const of_experimenter_data_t
of_object_to_extension_data_v%d[OF_OBJECT_COUNT] = {
""" % version)
        out.write("    {0, 0, 0}, /* of_object, not a valid specific type */\n")
        for j, cls in enumerate(of_g.all_class_order):
            comma = ""
            if j < len(of_g.all_class_order) - 1: # Avoid ultimate comma
                comma = ","

            if type_maps.class_is_extension(cls, version):
                exp_name = type_maps.extension_to_experimenter_macro_name(cls)
                subtype = type_maps.extension_to_subtype(cls, version)
                out.write("    {1, %s, %d}%s /* %s */\n" %
                          (exp_name, subtype, comma, cls))
            else:
                out.write("    {0, 0, 0}%s /* %s (non-extension) */\n" %
                          (comma, cls))
        out.write("};\n\n")

    out.write("""
/**
 * Unified map, indexed by wire version which is 1-based.
 */
const of_experimenter_data_t *const of_object_to_extension_data[OF_VERSION_ARRAY_MAX] = {
    NULL,
""")
    for version in of_g.of_version_range:
        out.write("    of_object_to_extension_data_v%d,\n" % version)
    out.write("""
};
""")

def gen_type_to_object_id(out, type_str, prefix, template,
                          value_array, max_val):
    """
    Generate C maps from various message class groups to object ids

    For each version, create an array mapping the type info to the
    object ID.  Then define an array containing those pointers.
    """

    # Create unified arrays and get length
    arr_len = type_maps.type_array_len(value_array, max_val)
    all_ars = []
    for version, val_dict in value_array.items(): # Per version dict
        ar = type_maps.dict_to_array(val_dict, max_val, type_maps.invalid_type)
        all_ars.append(ar)

    len_name = "%s_ITEM_COUNT" % prefix

    for i, ar in enumerate(all_ars):
        version = i + 1
        out.write("static const of_object_id_t\nof_%s_v%d[%s] = {\n" %
                  (type_str, version, len_name))
        for i in range(arr_len):
            comma = ""
            if i < arr_len - 1: # Avoid ultimate comma
                comma = ","

            # Per-version length check
            if i < len(ar):
                v = ar[i]
            else:
                v = type_maps.invalid_type

            if v == type_maps.invalid_type:
                out.write("    %-30s /* %d (Invalid) */\n" %
                          ("OF_OBJECT_INVALID" + comma, i))
            else:
                name = (template % v.upper()) + comma
                out.write("    %-30s /* %d */\n" % (name, i))
        out.write("};\n")

    out.write("""
/**
 * Maps from %(c_name)s wire type values to LOCI object ids
 *
 * Indexed by wire version which is 1-based.
 */

const of_object_id_t *const of_%(name)s[OF_VERSION_ARRAY_MAX] = {
    NULL,
""" % dict(name=type_str, c_name=prefix.lower()))
    for version in of_g.of_version_range:
        out.write("    of_%(name)s_v%(version)d,\n" % dict(name=type_str,
                                                           version=version))
    out.write("""
};

""" % dict(name=type_str, u_name=type_str.upper(),
           max_val=max_val, c_name=prefix.lower()))

def gen_type_maps(out):
    """
    Generate various type maps
    @param out The file handle to write to
    """

    out.write("#include <loci/loci.h>\n\n")

    # Generate maps from wire type values to object IDs
    gen_type_to_object_id(out, "action_type_to_id", "OF_ACTION",
                          "OF_ACTION_%s", type_maps.action_types,
                          max_type_value)
    gen_type_to_object_id(out, "action_id_type_to_id", "OF_ACTION_ID",
                          "OF_ACTION_ID_%s", type_maps.action_id_types,
                          max_type_value)
    gen_type_to_object_id(out, "instruction_type_to_id", "OF_INSTRUCTION",
                          "OF_INSTRUCTION_%s", type_maps.instruction_types,
                          max_type_value)
    gen_type_to_object_id(out, "queue_prop_type_to_id", "OF_QUEUE_PROP",
                          "OF_QUEUE_PROP_%s", type_maps.queue_prop_types,
                          max_type_value)
    gen_type_to_object_id(out, "table_feature_prop_type_to_id",
                          "OF_TABLE_FEATURE_PROP",
                          "OF_TABLE_FEATURE_PROP_%s",
                          type_maps.table_feature_prop_types,
                          max_type_value)
    gen_type_to_object_id(out, "meter_band_type_to_id", "OF_METER_BAND",
                          "OF_METER_BAND_%s", type_maps.meter_band_types,
                          max_type_value)
    gen_type_to_object_id(out, "hello_elem_type_to_id", "OF_HELLO_ELEM",
                          "OF_HELLO_ELEM_%s", type_maps.hello_elem_types,
                          max_type_value)

    # FIXME:  Multipart re-organization
    gen_type_to_object_id(out, "stats_request_type_to_id", "OF_STATS_REQUEST",
                          "OF_%s_STATS_REQUEST", type_maps.stats_types,
                          max_type_value)
    gen_type_to_object_id(out, "stats_reply_type_to_id", "OF_STATS_REPLY",
                          "OF_%s_STATS_REPLY", type_maps.stats_types,
                          max_type_value)
    gen_type_to_object_id(out, "flow_mod_type_to_id", "OF_FLOW_MOD",
                          "OF_FLOW_%s", type_maps.flow_mod_types,
                          max_type_value)
    gen_type_to_object_id(out, "oxm_type_to_id", "OF_OXM",
                          "OF_OXM_%s", type_maps.oxm_types, max_type_value)
    gen_type_to_object_id(out, "message_type_to_id", "OF_MESSAGE",
                          "OF_%s", type_maps.message_types, max_type_value)

    gen_object_id_to_type(out)
    gen_object_id_to_extension_data(out)
    # Don't need array mapping ID to stats types right now; handled directly
    # gen_object_id_to_stats_type(out)


def gen_type_to_obj_map_functions(out):
    """
    Generate the templated static inline type map functions
    @param out The file handle to write to
    """

    ################################################################
    # Generate all type-to-object-ID maps in a common way
    ################################################################
    map_template = """
/**
 * %(name)s wire type to object ID array.
 * Treat as private; use function accessor below
 */

extern const of_object_id_t *const of_%(name)s_type_to_id[OF_VERSION_ARRAY_MAX];

#define OF_%(u_name)s_ITEM_COUNT %(ar_len)d\n

/**
 * Map an %(name)s wire value to an OF object
 * @param %(name)s The %(name)s type wire value
 * @param version The version associated with the check
 * @return The %(name)s OF object type
 * @return OF_OBJECT_INVALID if type does not map to an object
 *
 */
static inline of_object_id_t
of_%(name)s_to_object_id(int %(name)s, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return OF_OBJECT_INVALID;
    }
    if (%(name)s < 0 || %(name)s >= OF_%(u_name)s_ITEM_COUNT) {
        return OF_OBJECT_INVALID;
    }

    return of_%(name)s_type_to_id[version][%(name)s];
}
"""
    map_with_experimenter_template = """
/**
 * %(name)s wire type to object ID array.
 * Treat as private; use function accessor below
 */

extern const of_object_id_t *const of_%(name)s_type_to_id[OF_VERSION_ARRAY_MAX];

#define OF_%(u_name)s_ITEM_COUNT %(ar_len)d\n

/**
 * Map an %(name)s wire value to an OF object
 * @param %(name)s The %(name)s type wire value
 * @param version The version associated with the check
 * @return The %(name)s OF object type
 * @return OF_OBJECT_INVALID if type does not map to an object
 *
 */
static inline of_object_id_t
of_%(name)s_to_object_id(int %(name)s, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return OF_OBJECT_INVALID;
    }
    if (%(name)s == OF_EXPERIMENTER_TYPE) {
        return OF_%(u_name)s_EXPERIMENTER;
    }
    if (%(name)s < 0 || %(name)s >= OF_%(u_name)s_ITEM_COUNT) {
        return OF_OBJECT_INVALID;
    }

    return of_%(name)s_type_to_id[version][%(name)s];
}
"""

    stats_template = """
/**
 * %(name)s wire type to object ID array.
 * Treat as private; use function accessor below
 */

extern const of_object_id_t *const of_%(name)s_type_to_id[OF_VERSION_ARRAY_MAX];

#define OF_%(u_name)s_ITEM_COUNT %(ar_len)d\n

/**
 * Map an %(name)s wire value to an OF object
 * @param %(name)s The %(name)s type wire value
 * @param version The version associated with the check
 * @return The %(name)s OF object type
 * @return OF_OBJECT_INVALID if type does not map to an object
 *
 */
static inline of_object_id_t
of_%(name)s_to_object_id(int %(name)s, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return OF_OBJECT_INVALID;
    }
    if (%(name)s == OF_EXPERIMENTER_TYPE) {
        return OF_EXPERIMENTER_%(u_name)s;
    }
    if (%(name)s < 0 || %(name)s >= OF_%(u_name)s_ITEM_COUNT) {
        return OF_OBJECT_INVALID;
    }

    return of_%(name)s_type_to_id[version][%(name)s];
}
"""
    # Experimenter mapping functions
    # Currently we support very few candidates, so we just do a
    # list of if/elses
    experimenter_function = """
/**
 * @brief Map a message known to be an exp msg to the proper object
 *
 * Assume that the message is a vendor/experimenter message.  Determine
 * the specific object type for the message.
 * @param msg An OF message object (uint8_t *)
 * @param length The number of bytes in the message (for error checking)
 * @param version Version of message
 * @returns object ID of specific type if recognized or OF_EXPERIMENTER if not
 *
 * @todo put OF_EXPERIMENTER_<name> in loci_base.h
 */

static inline of_object_id_t
of_message_experimenter_to_object_id(of_message_t msg, of_version_t version) {
    uint32_t experimenter_id;
    uint32_t subtype;

    /* Extract experimenter and subtype value; look for match from type maps */
    experimenter_id = of_message_experimenter_id_get(msg);
    subtype = of_message_experimenter_subtype_get(msg);

    /* Do a simple if/else search for the ver, experimenter and subtype */
"""
    first = True
    for version, experimenter_lists in type_maps.extension_message_subtype.items():
        for exp, subtypes in experimenter_lists.items():
            experimenter_function += """
    if ((experimenter_id == OF_EXPERIMENTER_ID_%(exp_name)s) &&
            (version == %(ver_name)s)) {
""" % dict(exp_name=exp.upper(), ver_name=of_g.wire_ver_map[version])
            for ext_msg, subtype in subtypes.items():
                experimenter_function += """
        if (subtype == %(subtype)s) {
            return %(ext_msg)s;
        }
""" % dict(subtype=subtype, ext_msg=ext_msg.upper())
            experimenter_function += """
    }
"""
    experimenter_function += """
    return OF_EXPERIMENTER;
}
"""

    # Message need different handling
    msg_template = """
/**
 * %(name)s wire type to object ID array.
 * Treat as private; use function accessor below
 */

extern const of_object_id_t *const of_%(name)s_type_to_id[OF_VERSION_ARRAY_MAX];

#define OF_%(u_name)s_ITEM_COUNT %(ar_len)d\n

/**
 * Extract the type info from the message and determine its object type
 * @param msg An OF message object (uint8_t *)
 * @param length The number of bytes in the message (for error checking)
 * @returns object ID or OF_OBJECT_INVALID if parse error
 */

static inline of_object_id_t
of_message_to_object_id(of_message_t msg, int length) {
    uint8_t type;
    of_version_t ver;
    of_object_id_t obj_id;
    uint16_t stats_type;
    uint8_t flow_mod_cmd;

    if (length < OF_MESSAGE_MIN_LENGTH) {
        return OF_OBJECT_INVALID;
    }
    type = of_message_type_get(msg);
    ver = of_message_version_get(msg);
    if (!OF_VERSION_OKAY(ver)) {
        return OF_OBJECT_INVALID;
    }

    if (type >= OF_MESSAGE_ITEM_COUNT) {
        return OF_OBJECT_INVALID;
    }

    obj_id = of_message_type_to_id[ver][type];

    /* Remap to specific message if known */
    if (obj_id == OF_EXPERIMENTER) {
        if (length < OF_MESSAGE_EXPERIMENTER_MIN_LENGTH) {
            return OF_OBJECT_INVALID;
        }
        return of_message_experimenter_to_object_id(msg, ver);
    }

    /* Remap to add/delete/strict version */
    if (obj_id == OF_FLOW_MOD) {
        if (length < OF_MESSAGE_MIN_FLOW_MOD_LENGTH(ver)) {
            return OF_OBJECT_INVALID;
        }
        flow_mod_cmd = of_message_flow_mod_command_get(msg, ver);
        obj_id = of_flow_mod_to_object_id(flow_mod_cmd, ver);
    }

    if ((obj_id == OF_STATS_REQUEST) || (obj_id == OF_STATS_REPLY)) {
        if (length < OF_MESSAGE_MIN_STATS_LENGTH) {
            return OF_OBJECT_INVALID;
        }
        stats_type = of_message_stats_type_get(msg);
        if (obj_id == OF_STATS_REQUEST) {
            obj_id = of_stats_request_to_object_id(stats_type, ver);
        } else {
            obj_id = of_stats_reply_to_object_id(stats_type, ver);
        }
    }

    return obj_id;
}
"""

    # Action types array gen
    ar_len = type_maps.type_array_len(type_maps.action_types, max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="action", u_name="ACTION", ar_len=ar_len))

    # Action ID types array gen
    ar_len = type_maps.type_array_len(type_maps.action_id_types, max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="action_id", u_name="ACTION_ID", ar_len=ar_len))

    # Instruction types array gen
    ar_len = type_maps.type_array_len(type_maps.instruction_types,
                                      max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="instruction", u_name="INSTRUCTION", ar_len=ar_len))

    # Queue prop types array gen
    ar_len = type_maps.type_array_len(type_maps.queue_prop_types,
                                      max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="queue_prop", u_name="QUEUE_PROP", ar_len=ar_len))

    # Table feature prop types array gen
    ar_len = type_maps.type_array_len(type_maps.table_feature_prop_types,
                                      max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="table_feature_prop", u_name="TABLE_FEATURE_PROP",
                   ar_len=ar_len))

    # Meter band types array gen
    ar_len = type_maps.type_array_len(type_maps.meter_band_types,
                                      max_type_value)
    out.write(map_with_experimenter_template %
              dict(name="meter_band", u_name="METER_BAND", ar_len=ar_len))

    # Hello elem types array gen
    ar_len = type_maps.type_array_len(type_maps.hello_elem_types,
                                      max_type_value)
    out.write(map_template %
              dict(name="hello_elem", u_name="HELLO_ELEM", ar_len=ar_len))

    # Stats types array gen
    ar_len = type_maps.type_array_len(type_maps.stats_types,
                                      max_type_value)
    out.write(stats_template %
              dict(name="stats_reply", u_name="STATS_REPLY", ar_len=ar_len))
    out.write(stats_template %
              dict(name="stats_request", u_name="STATS_REQUEST",
                   ar_len=ar_len))

    ar_len = type_maps.type_array_len(type_maps.flow_mod_types, max_type_value)
    out.write(map_template %
              dict(name="flow_mod", u_name="FLOW_MOD", ar_len=ar_len))

    ar_len = type_maps.type_array_len(type_maps.oxm_types, max_type_value)
    out.write("""
/* NOTE: We could optimize the OXM and only generate OF 1.2 versions. */
""")
    out.write(map_template %
              dict(name="oxm", u_name="OXM", ar_len=ar_len))

    out.write(experimenter_function)
    # Must follow stats reply/request
    ar_len = type_maps.type_array_len(type_maps.message_types, max_type_value)
    out.write(msg_template %
              dict(name="message", u_name="MESSAGE", ar_len=ar_len))

def gen_obj_to_type_map_functions(out):
    """
    Generate the static line maps from object IDs to types
    @param out The file handle to write to
    """

    ################################################################
    # Generate object ID to primary type map
    ################################################################

    out.write("""
extern const int *const of_object_to_type_map[OF_VERSION_ARRAY_MAX];

/**
 * Map an object ID to its primary wire type value
 * @param id An object ID
 * @return For message objects, the type value in the OpenFlow header
 * @return For non-message objects such as actions, instructions, OXMs
 * returns the type value that appears in the respective sub-header
 * @return -1 For improper version or out of bounds input
 *
 * NOTE that for stats request/reply, returns the header type, not the
 * sub-type
 *
 * Also, note that the value is returned as a signed integer.  So -1 is
 * an error code, while 0xffff is the usual "experimenter" code.
 */
static inline int
of_object_to_wire_type(of_object_id_t id, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return -1;
    }
    if (id < 0 || id >= OF_OBJECT_COUNT) {
        return -1;
    }
    return of_object_to_type_map[version][id];
}

""")

    # Now for experimenter ids
    out.write("""
/**
 * Map from object ID to a triple, (is_extension, experimenter id, subtype)
 */
""")
    out.write("""
typedef struct of_experimenter_data_s {
    int is_extension;  /* Boolean indication that this is an extension */
    uint32_t experimenter_id;
    uint32_t subtype;
} of_experimenter_data_t;

""")

    out.write("""
extern const of_experimenter_data_t *const of_object_to_extension_data[OF_VERSION_ARRAY_MAX];

/**
 * Map from the object ID of an extension to the experimenter ID
 */
static inline uint32_t
of_extension_to_experimenter_id(of_object_id_t obj_id, of_version_t ver)
{
    if (obj_id < 0 || obj_id > OF_OBJECT_COUNT) {
        return (uint32_t) -1;
    }
    /* @fixme: Verify ver? */
    return of_object_to_extension_data[ver][obj_id].experimenter_id;
}

/**
 * Map from the object ID of an extension to the experimenter subtype
 */
static inline uint32_t
of_extension_to_experimenter_subtype(of_object_id_t obj_id, of_version_t ver)
{
    if (obj_id < 0 || obj_id > OF_OBJECT_COUNT) {
        return (uint32_t) -1;
    }
    /* @fixme: Verify ver? */
    return of_object_to_extension_data[ver][obj_id].subtype;
}

/**
 * Boolean function indicating the the given object ID/version
 * is recognized as a supported (decode-able) extension.
 */
static inline int
of_object_id_is_extension(of_object_id_t obj_id, of_version_t ver)
{
    if (obj_id < 0 || obj_id > OF_OBJECT_COUNT) {
        return (uint32_t) -1;
    }
    /* @fixme: Verify ver? */
    return of_object_to_extension_data[ver][obj_id].is_extension;
}
""")

    ################################################################
    # Generate object ID to the stats sub-type map
    ################################################################

    out.write("""
/**
 * Map an object ID to a stats type
 * @param id An object ID
 * @return The wire value for the stats type
 * @return -1 if not supported for this version
 * @return -1 if id is not a specific stats type ID
 *
 * Note that the value is returned as a signed integer.  So -1 is
 * an error code, while 0xffff is the usual "experimenter" code.
 */

static inline int
of_object_to_stats_type(of_object_id_t id, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return -1;
    }
    switch (id) {
""")
    # Assumes 1.2 contains all stats types and type values are
    # the same across all versions
    stats_names = dict()
    for ver in of_g.of_version_range:
        for name, value in type_maps.stats_types[ver].items():
            if name in stats_names and (not value == stats_names[name]):
                print "ERROR stats type differ violating assumption"
                sys.exit(1)
            stats_names[name] = value

    for name, value in stats_names.items():
        out.write("    case OF_%s_STATS_REPLY:\n" % name.upper())
        out.write("    case OF_%s_STATS_REQUEST:\n" % name.upper())
        for version in of_g.of_version_range:
            if not name in type_maps.stats_types[version]:
                out.write("        if (version == %s) break;\n" %
                          of_g.of_version_wire2name[version])
        out.write("        return %d;\n" % value)
    out.write("""
    default:
        break;
    }
    return -1; /* Not recognized as stats type object for this version */
}
""")

    ################################################################
    # Generate object ID to the flow mod sub-type map
    ################################################################

    out.write("""
/**
 * Map an object ID to a flow-mod command value
 * @param id An object ID
 * @return The wire value for the flow-mod command
 * @return -1 if not supported for this version
 * @return -1 if id is not a specific stats type ID
 *
 * Note that the value is returned as a signed integer.  So -1 is
 * an error code, while 0xffff is the usual "experimenter" code.
 */

static inline int
of_object_to_flow_mod_command(of_object_id_t id, of_version_t version)
{
    if (!OF_VERSION_OKAY(version)) {
        return -1;
    }
    switch (id) {
""")
    # Assumes 1.2 contains all stats types and type values are
    # the same across all versions
    flow_mod_names = dict()
    for ver in of_g.of_version_range:
        for name, value in type_maps.flow_mod_types[ver].items():
            if name in flow_mod_names and \
                    (not value == flow_mod_names[name]):
                print "ERROR flow mod command differ violating assumption"
                sys.exit(1)
            flow_mod_names[name] = value

    for name, value in flow_mod_names.items():
        out.write("    case OF_FLOW_%s:\n" % name.upper())
        for version in of_g.of_version_range:
            if not name in type_maps.flow_mod_types[version]:
                out.write("        if (version == %s) break;\n" %
                          of_g.of_version_wire2name[version])
        out.write("        return %d;\n" % value)
    out.write("""
    default:
        break;
    }
    return -1; /* Not recognized as flow mod type object for this version */
}

""")

def gen_type_maps_header(out):
    """
    Generate various header file declarations for type maps
    @param out The file handle to write to
    """

    out.write("""
/**
 * Generic experimenter type value.  Applies to all except
 * top level message: Action, instruction, error, stats, queue_props, oxm
 */
#define OF_EXPERIMENTER_TYPE 0xffff
""")
    gen_type_to_obj_map_functions(out)
    gen_obj_to_type_map_functions(out)

    out.write("extern const int *const of_object_fixed_len[OF_VERSION_ARRAY_MAX];\n")

    out.write("""
/**
 * Map a message in a wire buffer object to its OF object id.
 * @param wbuf Pointer to a wire buffer object, populated with an OF message
 * @returns The object ID of the message
 * @returns OF_OBJECT_INVALID if unable to parse the message type
 */

static inline of_object_id_t
of_wire_object_id_get(of_wire_buffer_t *wbuf)
{
    of_message_t msg;

    msg = (of_message_t)WBUF_BUF(wbuf);
    return of_message_to_object_id(msg, WBUF_CURRENT_BYTES(wbuf));
}

/**
 * Use the type/length from the wire buffer and init the object
 * @param obj The object being initialized
 * @param base_object_id If > 0, this indicates the base object
 * @param max_len If > 0, the max length to expect for the obj
 * type for inheritance checking
 * @return OF_ERROR_
 *
 * Used for inheritance type objects such as actions and OXMs
 * The type is checked and if valid, the object is initialized.
 * Then the length is taken from the buffer.
 *
 * Note that the object version must already be properly set.
 */
static inline int
of_object_wire_init(of_object_t *obj, of_object_id_t base_object_id,
                    int max_len)
{
    if (obj->wire_type_get != NULL) {
        of_object_id_t id;
        obj->wire_type_get(obj, &id);
        if (!of_wire_id_valid(id, base_object_id)) {
            return OF_ERROR_PARSE;
        }
        obj->object_id = id;
        /* Call the init function for this object type; do not push to wire */
        of_object_init_map[id]((of_object_t *)(obj), obj->version, -1, 0);
    }
    if (obj->wire_length_get != NULL) {
        int length;
        obj->wire_length_get(obj, &length);
        if (length < 0 || (max_len > 0 && length > max_len)) {
            return OF_ERROR_PARSE;
        }
        obj->length = length;
    } else {
        /* @fixme Does this cover everything else? */
        obj->length = of_object_fixed_len[obj->version][base_object_id];
    }

    return OF_ERROR_NONE;
}

""")

    # Generate the function that sets the object type fields
    out.write("""

/**
 * Map a message in a wire buffer object to its OF object id.
 * @param wbuf Pointer to a wire buffer object, populated with an OF message
 * @returns The object ID of the message
 * @returns OF_OBJECT_INVALID if unable to parse the message type
 *
 * Version must be set in the buffer prior to calling this routine
 */

static inline int
of_wire_message_object_id_set(of_wire_buffer_t *wbuf, of_object_id_t id)
{
    int type;
    of_version_t ver;
    of_message_t msg;

    msg = (of_message_t)WBUF_BUF(wbuf);

    ver = of_message_version_get(msg);

    /* ASSERT(id is a message object) */

    if ((type = of_object_to_wire_type(id, ver)) < 0) {
        return OF_ERROR_PARAM;
    }
    of_message_type_set(msg, type);

    if ((type = of_object_to_stats_type(id, ver)) >= 0) {
        /* It's a stats obj */
        of_message_stats_type_set(msg, type);
    }
    if ((type = of_object_to_flow_mod_command(id, ver)) >= 0) {
        /* It's a flow mod obj */
        of_message_flow_mod_command_set(msg, ver, type);
    }
    if (of_object_id_is_extension(id, ver)) {
        uint32_t val32;

        /* Set the experimenter and subtype codes */
        val32 = of_extension_to_experimenter_id(id, ver);
        of_message_experimenter_id_set(msg, val32);
        val32 = of_extension_to_experimenter_subtype(id, ver);
        of_message_experimenter_subtype_set(msg, val32);
    }

    return OF_ERROR_NONE;
}
""")

def gen_type_data_header(out):

    out.write("""
/****************************************************************
 *
 * The following declarations are for type and length calculations.
 * Implementations may be found in of_type_maps.c
 *
 ****************************************************************/
/*
 * Special case length functions for objects with
 */
""")
    for ((cls, name), prev) in of_g.special_offsets.items():
        s_cls = cls[3:] # take off of_
        out.write("""
/**
 * Special length calculation for %(cls)s->%(name)s.
 * @param obj An object of type %(cls)s to check for
 * length of %(name)s
 * @param bytes[out] Where to store the calculated length
 *
 * Preceding data member is %(prev)s.
 */
extern int of_length_%(s_cls)s_%(name)s_get(
    %(cls)s_t *obj, int *bytes);

/**
 * Special offset calculation for %(cls)s->%(name)s.
 * @param obj An object of type %(cls)s to check for
 * length of %(name)s
 * @param offset[out] Where to store the calculated length
 *
 * Preceding data member is %(prev)s.
 */
extern int of_offset_%(s_cls)s_%(name)s_get(
    %(cls)s_t *obj, int *offset);
""" % dict(cls=cls, s_cls=s_cls, name=name, prev=prev))

# NOT NEEDED YET
#     # For non-message, variable length objects, give a fun that
#     # calculates the length
#     for cls in of_g.standard_class_order:
#         s_cls = cls[3:] # take off of_
#         if !type_is_var_len(cls, version):
#             continue
#         out.write("""
# /**
#  * Special length calculation for variable length object %(cls)s
#  * @param obj An object of type %(cls)s whose length is being calculated
#  * @param bytes[out] Where to store the calculated length
#  *
#  * The assumption is that the length member of the object is not
#  * valid and the length needs to be calculated from other information
#  * such as the parent.
#  */
# extern int of_length_%(s_cls)s_get(
#     %(cls)s_t *obj, int *bytes);
# """ % dict(cls=cls, s_cls=s_cls))

    out.write("""
/****************************************************************
 * Wire type/length functions.
 ****************************************************************/

extern void of_object_message_wire_length_get(of_object_t *obj, int *bytes);
extern void of_object_message_wire_length_set(of_object_t *obj, int bytes);

extern void of_oxm_wire_length_get(of_object_t *obj, int *bytes);
extern void of_oxm_wire_length_set(of_object_t *obj, int bytes);
extern void of_oxm_wire_object_id_get(of_object_t *obj, of_object_id_t *id);
extern void of_oxm_wire_object_id_set(of_object_t *obj, of_object_id_t id);

extern void of_tlv16_wire_length_get(of_object_t *obj, int *bytes);
extern void of_tlv16_wire_length_set(of_object_t *obj, int bytes);

extern void of_tlv16_wire_object_id_set(of_object_t *obj, of_object_id_t id);

/* Wire length is uint16 at front of structure */
extern void of_u16_len_wire_length_get(of_object_t *obj, int *bytes);
extern void of_u16_len_wire_length_set(of_object_t *obj, int bytes);

extern void of_action_wire_object_id_get(of_object_t *obj, of_object_id_t *id);
extern void of_action_id_wire_object_id_get(of_object_t *obj, of_object_id_t *id);
extern void of_instruction_wire_object_id_get(of_object_t *obj,
    of_object_id_t *id);
extern void of_queue_prop_wire_object_id_get(of_object_t *obj,
    of_object_id_t *id);
extern void of_table_feature_prop_wire_object_id_get(of_object_t *obj,
    of_object_id_t *id);
extern void of_meter_band_wire_object_id_get(of_object_t *obj,
    of_object_id_t *id);
extern void of_hello_elem_wire_object_id_get(of_object_t *obj,
    of_object_id_t *id);

/** @fixme VERIFY LENGTH IS NUMBER OF BYTES OF ENTRY INCLUDING HDR */
#define OF_OXM_MASKED_TYPE_GET(hdr) (((hdr) >> 8) & 0xff)
#define OF_OXM_MASKED_TYPE_SET(hdr, val)                    \\
    (hdr) = ((hdr) & 0xffff00ff) + (((val) & 0xff) << 8)

#define OF_OXM_LENGTH_GET(hdr) ((hdr) & 0xff)
#define OF_OXM_LENGTH_SET(hdr, val)                         \\
    (hdr) = ((hdr) & 0xffffff00) + ((val) & 0xff)

extern void of_packet_queue_wire_length_get(of_object_t *obj, int *bytes);
extern void of_packet_queue_wire_length_set(of_object_t *obj, int bytes);

extern void of_list_meter_band_stats_wire_length_get(of_object_t *obj,
                                                    int *bytes);
extern void of_meter_stats_wire_length_get(of_object_t *obj, int *bytes);
extern void of_meter_stats_wire_length_set(of_object_t *obj, int bytes);
extern int of_extension_object_wire_push(of_object_t *obj);

""")


def gen_length_array(out):
    """
    Generate an array giving the lengths of all objects/versions
    @param out The file handle to which to write
    """
    out.write("""
/**
 * An array with the number of bytes in the fixed length part
 * of each OF object
 */
""")

    for version in of_g.of_version_range:
        out.write("""
static const int\nof_object_fixed_len_v%d[OF_OBJECT_COUNT] = {
    -1,   /* of_object is not instantiable */
""" % version)
        for i, cls in enumerate(of_g.all_class_order):
            comma = ","
            if i == len(of_g.all_class_order) - 1:
                comma = ""
            val = "-1" + comma
            if (cls, version) in of_g.base_length:
                val = str(of_g.base_length[(cls, version)]) + comma
            out.write("    %-5s /* %d: %s */\n" % (val, i + 1, cls))
        out.write("};\n")

    out.write("""
/**
 * Unified map of fixed length part of each object
 */
const int *const of_object_fixed_len[OF_VERSION_ARRAY_MAX] = {
    NULL,
""")
    for version in of_g.of_version_range:
        out.write("    of_object_fixed_len_v%d,\n" % version)
    out.write("""
};
""")


################################################################
################################################################

# THIS IS PROBABLY NOT NEEDED AND MAY NOT BE CALLED CURRENTLY
def gen_object_id_to_stats_type(out):
    out.write("""
/**
 * Map from message object ID to stats type
 *
 * All message object IDs are mapped for simplicity
 */
""")
    for version in of_g.of_version_range:
        out.write("const int *of_object_to_stats_type_map_v%d = {\n" % (i+1))
        out.write("    -1, /* of_object (invalid) */\n");
        for cls in of_g.ordered_messages:
            name = cls[3:]
            name = name[:name.find("_stats")]
            if (((cls in type_maps.stats_reply_list) or
                 (cls in type_maps.stats_request_list)) and
                name in type_maps.stats_types[i]):
                out.write("    %d, /* %s */\n" %
                          (type_maps.stats_types[i][name], cls))
            else:
                out.write("    -1, /* %s (invalid) */\n" % cls)
        out.write("};\n\n")

    out.write("""
/**
 * Unified map, indexed by wire version which is 1-based.
 */
const int *of_object_to_stats_type_map[OF_VERSION_ARRAY_MAX] = {
    NULL,
""")
    for version in of_g.of_version_range:
        out.write("    of_object_to_stats_type_map_v%d,\n" % version)
    out.write("""
};
""")


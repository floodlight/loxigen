#!/usr/bin/env python
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

"""
@brief
Process openflow header files to create language specific LOXI interfaces

First cut at simple python script for processing input files

Internal notes

An input file for each supported OpenFlow version is passed in
on the command line.

Expected input file format:

These will probably be collapsed into a python dict or something

The first line has the ofC version identifier and nothing else
The second line has the openflow wire protocol value and nothing else

The main content is struct elements for each OF recognized class.
These are taken from current versions of openflow.h but are modified
a bit.  See Overview for more information.

Class canonical form:   A list of entries, each of which is a
pair "type, name;".  The exception is when type is the keyword
'list' in which the syntax is "list(type) name;".

From this, internal representations are generated:  For each
version, a dict indexed by class name.  One element (members) is
an array giving the member name and type.  From this, wire offsets
can be calculated.


@fixme Clean up the lang module architecture.  It should provide a
list of files that it wants to generate and maps to the filenames,
subdirectory names and generation functions.  It should also be
defined as a class, probably with the constructor taking the
language target.

@fixme Clean up global data structures such as versions and of_g
structures.  They should probably be a class or classes as well.

"""

import sys

import re
import string
import os
import glob
import copy
import collections
import of_g
import loxi_front_end.type_maps as type_maps
import loxi_utils.loxi_utils as loxi_utils
import loxi_front_end.c_parse_utils as c_parse_utils
import loxi_front_end.identifiers as identifiers
import pyparsing
import loxi_front_end.parser as parser
import loxi_front_end.translation as translation
import loxi_front_end.frontend as frontend
from loxi_ir import *
from generic_utils import *

root_dir = os.path.dirname(os.path.realpath(__file__))

# TODO:  Put these in a class so they get documented

## Dict indexed by version giving all info related to version
#
# This is local; after processing, the information is stored in
# of_g variables.
versions = {}

def config_sanity_check():
    """
    Check the configuration for basic consistency

    @fixme Needs update for generic language support
    """

    rv = True
    # For now, only "error" supported for get returns
    if config_check("copy_semantics") != "read":
        debug("Only 'read' is supported for copy_semantics");
        rv = False
    if config_check("get_returns") != "error":
        debug("Only 'error' is supported for get-accessor return types\m");
        rv = False
    if not config_check("use_fn_ptrs") and not config_check("gen_unified_fns"):
        debug("Must have gen_fn_ptrs and/or gen_unified_fns set in config")
        rv = False
    if config_check("use_obj_id"):
        debug("use_obj_id is set but not yet supported (change \
config_sanity_check if it is)")
        rv = False
    if config_check("gen_unified_macros") and config_check("gen_unified_fns") \
            and config_check("gen_unified_macro_lower"):
        debug("Conflict: Cannot generate unified functions and lower case \
unified macros")
        rv = False

    return rv

def add_class(wire_version, cls, members):
    """
    Process a class for the given version and update the unified
    list of classes as needed.

    @param wire_version The wire version for this class defn
    @param cls The name of the class being added
    @param members The list of members with offsets calculated
    """
    memid = 0

    sig = loxi_utils.class_signature(members)
    if cls in of_g.unified:
        uc = of_g.unified[cls]
        if wire_version in uc:
            debug("Error adding %s to unified. Wire ver %d exists" %
                  (cls, wire_version))
            sys.exit(1)
        uc[wire_version] = {}
        # Check for a matching signature
        for wver in uc:
            if type(wver) != type(0): continue
            if wver == wire_version: continue
            if not "use_version" in uc[wver]:
                if sig == loxi_utils.class_signature(uc[wver]["members"]):
                    log("Matched %s, ver %d to ver %d" %
                          (cls, wire_version, wver))
                    # have a match with existing version
                    uc[wire_version]["use_version"] = wver
                    # What else to do?
                    return
    else:  # Haven't seen this entry before
        log("Adding %s to unified list, ver %d" % (cls, wire_version))
        of_g.unified[cls] = dict(union={})
        uc = of_g.unified[cls]

    # At this point, need to add members for this version
    uc[wire_version] = dict(members = members)

    # Per member processing:
    #  Add to union list (I'm sure there's a better way)
    #  Check if it's a list
    union = uc["union"]
    if not cls in of_g.ordered_members:
        of_g.ordered_members[cls] = []
    for member in members:
        m_name = member["name"]
        m_type = member["m_type"]
        if m_name.find("pad") == 0:
            continue
        if m_name in union:
            if not m_type == union[m_name]["m_type"]:
                debug("ERROR:   CLASS: %s. VERSION %d. MEMBER: %s. TYPE: %s" %
                      (cls, wire_version, m_name, m_type))
                debug("    Type conflict adding member to unified set.")
                debug("    Current union[%s]:" % m_name)
                debug(union[m_name])
                sys.exit(1)
        else:
            union[m_name] = dict(m_type=m_type, memid=memid)
            memid += 1
        if not m_name in of_g.ordered_members[cls]:
            of_g.ordered_members[cls].append(m_name)

def update_offset(cls, wire_version, name, offset, m_type):
    """
    Update (and return) the offset based on type.
    @param cls The parent class
    @param wire_version The wire version being processed
    @param name The name of the data member
    @param offset The current offset
    @param m_type The type declaration being processed
    @returns A pair (next_offset, len_update)  next_offset is the new offset
    of the next object or -1 if this is a var-length object.  len_update
    is the increment that should be added to the length.  Note that (for
    of_match_v3) it is variable length, but it adds 8 bytes to the fixed
    length of the object
    If offset is already -1, do not update
    Otherwise map to base type and count and update (if possible)
    """
    if offset < 0:    # Don't update offset once set to -1
        return offset, 0

    count, base_type = c_parse_utils.type_dec_to_count_base(m_type)

    len_update = 0
    if base_type in of_g.of_mixed_types:
        base_type = of_g.of_mixed_types[base_type][wire_version]

    base_class = base_type[:-2]
    if (base_class, wire_version) in of_g.is_fixed_length:
        bytes = of_g.base_length[(base_class, wire_version)]
    else:
        if base_type == "of_match_v3_t":
            # This is a special case: it has non-zero min length
            # but is variable length
            bytes = -1
            len_update = 8
        elif base_type in of_g.of_base_types:
            bytes = of_g.of_base_types[base_type]["bytes"]
        else:
            print "UNKNOWN TYPE for %s %s: %s" % (cls, name, base_type)
            log("UNKNOWN TYPE for %s %s: %s" % (cls, name, base_type))
            bytes = -1

    # If bytes
    if bytes > 0:
        len_update = count * bytes

    if bytes == -1:
        return -1, len_update

    return offset + (count * bytes), len_update

def calculate_offsets_and_lengths(ordered_classes, classes, wire_version):
    """
    Generate the offsets for fixed offset class members
    Also calculate the class_sizes when possible.

    @param classes The classes to process
    @param wire_version The wire version for this set of classes

    Updates global variables
    """

    lists = set()

    # Generate offsets
    for cls in ordered_classes:
        fixed_offset = 0 # The last "good" offset seen
        offset = 0
        last_offset = 0
        last_name = "-"
        for member in classes[cls]:
            m_type = member["m_type"]
            name = member["name"]
            if last_offset == -1:
                if name == "pad":
                    log("Skipping pad for special offset for %s" % cls)
                else:
                    log("SPECIAL OFS: Member %s (prev %s), class %s ver %d" %
                          (name, last_name, cls, wire_version))
                    if (((cls, name) in of_g.special_offsets) and
                        (of_g.special_offsets[(cls, name)] != last_name)):
                        debug("ERROR: special offset prev name changed")
                        debug("  cls %s. name %s. version %d. was %s. now %s" %
                              cls, name, wire_version,
                              of_g.special_offsets[(cls, name)], last_name)
                        sys.exit(1)
                    of_g.special_offsets[(cls, name)] = last_name

            member["offset"] = offset
            if m_type.find("list(") == 0:
                (list_name, base_type) = loxi_utils.list_name_extract(m_type)
                lists.add(list_name)
                member["m_type"] = list_name + "_t"
                offset = -1
            elif m_type.find("struct") == 0:
                debug("ERROR found struct: %s.%s " % (cls, name))
                sys.exit(1)
            elif m_type == "octets":
                log("offset gen skipping octets: %s.%s " % (cls, name))
                offset = -1
            else:
                offset, len_update = update_offset(cls, wire_version, name,
                                                  offset, m_type)
                if offset != -1:
                    fixed_offset = offset
                else:
                    fixed_offset += len_update
                    log("offset is -1 for %s.%s version %d " %
                        (cls, name, wire_version))
            last_offset = offset
            last_name = name
        of_g.base_length[(cls, wire_version)] = fixed_offset
        if (offset != -1):
            of_g.is_fixed_length.add((cls, wire_version))
    for list_type in lists:
        classes[list_type] = []
        of_g.ordered_classes[wire_version].append(list_type)
        of_g.base_length[(list_type, wire_version)] = 0

def process_input_file(filename):
    """
    Process an input file

    Does not modify global state.

    @param filename The input filename

    @returns An OFInput object
    """

    # Parse the input file
    try:
        with open(filename, 'r') as f:
            ast = parser.parse(f.read())
    except pyparsing.ParseBaseException as e:
        print "Parse error in %s: %s" % (os.path.basename(filename), str(e))
        sys.exit(1)

    # Create the OFInput from the AST
    try:
        ofinput = frontend.create_ofinput(ast)
    except frontend.InputError as e:
        print "Error in %s: %s" % (os.path.basename(filename), str(e))
        sys.exit(1)

    return ofinput

def order_and_assign_object_ids():
    """
    Order all classes and assign object ids to all classes.

    This is done to promote a reasonable order of the objects, putting
    messages first followed by non-messages.  No assumptions should be
    made about the order, nor about contiguous numbering.  However, the
    numbers should all be reasonably small allowing arrays indexed by
    these enum values to be defined.
    """

    # Generate separate message and non-message ordered lists
    for cls in of_g.unified:
        if loxi_utils.class_is_message(cls):
            of_g.ordered_messages.append(cls)
        elif loxi_utils.class_is_list(cls):
            of_g.ordered_list_objects.append(cls)
        else:
            of_g.ordered_non_messages.append(cls)

    of_g.ordered_messages.sort()
    of_g.ordered_pseudo_objects.sort()
    of_g.ordered_non_messages.sort()
    of_g.ordered_list_objects.sort()
    of_g.standard_class_order.extend(of_g.ordered_messages)
    of_g.standard_class_order.extend(of_g.ordered_non_messages)
    of_g.standard_class_order.extend(of_g.ordered_list_objects)

    # This includes pseudo classes for which most code is not generated
    of_g.all_class_order.extend(of_g.ordered_messages)
    of_g.all_class_order.extend(of_g.ordered_non_messages)
    of_g.all_class_order.extend(of_g.ordered_list_objects)
    of_g.all_class_order.extend(of_g.ordered_pseudo_objects)

    # Assign object IDs
    for cls in of_g.ordered_messages:
        of_g.unified[cls]["object_id"] = of_g.object_id
        of_g.object_id += 1
    for cls in of_g.ordered_non_messages:
        of_g.unified[cls]["object_id"] = of_g.object_id
        of_g.object_id += 1
    for cls in of_g.ordered_list_objects:
        of_g.unified[cls]["object_id"] = of_g.object_id
        of_g.object_id += 1
    for cls in of_g.ordered_pseudo_objects:
        of_g.unified[cls] = {}
        of_g.unified[cls]["object_id"] = of_g.object_id
        of_g.object_id += 1


def initialize_versions():
    """
    Create an empty datastructure for each target version.
    """

    for wire_version in of_g.target_version_list:
        version_name = of_g.of_version_wire2name[wire_version]
        of_g.wire_ver_map[wire_version] = version_name
        versions[version_name] = dict(
            version_name = version_name,
            wire_version = wire_version,
            classes = {})
        of_g.ordered_classes[wire_version] = []


def read_input():
    """
    Read in from files given on command line and update global state

    @fixme Should select versions to support from command line
    """

    ofinputs_by_version = collections.defaultdict(lambda: [])
    filenames = sorted(glob.glob("%s/openflow_input/*" % root_dir))

    # Ignore emacs backup files
    filenames = [x for x in filenames if not x.endswith('~')]

    for filename in filenames:
        log("Processing struct file: " + filename)
        ofinput = process_input_file(filename)

        # Populate global state
        for wire_version in ofinput.wire_versions:
            ofinputs_by_version[wire_version].append(ofinput)
            version_name = of_g.of_version_wire2name[wire_version]

            for ofclass in ofinput.classes:
                of_g.ordered_classes[wire_version].append(ofclass.name)
                legacy_members = []
                pad_count = 0
                for m in ofclass.members:
                    if type(m) == OFPadMember:
                        m_name = 'pad%d' % pad_count
                        if m_name == 'pad0': m_name = 'pad'
                        legacy_members.append(dict(m_type='uint8_t[%d]' % m.length,
                                                   name=m_name))
                        pad_count += 1
                    else:
                        # HACK the C backend does not yet support of_oxm_t
                        if m.oftype == 'of_oxm_t':
                            m_type = 'of_octets_t'
                        else:
                            m_type = m.oftype
                        legacy_members.append(dict(m_type=m_type, name=m.name))
                versions[version_name]['classes'][ofclass.name] = legacy_members

            for enum in ofinput.enums:
                for name, value in enum.values:
                    identifiers.add_identifier(
                        translation.loxi_name(name),
                        name, enum.name, value, wire_version,
                        of_g.identifiers, of_g.identifiers_by_group)

        for wire_version, ofinputs in ofinputs_by_version.items():
            ofprotocol = OFProtocol(wire_version=wire_version, classes=[], enums=[])
            for ofinput in ofinputs:
                ofprotocol.classes.extend(ofinput.classes)
                ofprotocol.enums.extend(ofinput.enums)
            ofprotocol.classes.sort(key=lambda ofclass: ofclass.name)
            of_g.ir[wire_version] = ofprotocol

def populate_type_maps():
    """
    Use the type members in the IR to fill out the legacy type_maps.
    """

    def split_inherited_cls(cls):
        if cls == 'of_meter_band_stats': # HACK not a subtype of of_meter_band
            return None, None
        for parent in sorted(type_maps.inheritance_data.keys(), reverse=True):
            if cls.startswith(parent):
                return (parent, cls[len(parent)+1:])
        return None, None

    def find_experimenter(parent, cls):
        for experimenter in sorted(of_g.experimenter_name_to_id.keys(), reverse=True):
            prefix = parent + '_' + experimenter
            if cls.startswith(prefix) and cls != prefix:
                return experimenter
        return None

    def find_type_value(ofclass, m_name):
        for m in ofclass.members:
            if isinstance(m, OFTypeMember) and m.name == m_name:
                return m.value
        raise KeyError("ver=%d, cls=%s, m_name=%s" % (wire_version, cls, m_name))

    # Most inheritance classes: actions, instructions, etc
    for wire_version, protocol in of_g.ir.items():
        for ofclass in protocol.classes:
            cls = ofclass.name
            parent, subcls = split_inherited_cls(cls)
            if not (parent and subcls):
                continue
            if parent == 'of_oxm':
                val = (find_type_value(ofclass, 'type_len') >> 8) & 0xff
            else:
                val = find_type_value(ofclass, 'type')
            type_maps.inheritance_data[parent][wire_version][subcls] = val

            # Extensions (only actions for now)
            experimenter = find_experimenter(parent, cls)
            if parent == 'of_action' and experimenter:
                val = find_type_value(ofclass, 'subtype')
                type_maps.extension_action_subtype[wire_version][experimenter][cls] = val
                if wire_version >= of_g.VERSION_1_3:
                    cls2 = parent + "_id" + cls[len(parent):]
                    type_maps.extension_action_id_subtype[wire_version][experimenter][cls2] = val

    # Messages
    for wire_version, protocol in of_g.ir.items():
        for ofclass in protocol.classes:
            cls = ofclass.name
            # HACK (though this is what loxi_utils.class_is_message() does)
            if not [x for x in ofclass.members if isinstance(x, OFDataMember) and x.name == 'xid']:
                continue
            if type_maps.class_is_virtual(cls):
                continue
            subcls = cls[3:]
            val = find_type_value(ofclass, 'type')
            if not val in type_maps.message_types[wire_version].values():
                type_maps.message_types[wire_version][subcls] = val

            # Extensions
            experimenter = find_experimenter('of', cls)
            if experimenter:
                val = find_type_value(ofclass, 'subtype')
                type_maps.extension_message_subtype[wire_version][experimenter][cls] = val

    type_maps.generate_maps()

def analyze_input():
    """
    Add information computed from the input, including offsets and
    lengths of struct members and the set of list and action_id types.
    """

    # Generate header classes for inheritance parents
    for wire_version, ordered_classes in of_g.ordered_classes.items():
        classes = versions[of_g.of_version_wire2name[wire_version]]['classes']
        for cls in ordered_classes:
            if cls in type_maps.inheritance_map:
                new_cls = cls + '_header'
                of_g.ordered_classes[wire_version].append(new_cls)
                classes[new_cls] = classes[cls]

    # Generate action_id classes for OF 1.3
    for wire_version, ordered_classes in of_g.ordered_classes.items():
        if not wire_version in [of_g.VERSION_1_3]:
            continue
        classes = versions[of_g.of_version_wire2name[wire_version]]['classes']
        for cls in ordered_classes:
            if not loxi_utils.class_is_action(cls):
                continue
            action = cls[10:]
            if action == '' or action == 'header':
                continue
            name = "of_action_id_" + action
            members = classes["of_action"][:]
            of_g.ordered_classes[wire_version].append(name)
            if type_maps.action_id_is_extension(name, wire_version):
                # Copy the base action classes thru subtype
                members = classes["of_action_" + action][:4]
            classes[name] = members

    # @fixme If we support extended actions in OF 1.3, need to add IDs
    # for them here

    for wire_version in of_g.wire_ver_map.keys():
        version_name = of_g.of_version_wire2name[wire_version]
        calculate_offsets_and_lengths(
            of_g.ordered_classes[wire_version],
            versions[version_name]['classes'],
            wire_version)

def unify_input():
    """
    Create Unified View of Objects
    """

    global versions

    # Add classes to unified in wire-format order so that it is easier
    # to generate things later
    keys = versions.keys()
    keys.sort(reverse=True)
    for version in keys:
        wire_version = versions[version]["wire_version"]
        classes = versions[version]["classes"]
        for cls in of_g.ordered_classes[wire_version]:
            add_class(wire_version, cls, classes[cls])


def log_all_class_info():
    """
    Log the results of processing the input

    Debug function
    """

    for cls in of_g.unified:
        for v in of_g.unified[cls]:
            if type(v) == type(0):
                log("cls: %s. ver: %d. base len %d. %s" %
                    (str(cls), v, of_g.base_length[(cls, v)],
                     loxi_utils.class_is_var_len(cls,v) and "not fixed"
                     or "fixed"))
                if "use_version" in of_g.unified[cls][v]:
                    log("cls %s: v %d mapped to %d" % (str(cls), v,
                           of_g.unified[cls][v]["use_version"]))
                if "members" in of_g.unified[cls][v]:
                    for member in of_g.unified[cls][v]["members"]:
                        log("   %-20s: type %-20s. offset %3d" %
                            (member["name"], member["m_type"],
                             member["offset"]))

def generate_all_files():
    """
    Create the files for the language target
    """
    for (name, fn) in lang_module.targets.items():
        path = of_g.options.install_dir + '/' + name
        os.system("mkdir -p %s" % os.path.dirname(path))
        with open(path, "w") as outfile:
            fn(outfile, os.path.basename(name))
        print("Wrote contents for " + name)

if __name__ == '__main__':
    of_g.loxigen_log_file = open("loxigen.log", "w")
    of_g.loxigen_dbg_file = sys.stdout

    of_g.process_commandline()
    # @fixme Use command line params to select log

    if not config_sanity_check():
        debug("Config sanity check failed\n")
        sys.exit(1)

    # Import the language file
    lang_file = "lang_%s" % of_g.options.lang
    lang_module = __import__(lang_file)

    # If list files, just list auto-gen files to stdout and exit
    if of_g.options.list_files:
        for name in lang_module.targets:
            print of_g.options.install_dir + '/' + name
        sys.exit(0)

    log("\nGenerating files for target language %s\n" % of_g.options.lang)

    initialize_versions()
    read_input()
    populate_type_maps()
    analyze_input()
    unify_input()
    order_and_assign_object_ids()
    log_all_class_info()
    generate_all_files()

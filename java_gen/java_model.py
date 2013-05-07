# Prototype of an Intermediate Object model for the java code generator
# A lot of this stuff could/should probably be merged with the python utilities

import of_g
import os
import pdb
import re

import loxi_front_end.type_maps as type_maps
import loxi_utils.loxi_utils as utils
import py_gen.util as py_utils

import java_gen.java_utils as java_utils
ignore_fields = ['version', 'xid', 'length', 'type' ]

protected_fields = ['version', 'length']

class JavaOFVersion(object):
    """ Models a version of OpenFlow. contains methods to convert the internal
        Loxi version to a java constant / a string """
    def __init__(self, int_version):
        self.int_version = int(int_version)

    @property
    def of_version(self):
        return "1" + str(int(self.int_version) - 1)

    @property
    def constant_version(self):
        return "OF_" + self.of_version

    def __str__(self):
        return of_g.param_version_names[self.int_version]

class JavaProperty(object):
    """ Models a property (member) of an openflow class. """
    def __init__(self, msg, java_type, c_type, name, c_name):
        self.msg = msg
        self.java_type = java_type
        self.c_type = c_type
        self.name = name
        self.c_name = c_name

    @property
    def title_name(self):
        return self.name[0].upper() + self.name[1:]

    @property
    def constant_name(self):
        return self.c_name.upper()

    @property
    def default_name(self):
        return "DEFAULT_"+self.constant_name

    @property
    def default_value(self):
        java_type = self.java_type.public_type;

        if re.match(r'List.*', java_type):
            return "Collections.emptyList()"
        elif java_type == "boolean":
            return "false";
        elif java_type in ("byte", "char", "short", "int", "long"):
            return "({0}) 0".format(java_type);
        else:
            return "null";

    @property
    def is_pad(self):
        return self.c_name.startswith("pad")

    @property
    def length(self):
        count, base = utils.type_dec_to_count_base(self.c_type)
        return of_g.of_base_types[base]['bytes'] * count

    @staticmethod
    def for_field(msg, field, c_name=None):
        if not c_name:
            c_name = field['name']

        name = java_utils.name_c_to_camel(c_name)
        java_type = java_utils.convert_to_jtype(msg.c_name, c_name, field['m_type'])
        c_type = field['m_type']
        return JavaProperty(msg, java_type, c_type, name, c_name)

    @property
    def is_universal(self):
        for version in of_g.unified[self.msg.c_name]:
            if version == 'union' or version =='object_id':
                continue
            if 'use_version' in of_g.unified[self.msg.c_name][version]:
                continue

            if not self.c_name in (f['name'] for f in of_g.unified[self.msg.c_name][version]['members']):
                return False
        return True

class JavaOFMessage(object):
    """ Models an OpenFlow Message class """
    def __init__(self, c_name):
        self.c_name = c_name
        self.interface_name = java_utils.name_c_to_caps_camel(c_name)
        self.builder_name = self.interface_name + "Builder"
        self.constant_name = c_name.upper().replace("OF_", "")

    def min_length(self, version):
        return of_g.base_length[(self.c_name, version.int_version)] if (self.c_name, version.int_version) in of_g.base_length else -1

    def is_fixed_length(self, version):
        return (self.c_name, version.int_version) in of_g.is_fixed_length

    def class_name_for_version(self, version):
        return self.interface_name + "Ver"+version.of_version


    def all_properties(self, skip_pads=True):
        if 'union' in of_g.unified[self.c_name]:
            props = []
            fields = of_g.unified[self.c_name]['union']
            for field_name in sorted( fields.keys(), key=lambda k: fields[k]['memid']):
                if field_name in ignore_fields:
                    continue
                if skip_pads and field_name.startswith("pad"):
                    continue

                java_property = JavaProperty.for_field(self, fields[field_name], c_name=field_name)
                props.append(java_property)
            return props
        else:
            return []

    def all_versions(self):
        return [ JavaOFVersion(int_version)
                 for int_version in of_g.unified[self.c_name]
                 if int_version != 'union' and int_version != 'object_id' ]

    def property_in_version(self, prop, version):
        if self.version_is_inherited(version):
            version = self.inherited_from(version)

        if 'members' not in of_g.unified[self.c_name][version.int_version]:
            return False
        return prop.c_name in (member['name'] for member in of_g.unified[self.c_name][version.int_version]['members'])

    def properties_for_version(self, version, skip_pads=True):
        props = []
        if self.version_is_inherited(version):
            version = self.inherited_from(version)

        for field in of_g.unified[self.c_name][version.int_version]['members']:
            if field['name'] in ignore_fields:
                continue
            if skip_pads and field['name'].startswith("pad"):
                continue

            java_property = JavaProperty.for_field(self, field, c_name=field['name'])
            props.append(java_property)
        return props

    def version_is_inherited(self, version):
        return 'use_version' in of_g.unified[self.c_name][version.int_version]

    def inherited_from(self, version):
        return JavaOFVersion(of_g.unified[self.c_name][version.int_version]['use_version'])

    @property
    def is_virtual(self):
        return type_maps.class_is_virtual(self.c_name)

    @property
    def is_extension(self):
        return type_maps.message_is_extension(self.c_name, -1)

    def wire_type(self, version):
        try:
            return py_utils.primary_wire_type(self.c_name, version.int_version)
        except ValueError, e:
            return -1

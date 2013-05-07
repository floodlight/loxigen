import of_g
import os
import pdb
import re

import loxi_front_end.type_maps as type_maps
import loxi_utils.loxi_utils as utils
import py_gen.util as py_utils

import java_gen.java_utils as java_utils
from java_gen.java_model import *
ignore_fields = ['version', 'xid', 'length', 'type' ]

protected_fields = ['version', 'length']


templates_dir = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'templates')

def render_template(out, name, **context):
    prefix = '//::(?=[ \t]|$)'
    utils.render_template(out, name, [templates_dir], context, prefix=prefix)

def create_message_interfaces(message_names, basedir):
    """ Create the base interfaces for OFMessages"""
    for msg_name in message_names:
        msg = JavaOFMessage(msg_name)

        filename = os.path.join(basedir, "%s.java" % msg.interface_name)
        dirname = os.path.dirname(filename)
        if not os.path.exists(dirname):
            os.makedirs(dirname)

        with open(filename, "w") as f:
            render_template(f, "message_interface.java", msg=msg)

def create_of_type_enum(message_names, basedir):
    all_versions = [ JavaOFVersion(v) for v in of_g.target_version_list ]
    messages =  sorted(filter(lambda msg: not msg.is_virtual and not msg.is_extension, [ JavaOFMessage(msg_name) for msg_name in message_names ]), key=lambda msg: msg.wire_type(all_versions[-1]))
    filename = os.path.join(basedir, "../types/OFType.java")
    dirname = os.path.dirname(filename)
    if not os.path.exists(dirname):
        os.makedirs(dirname)
    with open(filename, "w") as f:
        render_template(f, "of_type.java", all_messages=messages, all_versions = all_versions)

def create_message_by_version(message_names, basedir):
    """ Create the OF Messages for each version that implement the above interfaces"""
    for msg_name in message_names:
        msg = JavaOFMessage(msg_name)

        for version in msg.all_versions():
            filename = os.path.join(basedir, "%s.java" % msg.class_name_for_version(version))
            with open(filename, "w") as f:
                   render_template(f, "message_class.java", msg=msg, version=version,
                            impl_class=msg.class_name_for_version(version))

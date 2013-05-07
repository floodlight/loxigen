import of_g
import pdb
import os

import lang_java

import loxi_utils.loxi_utils as loxi_utils

import java_gen.msgs as msgs
import java_gen.java_utils as java_utils

def gen_all_java(out, name):
    """ Generate all of the java files

    @param out is an open file handle to a file called README
    @param name should be 'README' and is ignored for the java
        driver
    """
    messages = list()
    actions = list()
    instructions = list()
    matches = list()
    stat_types = list()
    queue_prop = list()
    lists = list()
    for cls in of_g.unified:
        print "! Classifying %s" % cls
        if cls in [ 'of_stats_reply', 'of_flow_mod', 'of_stats_request' ] :
            continue # doesn't work?!
        if loxi_utils.class_is_stats_message(cls):
            stat_types.append(cls)
        elif loxi_utils.class_is_message(cls):
            messages.append(cls)
        elif loxi_utils.class_is_action(cls):
            actions.append(cls)
        elif loxi_utils.class_is_instruction(cls):
            instructions.append(cls)
        elif loxi_utils.class_is_oxm(cls):
            matches.append(cls)
        elif loxi_utils.class_is_queue_prop(cls):
            queue_prop.append(cls)
        elif loxi_utils.class_is_list(cls):
            lists.append(cls)
        else:
            print "Skipping Unknown class object %s" % str(cls)
    print "Parsed "
    print "  Messages: %d" % len(messages)
    print "  Actions: %d" % len(actions)
    print "  Instructions: %d" % len(instructions)
    print "  OXM matches: %d" % len(matches)
    print "  Stat types: %d" % len(stat_types)
    print "  Queue properties: %d" % len(queue_prop)
    print "  Lists: %d" % len(lists)
    target_dir='loxi_output/openflowj'
    basedir="%s/%s/" % (
            target_dir,
            lang_java.file_to_subdir_map['base_java'])
    srcdir = "%s/src/main/java/org/openflow/protocol" % basedir
    print "Outputting to %s" % basedir
    if not os.path.exists(basedir):
        os.makedirs(basedir)
    java_utils.copy_prewrite_tree(basedir)
    msgs.create_message_interfaces(messages,srcdir)
    msgs.create_message_by_version(messages,srcdir)
    msgs.create_of_type_enum(messages,srcdir)
    with open('README.java-lang') as readme_src:
        out.writelines(readme_src.readlines())
    out.close()

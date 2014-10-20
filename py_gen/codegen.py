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

from collections import defaultdict
import os
import loxi_globals
import template_utils
import loxi_utils.loxi_utils as utils
import util
from loxi_ir import *

# Map from inheritance root to module name
roots = {
    'of_header': 'message',
    'of_action': 'action',
    'of_action_id': 'action_id',
    'of_oxm': 'oxm',
    'of_instruction': 'instruction',
    'of_instruction_id': 'instruction_id',
    'of_meter_band': 'meter_band',
    'of_bsn_tlv': 'bsn_tlv',
    'of_port_desc_prop': 'port_desc_prop',
    'of_port_stats_prop': 'port_stats_prop',
    'of_port_mod_prop': 'port_mod_prop',
    'of_table_mod_prop': 'table_mod_prop',
    'of_queue_desc_prop': 'queue_desc_prop',
    'of_queue_stats_prop': 'queue_stats_prop',
}

# Return the module and class names for the generated Python class
def generate_pyname(ofclass):
    for root, module_name in roots.items():
        if ofclass.name == root:
            return module_name, module_name
        elif ofclass.is_instanceof(root):
            if root == 'of_header':
                # The input files don't prefix message names
                return module_name, ofclass.name[3:]
            else:
                return module_name, ofclass.name[len(root)+1:]
    return 'common', ofclass.name[3:]

# Create intermediate representation, extended from the LOXI IR
def build_ofclasses(version):
    modules = defaultdict(list)
    for ofclass in loxi_globals.ir[version].classes:
        module_name, ofclass.pyname = generate_pyname(ofclass)
        modules[module_name].append(ofclass)
    return modules

def codegen(install_dir):
    def render(name, template_name=None, **ctx):
        if template_name is None:
            template_name = os.path.basename(name)
        with template_utils.open_output(install_dir, name) as out:
            util.render_template(out, template_name, **ctx)

    render('__init__.py', template_name='toplevel_init.py')
    render('pp.py')
    render('generic_util.py')

    for version in loxi_globals.OFVersions.all_supported:
        subdir = 'of' + version.version.replace('.', '')
        modules = build_ofclasses(version)

        render(os.path.join(subdir, '__init__.py'), template_name='init.py',
               version=version, modules=modules.keys())

        render(os.path.join(subdir, 'util.py'), version=version)

        render(os.path.join(subdir, 'const.py'), version=version,
               enums=loxi_globals.ir[version].enums)

        args_by_module = {
            'common': { 'extra_template' : '_common_extra.py' },
            'message': { 'extra_template' : '_message_extra.py' },
        }

        for name, ofclasses in modules.items():
            args = args_by_module.get(name, {})
            render(os.path.join(subdir, name + '.py'), template_name='module.py',
                   version=version, ofclasses=ofclasses, modules=modules.keys(),
                   **args)

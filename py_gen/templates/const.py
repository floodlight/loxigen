:: # Copyright 2013, Big Switch Networks, Inc.
:: #
:: # LoxiGen is licensed under the Eclipse Public License, version 1.0 (EPL), with
:: # the following special exception:
:: #
:: # LOXI Exception
:: #
:: # As a special exception to the terms of the EPL, you may distribute libraries
:: # generated by LoxiGen (LoxiGen Libraries) under the terms of your choice, provided
:: # that copyright and licensing notices generated by LoxiGen are not altered or removed
:: # from the LoxiGen Libraries and the notice provided below is (i) included in
:: # the LoxiGen Libraries, if distributed in source code form and (ii) included in any
:: # documentation for the LoxiGen Libraries, if distributed in binary form.
:: #
:: # Notice: "Copyright 2013, Big Switch Networks, Inc. This library was generated by the LoxiGen Compiler."
:: #
:: # You may not use this file except in compliance with the EPL or LOXI Exception. You may obtain
:: # a copy of the EPL at:
:: #
:: # http://www.eclipse.org/legal/epl-v10.html
:: #
:: # Unless required by applicable law or agreed to in writing, software
:: # distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
:: # WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
:: # EPL for the specific language governing permissions and limitations
:: # under the EPL.
::
:: blacklisted_map_groups = ['macro_definitions']
:: blacklisted_map_idents = ['OFPFW_NW_DST_BITS', 'OFPFW_NW_SRC_BITS',
::     'OFPFW_NW_SRC_SHIFT', 'OFPFW_NW_DST_SHIFT', 'OFPFW_NW_SRC_ALL',
::     'OFPFW_NW_SRC_MASK', 'OFPFW_NW_DST_ALL', 'OFPFW_NW_DST_MASK',
::     'OFPFW_ALL']
:: include('_copyright.py')

:: include('_autogen.py')

OFP_VERSION = ${version}

:: for enum in sorted(enums, key=lambda enum: enum.name):
# Identifiers from group ${enum.name}
::    for (ident, value) in enum.values:
::        if version == 1 and ident.startswith('OFPP_'):
::        # HACK loxi converts these to 32-bit
${ident} = ${"%#x" % (value & 0xffff)}
::        else:
${ident} = ${value}
::        #endif
::    #endfor

::    if enum.name not in blacklisted_map_groups:
${enum.name}_map = {
::        for (ident, value) in enum.values:
::            if ident in blacklisted_map_idents:
::                pass
::            elif version == 1 and ident.startswith('OFPP_'):
::                # HACK loxi converts these to 32-bit
    ${"%#x" % (value & 0xffff)}: ${repr(ident)},
::        else:
    ${value}: ${repr(ident)},
::            #endif
::        #endfor
}

::     #endif
:: #endfor

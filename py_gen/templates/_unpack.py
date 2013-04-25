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
:: # TODO coalesce format strings
:: all_members = ofclass.members[:]
:: if ofclass.length_member: all_members.append(ofclass.length_member)
:: all_members.extend(ofclass.type_members)
:: all_members.sort(key=lambda x: x.offset)
:: for m in all_members:
::     unpack_expr = m.oftype.gen_unpack_expr('buf', m.offset)
::     if m == ofclass.length_member:
        _length = ${unpack_expr}
:: if ofclass.name != 'of_match_v3':
        assert(_length == len(buf))
:: #endif
:: if ofclass.is_fixed_length:
        if _length != ${ofclass.min_length}: raise loxi.ProtocolError("${ofclass.pyname} length is %d, should be ${ofclass.min_length}" % _length)
:: else:
        if _length < ${ofclass.min_length}: raise loxi.ProtocolError("${ofclass.pyname} length is %d, should be at least ${ofclass.min_length}" % _length)
:: #endif
::     elif m in ofclass.type_members:
        ${m.name} = ${unpack_expr}
        assert(${m.name} == ${m.value})
::     else:
        obj.${m.name} = ${unpack_expr}
::     #endif
:: #endfor

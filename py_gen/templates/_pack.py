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
:: length_member_index = None
:: index = 0
:: for m in all_members:
::     if m == ofclass.length_member:
::         length_member_index = index
        packed.append(${m.oftype.gen_pack_expr('0')}) # placeholder for ${m.name} at index ${length_member_index}
::     else:
        packed.append(${m.oftype.gen_pack_expr('self.' + m.name)})
::     #endif
::     index += 1
:: #endfor
:: if length_member_index != None:
        length = sum([len(x) for x in packed])
        packed[${length_member_index}] = ${ofclass.length_member.oftype.gen_pack_expr('length')}
:: #endif
:: if ofclass.name == 'of_match_v3':
        packed.append('\x00' * ((length + 7)/8*8 - length))
:: #endif

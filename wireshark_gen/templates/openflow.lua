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
:: import of_g
:: ir = of_g.ir
:: include('_copyright.lua')

:: include('_ofreader.lua')

p_of = Proto ("of", "OpenFlow")

local openflow_versions = {
:: for (version, name) in of_g.param_version_names.items():
    [${version}] = "${name}",
:: #endfor
}

:: for version, ofproto in ir.items():
:: for enum in ofproto.enums:
local enum_v${version}_${enum.name} = {
:: for (name, value) in enum.values:
    [${value}] = "${name}",
:: #endfor
}

:: #endfor

:: #endfor

fields = {}
:: for field in fields:
fields[${repr(field.fullname)}] = ProtoField.new("${field.name}", "${field.fullname}", "FT_${field.type}", nil, "BASE_${field.base}")
:: #endfor

p_of.fields = {
:: for field in fields:
    fields[${repr(field.fullname)}],
:: #endfor
}

:: for supercls in set(sorted(superclasses.values())):
local ${supercls}_dissectors = {
:: for version, ofproto in ir.items():
    [${version}] = {},
:: #endfor
}
:: #endfor

:: for version, ofproto in ir.items():
:: for ofclass in ofproto.classes:
:: name = 'dissect_%s_v%d' % (ofclass.name, version)
:: typeval = 0
:: include('_ofclass_dissector.lua', name=name, ofclass=ofclass)
:: if ofclass.name in superclasses:
${superclasses[ofclass.name]}_dissectors[${version}][${typeval}] = ${name}

:: #endif
:: #endfor
:: #endfor

function dissect_of_message(buf, root)
    local subtree = root:add(p_of, buf(0))
    subtree:add(fields['of10.header.version'], buf(0,1))
    subtree:add(fields['of10.header.type'], buf(1,1))
    subtree:add(fields['of10.header.length'], buf(2,2))
    subtree:add(fields['of10.header.xid'], buf(4,4))

    local version_val = buf(0,1):uint()
    local type_val = buf(1,1):uint()
    if of_message_dissectors[version_val] and of_message_dissectors[version_val][type_val] then
        of_message_dissectors[version_val][type_val](buf, subtree)
    end
end

-- of dissector function
function p_of.dissector (buf, pkt, root)
    pkt.cols.protocol = p_of.name

    local offset = 0
    repeat
        if buf:len() - offset >= 4 then
            msg_len = buf(offset+2,2):uint()
            if offset + msg_len > buf:len() then
                -- we don't have all the data we need yet
                pkt.desegment_len = offset + msg_len - buf:len()
                return
            end

            dissect_of_message(buf(offset, msg_len), root)
            offset = offset + msg_len
        else
            -- we don't have all of length field yet
            pkt.desegment_len = DESEGMENT_ONE_MORE_SEGMENT
            return
        end
    until offset >= buf:len()
end

-- Initialization routine
function p_of.init()
end

-- register a chained dissector for OpenFlow port numbers
local tcp_dissector_table = DissectorTable.get("tcp.port")
tcp_dissector_table:add(6633, p_of)
tcp_dissector_table:add(6653, p_of)
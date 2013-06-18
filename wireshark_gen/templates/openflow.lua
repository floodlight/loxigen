:: import of_g
:: ir = of_g.ir
-- TODO copyright (GPL)

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

local f_version = ProtoField.uint8("of.version", "Version", base.HEX, openflow_versions)
local f_type = ProtoField.uint8("of.type", "Type", base.HEX, enum_v1_ofp_type)
local f_length = ProtoField.uint16("of.length", "Length")
local f_xid = ProtoField.uint32("of.xid", "XID", base.HEX)

p_of.fields = {
    f_version,
    f_type,
    f_length,
    f_xid,
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
    -- create subtree for of
    local subtree = root:add(p_of, buf(0))
    -- add protocol fields to subtree
    subtree:add(f_version, buf(0,1))
    subtree:add(f_type, buf(1,1))
    subtree:add(f_length, buf(2,2))
    subtree:add(f_xid, buf(4,4))

    local version_val = buf(0,1):uint()
    local type_val = buf(1,1):uint()
    if of_message_dissectors[version_val] and of_message_dissectors[version_val][type_val] then
        of_message_dissectors[version_val][type_val](buf, root)
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

-- register a chained dissector for port 8002
local tcp_dissector_table = DissectorTable.get("tcp.port")
tcp_dissector_table:add(6633, p_of)

-- TODO copyright (GPL)

p_of = Proto ("of", "OpenFlow")

local ofp_type = {
    [0] = "HELLO",
    [1] = "ECHO_REQUEST",
    [2] = "ECHO_REPLY",
    [3] = "ERROR",
}

local f_version = ProtoField.uint8("of.version", "Version", base.HEX)
local f_type = ProtoField.uint8("of.type", "Type", base.HEX, ofp_type)
local f_length = ProtoField.uint16("of.length", "Length", base.HEX)
local f_xid = ProtoField.uint32("of.xid", "XID", base.HEX)

p_of.fields = {
    f_version,
    f_type,
    f_length,
    f_xid,
}

function dissect_one(buf, pkt, root)
    -- create subtree for of
    local subtree = root:add(p_of, buf(0))
    -- add protocol fields to subtree
    subtree:add(f_version, buf(0,1))
    subtree:add(f_type, buf(1,1))
    subtree:add(f_length, buf(2,2))
    subtree:add(f_xid, buf(4,4))

    local type_val = buf(1,1):uint()
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

            dissect_one(buf(offset, msg_len), pkt, root)
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

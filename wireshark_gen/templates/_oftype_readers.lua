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

function read_scalar(reader, subtree, field_name, length)
    subtree:add(fields[field_name], reader.read(length))
end

function read_uint8_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 1)
end

function read_uint16_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 2)
end

function read_uint32_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 4)
end

function read_uint64_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 8)
end

function read_of_octets_t(reader, version, subtree, field_name)
    if not reader.is_empty() then
        subtree:add(fields[field_name], reader.read_all())
    end
end

function read_list_of_hello_elem_t(reader, version, subtree, field_name)
    -- TODO
end

function read_of_match_t(reader, version, subtree, field_name)
    if version == 1 then
        dissect_of_match_v1_v1(reader, subtree:add(fields[field_name]))
    elseif version == 2 then
        dissect_of_match_v2_v2(reader, subtree:add(fields[field_name]))
    elseif version >= 3 then
        dissect_of_match_v3_v3(reader, subtree:add(fields[field_name]))
    end
end

function read_of_wc_bmap_t(reader, version, subtree, field_name)
    if version <= 2 then
        read_scalar(reader, subtree, field_name, 4)
    else
        read_scalar(reader, subtree, field_name, 8)
    end
end

function read_of_port_no_t(reader, version, subtree, field_name)
    if version == 1 then
        read_scalar(reader, subtree, field_name, 2)
    else
        read_scalar(reader, subtree, field_name, 4)
    end
end

function read_of_mac_addr_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 6)
end

function read_of_ipv4_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 4)
end

function read_of_ipv6_t(reader, version, subtree, field_name)
    read_scalar(reader, subtree, field_name, 16)
end

function read_of_fm_cmd_t(reader, version, subtree, field_name)
    if version == 1 then
        read_scalar(reader, subtree, field_name, 2)
    else
        read_scalar(reader, subtree, field_name, 1)
    end
end

function read_list_of_action_t(reader, version, subtree, field_name)
    if reader.is_empty() then
        return
    end

    local list = subtree:add(fields[field_name], reader.peek_all(0))
    while not reader.is_empty() do
        local action_len = reader.peek(2, 2):uint()
        local child_reader = reader.slice(action_len)
        local child_subtree = list:add(fields[field_name], child_reader.peek_all(0))
        local info = dissect_of_action_v1(child_reader, child_subtree)
        child_subtree:set_text(info)
    end
end

function read_list_of_port_desc_t(reader, version, subtree, field_name)
    -- TODO
end

function read_list_of_packet_queue_t(reader, version, subtree, field_name)
    -- TODO
end

function read_list_of_oxm_t(reader, version, subtree, field_name)
    if reader.is_empty() then
        return
    end
    local list_len = reader.peek(-2,2):uint()
    local reader2 = reader.slice(list_len - 4)
    local list = nil
    if not reader2.is_empty() then
        list = subtree:add(fields[field_name], reader2.peek_all(0))
    end
    while not reader2.is_empty() do
        local match_len = 4 + reader2.peek(3,1):uint()
        local child_reader = reader2.slice(match_len)
        local child_subtree = list:add(fields[field_name], child_reader.peek_all(0))
        local info = dissect_of_oxm_v3(child_reader, child_subtree)
        child_subtree:set_text(info)
    end
    reader.skip_align()
end

function read_list_of_instruction_t(reader, version, subtree, field_name)
    if reader.is_empty() then
        return
    end
    if not reader.is_empty() then
        subtree:add(fields[field_name], reader.read_all())
    end
end

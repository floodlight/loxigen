import os
import errno
import re
import subprocess
import time

def name_c_to_camel(name):
    """ 'of_stats_reply' -> 'ofStatsReply' """
    name = re.sub(r'^_','', name)
    tokens = name.split('_')
    for i in range(1, len(tokens)):
            tokens[i] = tokens[i].title()
    return "".join(tokens)

def name_c_to_caps_camel(name):
    """ 'of_stats_reply' to 'OFStatsReply' """
    camel = name_c_to_camel(name.title())
    if camel.startswith('Of'):
        return camel.replace('Of','OF',1)
    else:
        return camel


class JType(object):
    """ Wrapper class to hold C to Java type conversion information """
    def __init__(self, pub_type, priv_type=None, size=None, read_op=None, write_op=None):
        self.pub_type = pub_type    # the type we expose externally, e.g. 'U8'
        if priv_type is None:
            priv_type = pub_type
        self.priv_type = priv_type  # the internal storage type
        self.size = size            # bytes on the wire; None == variable length or hard to calc
        if read_op is None:
            read_op = 'ChannelUtils.read%s(bb)' % self.pub_type
        if write_op is None:
            write_op = 'ChannelUtils.write%s(bb, $name)'  % self.pub_type
        self._read_op = read_op
        self._write_op = write_op

    @property
    def public_type(self):
        """ return the public type """
        return self.pub_type

    def priv(self):
        """ return the public type """
        return self.priv_type

    def has_priv(self):
        """ Is the private type different from the public one?"""
        return self.pub_type != self.priv_type

    def read_op(self, version=None, length=None):
        if length is None:
            length = "length - bb.readerIndex()";

        if callable(self._read_op):
            return self._read_op(version)
        else:
            return self._read_op.replace("$length", str(length)).replace("$version", version.of_version)

    def write_op(self, version=None, name=None):
        if callable(self._write_op):
            return self._write_op(version, name)
        else:
            return self._write_op.replace("$name", str(name)).replace("$version", version.of_version)

hello_elem_list = JType("List<OFHelloElement>",
        read_op = 'ChannelUtils.readHelloElementList(bb)',
        write_op = 'ChannelUtils.writeHelloElementList(bb)'
        )
u8 =  JType('byte',  size=1, read_op='bb.readByte()',
        write_op='bb.writeByte($name)')
u8_list =  JType('List<U8>',  size=1, read_op='bb.readByte()',
        write_op='bb.writeByte($name)')
u16 = JType('int', 'int', size=2, read_op='U16.f(bb.readShort())',
        write_op='bb.writeShort(U16.t($name))')
u32 = JType('int', 'int',   size=4, read_op='bb.readInt()',
        write_op='bb.writeInt($name)')
u32_list = JType('List<U32>', 'int[]',   size=4, read_op='bb.readInt()',
        write_op='bb.writeInt($name)')
u64 = JType('U64', 'U64', size=8, read_op='U64.of(bb.readLong())',
        write_op='bb.writeLong($name.getValue())')
of_port= JType('OFPort',size=None,
        read_op=lambda version: 'OFPort.ofShort(bb.readShort())' if version.int_version < 2 else 'OFPort.ofInt(bb.readInt())',
        write_op=lambda version, name: 'bb.writeShort(%s.getShortPortNumber())' % name if version.int_version < 2 else 'bb.writeInt(%s.getPortNumber())' % name)
one_byte_array = JType('byte[]', size=1,
        read_op = 'ChannelUtils.readBytes(bb, 1)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
two_byte_array = JType('byte[]', size=2,
        read_op = 'ChannelUtils.readBytes(bb, 2)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
three_byte_array = JType('byte[]', size=3,
        read_op = 'ChannelUtils.readBytes(bb, 3)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
four_byte_array = JType('byte[]', size=4,
        read_op = 'ChannelUtils.readBytes(bb, 4)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
five_byte_array = JType('byte[]', size=5,
        read_op = 'ChannelUtils.readBytes(bb, 5)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
six_byte_array = JType('byte[]', size=6,
        read_op = 'ChannelUtils.readBytes(bb, 6)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
seven_byte_array = JType('byte[]', size=7,
        read_op = 'ChannelUtils.readBytes(bb, 7)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
actions_list = JType('List<OFAction>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readActionsList(bb, $length)',
        write_op = 'ChannelUtils.writeActionsList(bb, $name);')
instructions_list = JType('List<OFInstruction>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readInstructionsList(bb, $length)',
        write_op = 'ChannelUtils.writeList(bb, $name)')
buckets_list = JType('List<OFBucket>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readBucketList(bb, $length)',
        write_op = 'ChannelUtils.writeList(bb, $name)')
port_desc_list = JType('List<OFPhysicalPort>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readPhysicalPortList(bb, $length)',
        write_op = 'ChannelUtils.writeList(bb, $name)')
port_desc = JType('OFPortDesc', size='$name.getLength()',
        read_op = 'null; // TODO OFPortDescVer$version.READER.read(bb)',
        write_op = '$name.writeTo(bb)')
packet_queue_list = JType('List<OFPacketQueue>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readPacketQueueList(bb, $length)',
        write_op = 'ChannelUtils.writeList(bb, $name)')
octets = JType('byte[]', size="$length",
        read_op = 'ChannelUtils.readBytes(bb, $length)',
        write_op = 'bb.writeBytes($name)')
of_match = JType('Match', size="$name.getLength()",
        read_op = 'ChannelUtils.readOFMatch(bb)',
        write_op = 'ChannelUtils.writeOFMatch(bb, $name)')
flow_mod_cmd = JType('OFFlowModCommand', 'short', size="$name.getLength()",
        read_op = lambda v: "bb.readShort()" if v.int_version == 1 else "bb.readByte()",
        write_op = lambda v, name: "bb.writeShort(%s)" % name if v.int_version == 1 else "bb.writeByte(%s)" % name)
mac_addr = JType('MacAddress', 'byte[]', size=6,
        read_op = 'MacAddress.readFrom(bb)',
        write_op = '$name.writeTo(bb)')
port_name = JType('String', size=16,
        read_op = 'ChannelUtils.readFixedLengthString(bb, 16)',
        write_op = 'ChannelUtils.writeFixedLengthString(bb, $name, 16)')
desc_str = JType('String', size=256,
        read_op = 'ChannelUtils.readFixedLengthString(bb, 256)',
        write_op = 'ChannelUtils.writeFixedLengthString(bb, $name, 256)')
serial_num = JType('String', size=32,
        read_op = 'ChannelUtils.readFixedLengthString(bb, 32)',
        write_op = 'ChannelUtils.writeFixedLengthString(bb, $name, 32)')
table_name = JType('String', size=32,
        read_op = 'ChannelUtils.readFixedLengthString(bb, 32)',
        write_op = 'ChannelUtils.writeFixedLengthString(bb, $name, 32)')
ipv4 = JType("IPv4",
        read_op = "IPv4.readFrom(bb)",
        write_op = "$name.writeTo(bb)")
ipv6 = JType("IPv6",
        read_op = "IPv6.readFrom(bb)",
        write_op = "$name.writeTo(bb)")

default_mtype_to_jtype_convert_map = {
        'uint8_t' : u8,
        'uint16_t' : u16,
        'uint32_t' : u32,
        'uint64_t' : u64,
        'uint8_t[1]' : one_byte_array,
        'uint8_t[2]' : two_byte_array,
        'uint8_t[3]' : three_byte_array,
        'uint8_t[4]' : four_byte_array,
        'uint8_t[5]' : five_byte_array,
        'uint8_t[6]' : six_byte_array,
        'uint8_t[7]' : seven_byte_array,
        'of_port_no_t' : of_port,
        'list(of_action_t)' : actions_list,
        'list(of_instruction_t)' : instructions_list,
        'list(of_bucket_t)': buckets_list,
        'list(of_port_desc_t)' : port_desc_list,
        'list(of_packet_queue_t)' : packet_queue_list,
        'list(of_uint32_t)' : u32_list,
        'list(of_uint8_t)' : u8_list,
        'of_octets_t' : octets,
        'of_match_t': of_match,
        'of_fm_cmd_t': flow_mod_cmd,
        'of_mac_addr_t': mac_addr,
        'of_port_desc_t': port_desc,
        'of_desc_str_t': desc_str,
        'of_serial_num_t': serial_num,
        'of_port_name_t': port_name,
        'of_table_name_t': table_name,
        'of_ipv4_t': ipv4,
        'of_ipv6_t': ipv6,
        'of_wc_bmap_t': JType("Wildcards")
        }

## This is where we drop in special case handling for certain types
exceptions = {
        'OFPacketIn': {
            'data' : octets
            },
}


def make_standard_list_jtype(c_type):
    m = re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type)
    if not m:
        raise Exception("Not a recgonized standard list type declaration: %s" % c_type)
    base_name = m.group(1)
    java_base_name = name_c_to_caps_camel(base_name)
    return JType("List<OF%s>" % java_base_name,
        read_op = 'ChannelUtils.read%sList(bb)' % java_base_name,
        write_op = 'ChannelUtils.write%sList(bb, $name)' % java_base_name
        )

def convert_to_jtype(obj_name, field_name, c_type):
    """ Convert from a C type ("uint_32") to a java type ("U32")
    and return a JType object with the size, internal type, and marshalling functions"""
    if obj_name in exceptions and field_name in exceptions[obj_name]:
        return exceptions[obj_name][field_name]
    elif field_name == "type" and c_type == "uint8_t":
        return JType("OFType", 'byte', size=1, read_op='bb.readByte()',
        write_op='bb.writeByte($name)')
    elif field_name == "type" and re.match(r'of_action.*', obj_name):
        return JType("OFActionType", 'short', size=2, read_op='bb.readShort()',
        write_op='bb.writeShort($name)')
    elif field_name == "version" and c_type == "uint8_t":
        return JType("OFVersion", 'byte', size=1, read_op='bb.readByte()',
        write_op='bb.writeByte($name)')
    elif c_type in default_mtype_to_jtype_convert_map:
        return default_mtype_to_jtype_convert_map[c_type]
    elif re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type):
        return make_standard_list_jtype(c_type)
    else:
        print "WARN: Couldn't find java type conversion for '%s' in %s:%s" % (c_type, obj_name, field_name)
        jtype = name_c_to_caps_camel(re.sub(r'_t$', "", c_type))
        return JType(jtype)

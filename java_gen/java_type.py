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


java_primitive_types = set("byte char short int long".split(" "))

ANY = 0xFFFFFFFFFFFFFFFF

class VersionOp:
    def __init__(self, version=ANY, read=None, write=None):
        self.version = version
        self.read = read
        self.write = write

    def __str__(self):
        return "[Version: %d, Read: '%s', Write: '%s']" % (self.version, self.read, self.write)

class JType(object):
    """ Wrapper class to hold C to Java type conversion information """
    def __init__(self, pub_type, priv_type=None, size=None, read_op=None, write_op=None):
        self.pub_type = pub_type    # the type we expose externally, e.g. 'U8'
        if priv_type is None:
            priv_type = pub_type
        self.priv_type = priv_type  # the internal storage type
        self.size = size            # bytes on the wire; None == variable length or hard to calc
        self.ops = {}
#        if read_op is None:
#            read_op = 'ChannelUtilsVer$version.read%s(bb)' % self.pub_type
#        if write_op is None:
#            write_op = 'ChannelUtilsVer$version.write%s(bb, $name)'  % self.pub_type
#        self._read_op = read_op
#        self._write_op = write_op

    def op(self, version=ANY, read=None, write=None):
        self.ops[version] = VersionOp(version, read, write)
        return self

    def cast(self, min):
        """ declares that the value has to be cast to itself for values >= min.
            This is to deal with Java signedness """
        def format_cast_value(value):
            if value >= min:
                return "(%s) 0x%x" % (self.pub_type, value)
            else:
                return "0x%x" % (value)

        self.format_value = format_cast_value
        return self

    def format_value(self, value):
        return value

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

        ver = ANY if version is None else version.int_version
        _read_op = None
        if ver in self.ops:
            _read_op = self.ops[ver].read or self.ops[ANY].read
        elif ANY in self.ops:
            _read_op = self.ops[ANY].read
        if _read_op is None:
            _read_op = 'ChannelUtilsVer$version.read%s(bb)' % self.pub_type
        if callable(_read_op):
            return _read_op(version)
        else:
            return _read_op.replace("$length", str(length)).replace("$version", version.of_version)

    def write_op(self, version=None, name=None):
        ver = ANY if version is None else version.int_version
        _write_op = None
        if ver in self.ops:
            _write_op = self.ops[ver].write or self.ops[ANY].write
        elif ANY in self.ops:
            _write_op = self.ops[ANY].write
        if _write_op is None:
            _write_op = 'ChannelUtilsVer$version.write%s(bb, $name)' % self.pub_type
        if callable(_write_op):
            return _write_op(version, name)
        else:
            return _write_op.replace("$name", str(name)).replace("$version", version.of_version)

    @property
    def is_primitive(self):
        return self.pub_type in java_primitive_types

    @property
    def is_array(self):
        return self.pub_type.endswith("[]")


u8 =  JType('byte',  size=1) \
        .op(read='bb.readByte()', write='bb.writeByte($name)')
u8_list =  JType('List<U8>',  size=1) \
        .op(read='bb.readByte()', write='bb.writeByte($name)')
u16 = JType('int', 'int', size=2) \
        .op(read='U16.f(bb.readShort())', write='bb.writeShort(U16.t($name))')
u32 = JType('int', 'int', size=4) \
        .op(read='bb.readInt()', write='bb.writeInt($name)')
u32_list = JType('List<U32>', 'int[]', size=4) \
        .op(read='bb.readInt()', write='bb.writeInt($name)')
u64 = JType('U64', 'U64', size=8) \
        .op(read='U64.of(bb.readLong())', write='bb.writeLong($name.getValue())')
of_port = JType("OFPort") \
         .op(version=1, read="OFPort.read2Bytes(bb)", write="$name.write2Bytes(bb)") \
         .op(version=ANY, read="OFPort.read4Bytes(bb)", write="$name.write4Bytes(bb)")
one_byte_array = JType('byte[]', size=1) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 1)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
two_byte_array = JType('byte[]', size=2) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 2)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
three_byte_array = JType('byte[]', size=3) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 3)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
four_byte_array = JType('byte[]', size=4) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 4)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
five_byte_array = JType('byte[]', size=5) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 5)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
six_byte_array = JType('byte[]', size=6) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 6)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
seven_byte_array = JType('byte[]', size=7) \
        .op(read='ChannelUtilsVer$version.readBytes(bb, 7)', write='ChannelUtilsVer$version.writeBytes(bb, $name)')
actions_list = JType('List<OFAction>', size='ChannelUtilsVer$version.calcListSize($name)') \
        .op(read='ChannelUtilsVer$version.readActionsList(bb, $length)', write='ChannelUtilsVer$version.writeActionsList(bb, $name);')
instructions_list = JType('List<OFInstruction>', size='ChannelUtilsVer$version.calcListSize($name)') \
        .op(read='ChannelUtilsVer$version.readInstructionsList(bb, $length)', \
            write='ChannelUtilsVer$version.writeList(bb, $name)')
buckets_list = JType('List<OFBucket>', size='ChannelUtilsVer$version.calcListSize($name)') \
        .op(read='ChannelUtilsVer$version.readBucketList(bb, $length)', \
            write='ChannelUtilsVer$version.writeList(bb, $name)')
port_desc_list = JType('List<OFPhysicalPort>', size='ChannelUtilsVer$version.calcListSize($name)') \
        .op(read='ChannelUtilsVer$version.readPhysicalPortList(bb, $length)', \
            write='ChannelUtilsVer$version.writePhysicalPortList(bb, $name)')
port_desc = JType('OFPortDesc', size='$name.getLength()') \
        .op(read='null; // TODO OFPortDescVer$version.READER.read(bb)', \
            write='$name.writeTo(bb)')
packet_queue_list = JType('List<OFPacketQueue>', size='ChannelUtilsVer$version.calcListSize($name)') \
        .op(read='ChannelUtilsVer$version.readPacketQueueList(bb, $length)', \
            write='ChannelUtilsVer$version.writeList(bb, $name)')
octets = JType('byte[]', size="$length") \
        .op(read='ChannelUtilsVer$version.readBytes(bb, $length)', \
            write='bb.writeBytes($name)')
of_match = JType('Match', size="$name.getLength()") \
        .op(read='ChannelUtilsVer$version.readOFMatch(bb)', \
            write='ChannelUtilsVer$version.writeOFMatch(bb, $name)')
flow_mod_cmd = JType('OFFlowModCommand', 'short', size="$name.getLength()") \
        .op(version=1, read="bb.readShort()", write="bb.writeShort($name)") \
        .op(version=ANY, read="bb.readByte()", write="bb.writeByte($name)")
mac_addr = JType('MacAddress', 'byte[]', size=6) \
        .op(read="MacAddress.read6Bytes(bb)", \
            write="$name.write6Bytes(bb)")
port_name = JType('String', size=16) \
        .op(read='ChannelUtilsVer$version.readFixedLengthString(bb, 16)', \
            write='ChannelUtilsVer$version.writeFixedLengthString(bb, $name, 16)')
desc_str = JType('String', size=256) \
        .op(read='ChannelUtilsVer$version.readFixedLengthString(bb, 256)', \
            write='ChannelUtilsVer$version.writeFixedLengthString(bb, $name, 256)')
serial_num = JType('String', size=32) \
        .op(read='ChannelUtilsVer$version.readFixedLengthString(bb, 32)', \
            write='ChannelUtilsVer$version.writeFixedLengthString(bb, $name, 32)')
table_name = JType('String', size=32) \
        .op(read='ChannelUtilsVer$version.readFixedLengthString(bb, 32)', \
            write='ChannelUtilsVer$version.writeFixedLengthString(bb, $name, 32)')
ipv4 = JType("IPv4") \
        .op(read="IPv4.read4Bytes(bb)", \
            write="$name.write4Bytes(bb)")
ipv6 = JType("IPv6") \
        .op(read="IPv6.read16Bytes(bb)", \
            write="$name.write16Bytes(bb)")
packetin_reason = JType("OFPacketInReason")\
        .op(read="OFPacketInReasonSerializerVer$version.readFrom(bb)", write="OFPacketInReasonSerializerVer$version.writeTo(bb, $name)")

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
        'of_packet_in': {
            'data' : octets,
            'reason': packetin_reason
            },
}


enum_wire_types = {
        "uint8_t": JType("byte").op(read="bb.readByte()", write="bb.writeByte($name)").cast(min=1<<7),
        "uint16_t": JType("short").op(read="bb.readShort()", write="bb.writeShort($name)").cast(min=1<<15),
        "uint32_t": JType("int").op(read="bb.readInt()", write="bb.writeInt($name)").cast(min=1<<31),
        "uint64_t": JType("long").op(read="bb.readLong()", write="bb.writeLong($name)").cast(min=1<<31)
}

def convert_enum_wire_type_to_jtype(wire_type):
    return enum_wire_types[wire_type]

def make_standard_list_jtype(c_type):
    m = re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type)
    if not m:
        raise Exception("Not a recgonized standard list type declaration: %s" % c_type)
    base_name = m.group(1)
    java_base_name = name_c_to_caps_camel(base_name)
    return JType("List<OF%s>" % java_base_name) \
        .op(read='ChannelUtilsVer$version.read%sList(bb, $length)' % java_base_name, \
            write='ChannelUtilsVer$version.write%sList(bb, $name)' % java_base_name)

def convert_to_jtype(obj_name, field_name, c_type):
    """ Convert from a C type ("uint_32") to a java type ("U32")
    and return a JType object with the size, internal type, and marshalling functions"""
    if obj_name in exceptions and field_name in exceptions[obj_name]:
        return exceptions[obj_name][field_name]
    elif field_name == "type" and c_type == "uint8_t":
        return JType("OFType", 'byte', size=1) \
            .op(read='bb.readByte()', write='bb.writeByte($name)')
    elif field_name == "type" and re.match(r'of_action.*', obj_name):
        return JType("OFActionType", 'short', size=2) \
            .op(read='bb.readShort()', write='bb.writeShort($name)')
    elif field_name == "version" and c_type == "uint8_t":
        return JType("OFVersion", 'byte', size=1) \
            .op(read='bb.readByte()', write='bb.writeByte($name)')
    elif c_type in default_mtype_to_jtype_convert_map:
        return default_mtype_to_jtype_convert_map[c_type]
    elif re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type):
        return make_standard_list_jtype(c_type)
    else:
        print "WARN: Couldn't find java type conversion for '%s' in %s:%s" % (c_type, obj_name, field_name)
        jtype = name_c_to_caps_camel(re.sub(r'_t$', "", c_type))
        return JType(jtype)

import loxi_utils.loxi_utils as loxi_utils
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

### info table about java primitive types, for casting literals in the source code
# { name : (signed?, length_in_bits) }
java_primitives_info = {
        'byte' : (True, 8),
        'char' : (False, 16),
        'short' : (True, 16),
        'int' : (True, 32),
        'long' : (True, 64),
}

def format_primitive_literal(t, value):
    """ Format a primitive numeric literal for inclusion in the
        java source code. Takes care of casting the literal
        apropriately for correct representation despite Java's
        signed-craziness
    """
    signed, bits = java_primitives_info[t]
    max = (1 << bits)-1
    if value > max:
        raise Exception("Value %d to large for type %s" % (value, t))

    if signed:
        max_pos = (1 << (bits-1)) - 1

        if  value > max_pos:
            if t == "long":
                return str((1 << bits) - value)
            else:
                return "(%s) 0x%x" % (t, value)
        else:
            return "0x%x%s" % (value, "L" if t=="long" else "")

ANY = 0xFFFFFFFFFFFFFFFF

class VersionOp:
    def __init__(self, version=ANY, read=None, write=None):
        self.version = version
        self.read = read
        self.write = write

    def __str__(self):
        return "[Version: %d, Read: '%s', Write: '%s']" % (self.version, self.read, self.write)

### FIXME: This class should really be cleaned up
class JType(object):
    """ Wrapper class to hold C to Java type conversion information. JTypes can have a 'public'
        and or 'private' java type associated with them and can define how those types can be
        read from and written to ChannelBuffers.

    """
    def __init__(self, pub_type, priv_type=None, read_op=None, write_op=None):
        self.pub_type = pub_type    # the type we expose externally, e.g. 'U8'
        if priv_type is None:
            priv_type = pub_type
        self.priv_type = priv_type  # the internal storage type
        self.ops = {}

    def op(self, version=ANY, read=None, write=None, pub_type=ANY):
        """
        define operations to be performed for reading and writing this type
        (when read_op, write_op is called). The operations 'read' and 'write'
        can either be strings ($name, and $version and $length will be replaced),
        or callables (name, version and length) will be passed.

        @param version int      OF version to define operation for, or ANY for all
        @param pub_type boolean whether to define operations for the public type (True), the
                                private type(False) or both (ALL)
        @param read read expression (either string or callable)s
        @param write write expression (either string or callable)
        """

        pub_types = [ pub_type ] if pub_type is not ANY else [ False, True ]
        for pub_type in pub_types:
            self.ops[(version, pub_type)] = VersionOp(version, read, write)
        return self

    def format_value(self, value, pub_type=True):
        # Format a constant value of this type, for inclusion in the java source code
        # For primitive types, takes care of casting the value appropriately, to
        # cope with java's signedness limitation
        t = self.pub_type if pub_type else self.priv_type
        if t in java_primitive_types:
            return format_primitive_literal(t, value)
        else:
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

    def read_op(self, version=None, length=None, pub_type=True):
        """ return a Java stanza that reads a value of this JType from ChannelBuffer bb.
        @param version int - OF wire version to generate expression for
        @param pub_type boolean use this JTypes 'public' (True), or private (False) representation
        @param length string, for operations that need it (e.g., read a list of unknown length)
               Java expression evaluating to the byte length to be read. Defaults to the remainig
               length of the message.
        @return string containing generated Java expression.
        """
        if length is None:
             # assumes that
             # (1) length of the message has been read to 'length'
             # (2) readerIndex at the start of the message has been stored in 'start'
            length = "length - (bb.readerIndex() - start)";

        ver = ANY if version is None else version.int_version
        _read_op = None
        if (ver, pub_type) in self.ops:
            _read_op = self.ops[(ver, pub_type)].read or self.ops[(ANY, pub_type)].read
        elif (ANY, pub_type) in self.ops:
            _read_op = self.ops[(ANY, pub_type)].read
        if _read_op is None:
            _read_op = 'ChannelUtilsVer$version.read%s(bb)' % self.pub_type
        if callable(_read_op):
            return _read_op(version)
        else:
            return _read_op.replace("$length", str(length)).replace("$version", version.of_version)

    def write_op(self, version=None, name=None, pub_type=True):
        """ return a Java stanza that writes a value of this JType contained in Java expression
        'name' to ChannelBuffer bb.
        @param name string containing Java expression that evaluations to the value to be written
        @param version int - OF wire version to generate expression for
        @param pub_type boolean use this JTypes 'public' (True), or private (False) representation
        @return string containing generated Java expression.
        """
        ver = ANY if version is None else version.int_version
        _write_op = None
        if (ver, pub_type) in self.ops:
            _write_op = self.ops[(ver, pub_type)].write or self.ops[(ANY, pub_type)].write
        elif (ANY, pub_type) in self.ops:
            _write_op = self.ops[(ANY, pub_type)].write
        if _write_op is None:

            _write_op = 'ChannelUtilsVer$version.write%s(bb, $name)' % self.pub_type
        if callable(_write_op):
            return _write_op(version, name)
        else:
            return _write_op.replace("$name", str(name)).replace("$version", version.of_version)

    def skip_op(self, version=None, length=None):
        """ return a java stanza that skips an instance of JType in the input ChannelBuffer 'bb'.
            This is used in the Reader implementations for virtual classes (because after the
            discriminator field, the concrete Reader instance will re-read all the fields)
            Currently just delegates to read_op + throws away the result."""
        return self.read_op(version, length)

    @property
    def is_primitive(self):
        """ return true if the pub_type is a java primitive type (and thus needs
        special treatment, because it doesn't have methods)"""
        return self.pub_type in java_primitive_types

    @property
    def is_array(self):
        """ return true iff the pub_type is a Java array (and thus requires special
        treament for equals / toString etc.) """
        return self.pub_type.endswith("[]")


##### Predefined JType mappings
# FIXME: This list needs to be pruned / cleaned up. Most of these are schematic.

u8 =  JType('short', 'byte') \
        .op(read='bb.readByte()', write='bb.writeByte($name)')
u8_list =  JType('List<U8>') \
        .op(read='ChannelUtils.readList(bb, $length, U8.READER)', write='ChannelUtils.writeList(bb, $name)')
u16 = JType('int', 'short') \
        .op(read='U16.f(bb.readShort())', write='bb.writeShort(U16.t($name))', pub_type=True) \
        .op(read='bb.readShort()', write='bb.writeShort($name)', pub_type=False)
u32 = JType('long', 'int') \
        .op(read='U32.f(bb.readInt())', write='bb.writeInt(U32.t($name))', pub_type=True) \
        .op(read='bb.readInt()', write='bb.writeInt($name)', pub_type=False)
u32_list = JType('List<U32>', 'int[]') \
        .op(read='ChannelUtils.readList(bb, $length, U32.READER)', write='ChannelUtils.writeList(bb, $name)')
u64 = JType('U64', 'U64') \
        .op(read='U64.of(bb.readLong())', write='bb.writeLong($name.getValue())')
of_port = JType("OFPort") \
         .op(version=1, read="OFPort.read2Bytes(bb)", write="$name.write2Bytes(bb)") \
         .op(version=ANY, read="OFPort.read4Bytes(bb)", write="$name.write4Bytes(bb)")
actions_list = JType('List<OFAction>') \
        .op(read='ChannelUtils.readList(bb, $length, OFActionVer$version.READER)', write='ChannelUtils.writeList(bb, $name);')
instructions_list = JType('List<OFInstruction>') \
        .op(read='ChannelUtils.readList(bb, $length, OFInstructionVer$version.READER)', \
            write='ChannelUtils.writeList(bb, $name)')
buckets_list = JType('List<OFBucket>') \
        .op(read='ChannelUtils.readList(bb, $length, OFBucketVer$version.READER)', write='ChannelUtils.writeList(bb, $name)')
port_desc_list = JType('List<OFPhysicalPort>') \
        .op(read='ChannelUtils.readList(bb, $length, OFPhysicalPort.READER)', write='ChannelUtils.writeList(bb, $name)')
port_desc = JType('OFPortDesc') \
        .op(read='OFPortDescVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')
packet_queue_list = JType('List<OFPacketQueue>') \
        .op(read='ChannelUtils.readList(bb, $length, OFPacketQueueVer$version.READER)', write='ChannelUtils.writeList(bb, $name);')
octets = JType('byte[]') \
        .op(read='ChannelUtils.readBytes(bb, $length)', \
            write='bb.writeBytes($name)')
of_match = JType('Match') \
        .op(read='ChannelUtilsVer$version.readOFMatch(bb)', \
            write='$name.writeTo(bb)');
flow_mod_cmd = JType('OFFlowModCommand', 'short') \
        .op(version=1, read="bb.readShort()", write="bb.writeShort($name)") \
        .op(version=ANY, read="bb.readByte()", write="bb.writeByte($name)")
mac_addr = JType('MacAddress') \
        .op(read="MacAddress.read6Bytes(bb)", \
            write="$name.write6Bytes(bb)")
port_name = JType('String') \
        .op(read='ChannelUtils.readFixedLengthString(bb, 16)', \
            write='ChannelUtils.writeFixedLengthString(bb, $name, 16)')
desc_str = JType('String') \
        .op(read='ChannelUtils.readFixedLengthString(bb, 256)', \
            write='ChannelUtils.writeFixedLengthString(bb, $name, 256)')
serial_num = JType('String') \
        .op(read='ChannelUtils.readFixedLengthString(bb, 32)', \
            write='ChannelUtils.writeFixedLengthString(bb, $name, 32)')
table_name = JType('String') \
        .op(read='ChannelUtils.readFixedLengthString(bb, 32)', \
            write='ChannelUtils.writeFixedLengthString(bb, $name, 32)')
ipv4 = JType("IPv4") \
        .op(read="IPv4.read4Bytes(bb)", \
            write="$name.write4Bytes(bb)")
ipv6 = JType("IPv6") \
        .op(read="IPv6.read16Bytes(bb)", \
            write="$name.write16Bytes(bb)")
packetin_reason = JType("OFPacketInReason")\
        .op(read="OFPacketInReasonSerializerVer$version.readFrom(bb)", write="OFPacketInReasonSerializerVer$version.writeTo(bb, $name)")
wildcards = JType("Wildcards")\
        .op(read="Wildcards.of(bb.readInt())", write="bb.writeInt($name.getInt())");
transport_port = JType("TransportPort")\
        .op(read="TransportPort.read2Bytes(bb)", write="$name.write2Bytes(bb)")
oxm = JType("OFOxm")\
        .op(read="OFOxmVer$version.READER.readFrom(bb)", write="$name.writeTo(bb)")
meter_features = JType("OFMeterFeatures")\
        .op(read="OFMeterFeaturesVer$version.READER.readFrom(bb)", write="$name.writeTo(bb)")


default_mtype_to_jtype_convert_map = {
        'uint8_t' : u8,
        'uint16_t' : u16,
        'uint32_t' : u32,
        'uint64_t' : u64,
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
        'of_wc_bmap_t': wildcards,
        'of_oxm_t': oxm,
        'of_meter_features_t': meter_features,
        }

## Map that defines exceptions from the standard loxi->java mapping scheme
# map of {<loxi_class_name> : { <loxi_member_name> : <JType instance> } }
exceptions = {
        'of_packet_in': {
            'data' : octets,
            'reason': packetin_reason
            },
        'of_oxm_tcp_src' : {
            'value' : transport_port
            },
}


# Create a default mapping for a list type. Type defauls to List<${java_mapping_of_name}>
def make_standard_list_jtype(c_type):
    m = re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type)
    if not m:
        raise Exception("Not a recgonized standard list type declaration: %s" % c_type)
    base_name = m.group(1)
    java_base_name = name_c_to_caps_camel(base_name)

    # read op assumes the class has a public final static field READER that implements
    # OFMessageReader<$class> i.e., can deserialize an instance of class from a ChannelBuffer
    # write op assumes class implements Writeable
    return JType("List<OF%s>" % java_base_name) \
        .op(
            read= 'ChannelUtils.readList(bb, $length, OF%sVer$version.READER)' % java_base_name, \
            write='ChannelUtils.writeList(bb, $name)')


#### main entry point for conversion of LOXI types (c_types) Java types.
# FIXME: This badly needs a refactoring

def convert_to_jtype(obj_name, field_name, c_type):
    """ Convert from a C type ("uint_32") to a java type ("U32")
    and return a JType object with the size, internal type, and marshalling functions"""
    if obj_name in exceptions and field_name in exceptions[obj_name]:
        return exceptions[obj_name][field_name]
    elif ( obj_name == "of_header" or loxi_utils.class_is_message(obj_name)) and field_name == "type" and c_type == "uint8_t":
        return JType("OFType", 'byte') \
            .op(read='bb.readByte()', write='bb.writeByte($name)')
    elif field_name == "type" and re.match(r'of_action.*', obj_name):
        return JType("OFActionType", 'short') \
            .op(read='bb.readShort()', write='bb.writeShort($name)', pub_type=False)\
            .op(read="OFActionTypeSerializerVer$version.readFrom(bb)", write="OFActionTypeSerializerVer$version.writeTo(bb, $name)", pub_type=True)
    elif field_name == "version" and c_type == "uint8_t":
        return JType("OFVersion", 'byte') \
            .op(read='bb.readByte()', write='bb.writeByte($name)')
    elif c_type in default_mtype_to_jtype_convert_map:
        return default_mtype_to_jtype_convert_map[c_type]
    elif re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type):
        return make_standard_list_jtype(c_type)
    else:
        print "WARN: Couldn't find java type conversion for '%s' in %s:%s" % (c_type, obj_name, field_name)
        jtype = name_c_to_caps_camel(re.sub(r'_t$', "", c_type))
        return JType(jtype)


#### Enum specific wiretype definitions
enum_wire_types = {
        "uint8_t": JType("byte").op(read="bb.readByte()", write="bb.writeByte($name)"),
        "uint16_t": JType("short").op(read="bb.readShort()", write="bb.writeShort($name)"),
        "uint32_t": JType("int").op(read="bb.readInt()", write="bb.writeInt($name)"),
        "uint64_t": JType("long").op(read="bb.readLong()", write="bb.writeLong($name)"),
}

def convert_enum_wire_type_to_jtype(wire_type):
    return enum_wire_types[wire_type]

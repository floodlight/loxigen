import errno
import os
import re
import subprocess
import time

import loxi_globals
from generic_utils import memoize
import loxi_utils.loxi_utils as loxi_utils

def erase_type_annotation(class_name):
    m=re.match(r'(.*)<.*>', class_name)
    if m:
        return m.group(1)
    else:
        return class_name

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
    if camel.startswith('Ofp'):
        return camel.replace('Ofp','OF',1)
    elif camel.startswith('Of'):
        return camel.replace('Of','OF',1)
    else:
        return camel

java_primitive_types = set("boolean byte char short int long".split(" "))

### info table about java primitive types, for casting literals in the source code
# { name : (signed?, length_in_bits) }
java_primitives_info = {
        'boolean' : (False, 8, False),
        'byte' : (True, 8, True),
        'char' : (False, 16, True),
        'short' : (True, 16, True),
        'int' : (True, 32, False),
        'long' : (True, 64, False),
}

def format_primitive_literal(t, value):
    """ Format a primitive numeric literal for inclusion in the
        java source code. Takes care of casting the literal
        appropriately for correct representation despite Java's
        signed-craziness
    """
    signed, bits, cast_needed = java_primitives_info[t]
    if t == 'boolean':
        return "true" if bool(value) and value not in("False", "false") else "false"

    max = (1 << bits)-1
    if value > max:
        raise Exception("Value %s to large for type %s" % (value, t))

    if signed:
        max_pos = (1 << (bits-1)) - 1

        if  value > max_pos:
            if t == "long":
                return str((1 << bits) - value)
            else:
                return "(%s) 0x%x" % (t, value)
    return "%s0x%x%s" % ("(%s) " % t if cast_needed else "", value, "L" if t=="long" else "")


ANY = 0xFFFFFFFFFFFFFFFF

class VersionOp:
    def __init__(self, version=ANY, read=None, write=None, default=None, funnel=None, normalize=None):
        self.version = version
        self.read = read
        self.write = write
        self.default = default
        self.funnel = funnel
        self.normalize = normalize

    def __str__(self):
        return "[Version: %d, Read: '%s', Write: '%s', Default: '%s', Funnel: '%s', Normalize: '%s' ]" % \
            (self.version, self.read, self.write, self.default, self.funnel, self.normalize )

### FIXME: This class should really be cleaned up
class JType(object):
    """ Wrapper class to hold C to Java type conversion information. JTypes can have a 'public'
        and or 'private' java type associated with them and can define how those types can be
        read from and written to ByteBufs.

    """
    def __init__(self, pub_type, priv_type=None):
        self.pub_type = pub_type    # the type we expose externally, e.g. 'U8'
        if priv_type is None:
            priv_type = pub_type
        self.priv_type = priv_type  # the internal storage type
        self.ops = {}

    def set_priv_type(self, priv_type):
        self.priv_type = priv_type
        return self

    def op(self, version=ANY, read=None, write=None, default=None, funnel=None, normalize=None, pub_type=ANY):
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
            self.ops[(version, pub_type)] = VersionOp(version, read, write, default, funnel, normalize)
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

    def get_op(self, op_type, version, pub_type, default_value, arguments):
        ver = ANY if version is None else version.int_version

        if not "version" in arguments:
            arguments["version"] = version.dotless_version

        def lookup(ver, pub_type):
            if (ver, pub_type) in self.ops:
                return getattr(self.ops[(ver, pub_type)], op_type)
            else:
                return None

        _op = lookup(ver, pub_type) or lookup(ANY, pub_type) or default_value
        if callable(_op):
            return _op(**arguments)
        else:
            return reduce(lambda a,repl: a.replace("$%s" % repl[0], str(repl[1])),  arguments.items(), _op)

    def read_op(self, version=None, length=None, pub_type=True):
        """ return a Java stanza that reads a value of this JType from ByteBuf bb.
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
            length = "length - (bb.readerIndex() - start)"

        return self.get_op("read", version, pub_type,
            default_value='ChannelUtilsVer$version.read%s(bb)' % self.pub_type,
            arguments=dict(length=length)
            )

    def write_op(self, version=None, name=None, pub_type=True):
        """ return a Java stanza that writes a value of this JType contained in Java expression
        'name' to ByteBuf bb.
        @param name string containing Java expression that evaluations to the value to be written
        @param version int - OF wire version to generate expression for
        @param pub_type boolean use this JTypes 'public' (True), or private (False) representation
        @return string containing generated Java expression.
        """

        return self.get_op("write", version, pub_type,
            default_value='ChannelUtilsVer$version.write%s(bb, $name)' % self.pub_type,
            arguments=dict(name=name)
            )


    def default_op(self, version=None, pub_type=True):
        """ return a Java stanza that returns a default value of this JType.
        @param version JavaOFVersion
        @return string containing generated Java expression.
        """
        return self.get_op("default", version, pub_type,
            arguments = dict(),
            default_value = self.format_value(0) if self.is_primitive else "null"
        )

    def normalize_op(self, version=None, name=None, pub_type=True):
        """ return a Java stanza that returns a default value of this JType.
        @param version JavaOFVersion
        @return string containing generated Java expression.
        """
        return self.get_op("normalize", version, pub_type,
            arguments = dict(name=name),
            default_value = name
        )

    def skip_op(self, version=None, length=None):
        """ return a java stanza that skips an instance of JType in the input ByteBuf 'bb'.
            This is used in the Reader implementations for virtual classes (because after the
            discriminator field, the concrete Reader instance will re-read all the fields)
            Currently just delegates to read_op + throws away the result."""
        return self.read_op(version, length)

    def funnel_op(self, version=None, name=None, pub_type=True):
        t = self.pub_type if pub_type else self.priv_type
        return self.get_op("funnel", version, pub_type,
            arguments = dict(name=name),
            default_value =  '$name.putTo(sink)' if not self._is_primitive(pub_type) else "sink.put{}($name)".format(t[0].upper() + t[1:])
        )

    @property
    def is_primitive(self):
        return self._is_primitive()

    def _is_primitive(self, pub_type=True):
        """ return true if the pub_type is a java primitive type (and thus needs
        special treatment, because it doesn't have methods)"""
        t = self.pub_type if pub_type else self.priv_type
        return t in java_primitive_types

    @property
    def is_array(self):
        return self._is_array()

    def _is_array(self, pub_type=True):
        t = self.pub_type if pub_type else self.priv_type
        return t.endswith("[]")

# Create a default mapping for a list type. Type defauls to List<${java_mapping_of_name}>
def gen_enum_jtype(java_name, is_bitmask=False):
    if is_bitmask:
        java_type = "Set<{}>".format(java_name)
        default_value = "ImmutableSet.<{}>of()".format(java_name)
    else:
        java_type = java_name
        default_value = "null"

    serializer = "{}SerializerVer$version".format(java_name)

    return JType(java_type)\
            .op(read="{}.readFrom(bb)".format(serializer),
                write="{}.writeTo(bb, $name)".format(serializer),
                default=default_value,
                funnel="{}.putTo($name, sink)".format(serializer)
               )

def gen_list_jtype(java_base_name):
    # read op assumes the class has a public final static field READER that implements
    # OFMessageReader<$class> i.e., can deserialize an instance of class from a ByteBuf
    # write op assumes class implements Writeable
    return JType("List<{}>".format(java_base_name)) \
        .op(
            read= 'ChannelUtils.readList(bb, $length, {}Ver$version.READER)'.format(java_base_name), \
            write='ChannelUtils.writeList(bb, $name)',
            default="ImmutableList.<{}>of()".format(java_base_name),
            funnel='FunnelUtils.putList($name, sink)'
            )

def gen_fixed_length_string_jtype(length):
    return JType('String').op(
              read='ChannelUtils.readFixedLengthString(bb, {})'.format(length),
              write='ChannelUtils.writeFixedLengthString(bb, $name, {})'.format(length),
              default='""',
              funnel='sink.putUnencodedChars($name)'
            )

##### Predefined JType mappings
# FIXME: This list needs to be pruned / cleaned up. Most of these are schematic.

u8 =  JType('short', 'byte') \
        .op(read='U8.f(bb.readByte())', write='bb.writeByte(U8.t($name))', normalize="U8.normalize($name)", pub_type=True) \
        .op(read='bb.readByte()', write='bb.writeByte($name)', pub_type=False)
u8_list =  JType('List<U8>') \
        .op(read='ChannelUtils.readList(bb, $length, U8.READER)',
            write='ChannelUtils.writeList(bb, $name)',
            default='ImmutableList.<U8>of()',
            funnel='FunnelUtils.putList($name, sink)'
           )
u16 = JType('int', 'short') \
        .op(read='U16.f(bb.readShort())', write='bb.writeShort(U16.t($name))', normalize="U16.normalize($name)", pub_type=True) \
        .op(read='bb.readShort()', write='bb.writeShort($name)', pub_type=False)
u16_list = JType('List<U16>', 'short[]') \
        .op(read='ChannelUtils.readList(bb, $length, U16.READER)',
            write='ChannelUtils.writeList(bb, $name)',
            default='ImmutableList.<U16>of()',
            funnel='FunnelUtils.putList($name, sink)')
u32 = JType('long', 'int') \
        .op(read='U32.f(bb.readInt())', write='bb.writeInt(U32.t($name))', normalize="U32.normalize($name)", pub_type=True) \
        .op(read='bb.readInt()', write='bb.writeInt($name)', pub_type=False)
u32_list = JType('List<U32>', 'int[]') \
        .op(
                read='ChannelUtils.readList(bb, $length, U32.READER)',
                write='ChannelUtils.writeList(bb, $name)',
                default="ImmutableList.<U32>of()",
                funnel="FunnelUtils.putList($name, sink)")
u64_list = JType('List<U64>', 'int[]') \
        .op(
                read='ChannelUtils.readList(bb, $length, U64.READER)',
                write='ChannelUtils.writeList(bb, $name)',
                default="ImmutableList.<U64>of()",
                funnel="FunnelUtils.putList($name, sink)")
u8obj = JType('U8', 'U8') \
        .op(read='U8.of(bb.readByte())', write='bb.writeByte($name.getRaw())', default="U8.ZERO")
u16obj = JType('U16', 'U16') \
        .op(read='U16.of(bb.readShort())', write='bb.writeShort($name.getRaw())', default="U16.ZERO")
u32obj = JType('U32', 'U32') \
        .op(read='U32.of(bb.readInt())', write='bb.writeInt($name.getRaw())', default="U32.ZERO")
u64 = JType('U64', 'long') \
        .op(read='U64.ofRaw(bb.readLong())', write='bb.writeLong($name.getValue())', default="U64.ZERO", pub_type=True) \
        .op(read='bb.readLong()', write='bb.writeLong($name)', pub_type=False)
u128 = JType("U128") \
        .op(read='U128.read16Bytes(bb)',
            write='$name.write16Bytes(bb)',
            default='U128.ZERO')
of_port = JType("OFPort") \
         .op(version=1, read="OFPort.read2Bytes(bb)", write="$name.write2Bytes(bb)", default="OFPort.ANY") \
         .op(version=ANY, read="OFPort.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="OFPort.ANY")
# the same OFPort, but with a default value of ZERO, only for OF10 match
of_port_match_v1 = JType("OFPort") \
         .op(version=1, read="OFPort.read2Bytes(bb)", write="$name.write2Bytes(bb)", default="OFPort.ZERO")
actions_list = gen_list_jtype("OFAction")
instructions_list = gen_list_jtype("OFInstruction")
buckets_list = gen_list_jtype("OFBucket")
port_desc_list = gen_list_jtype("OFPortDesc")
packet_queue_list = gen_list_jtype("OFPacketQueue")
port_desc = JType('OFPortDesc') \
        .op(read='OFPortDescVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')
octets = JType('byte[]')\
        .op(read='ChannelUtils.readBytes(bb, $length)', \
            write='bb.writeBytes($name)', \
            default="new byte[0]",
            funnel="sink.putBytes($name)"
            );
of_match = JType('Match') \
        .op(read='ChannelUtilsVer$version.readOFMatch(bb)', \
            write='$name.writeTo(bb)',
            default="OFFactoryVer$version.MATCH_WILDCARD_ALL");
of_stat = JType('Stat') \
         .op(read='ChannelUtilsVer$version.readOFStat(bb)', write='$name.writeTo(bb)')
of_time = JType('OFTime') \
         .op(read='OFTimeVer$version.READER.readFrom(bb)', \
             write='$name.writeTo(bb)')
group_mod_cmd = JType('OFGroupModCommand', 'short') \
        .op(version=ANY, read="bb.readShort()", write="bb.writeShort($name)")
flow_mod_cmd = JType('OFFlowModCommand', 'short') \
        .op(version=1, read="bb.readShort()", write="bb.writeShort($name)") \
        .op(version=ANY, read="bb.readByte()", write="bb.writeByte($name)")
mac_addr = JType('MacAddress') \
        .op(read="MacAddress.read6Bytes(bb)", \
            write="$name.write6Bytes(bb)",
            default="MacAddress.NONE")
vxlan_ni = JType('VxlanNI') \
        .op(read="VxlanNI.read4Bytes(bb)", \
            write="$name.write4Bytes(bb)",
            default="VxlanNI.ZERO")

vfi = JType('VFI') \
        .op(read="VFI.read2Bytes(bb)", \
            write="$name.write2Bytes(bb)",
            default="VFI.ZERO")

port_name = gen_fixed_length_string_jtype(16)
desc_str = gen_fixed_length_string_jtype(256)
serial_num = gen_fixed_length_string_jtype(32)
table_name = gen_fixed_length_string_jtype(32)
str64 = gen_fixed_length_string_jtype(64)
ipv4 = JType("IPv4Address") \
        .op(read="IPv4Address.read4Bytes(bb)", \
            write="$name.write4Bytes(bb)",
            default='IPv4Address.NONE')
ipv4_list =  JType('List<IPv4Address>') \
        .op(read='ChannelUtils.readList(bb, $length, IPv4Address.READER)',
            write='ChannelUtils.writeList(bb, $name)',
            default='ImmutableList.<IPv4Address>of()',
            funnel="FunnelUtils.putList($name, sink)")
ipv6 = JType("IPv6Address") \
        .op(read="IPv6Address.read16Bytes(bb)", \
            write="$name.write16Bytes(bb)",
            default='IPv6Address.NONE')
ipv6_list =  JType('List<IPv46ddress>') \
        .op(read='ChannelUtils.readList(bb, $length, IPv6Address.READER)',
            write='ChannelUtils.writeList(bb, $name)',
            default='ImmutableList.<IPv6Address>of()',
            funnel="FunnelUtils.putList($name, sink)")
packetin_reason = gen_enum_jtype("OFPacketInReason")
transport_port = JType("TransportPort")\
        .op(read="TransportPort.read2Bytes(bb)",
            write="$name.write2Bytes(bb)",
            default="TransportPort.NONE")
eth_type = JType("EthType")\
        .op(read="EthType.read2Bytes(bb)",
            write="$name.write2Bytes(bb)",
            default="EthType.NONE")
vlan_vid = JType("VlanVid")\
        .op(version=ANY, read="VlanVid.read2Bytes(bb)", write="$name.write2Bytes(bb)", default="VlanVid.ZERO")
vlan_vid_match = JType("OFVlanVidMatch")\
        .op(version=1, read="OFVlanVidMatch.read2BytesOF10(bb)", write="$name.write2BytesOF10(bb)", default="OFVlanVidMatch.NONE") \
        .op(version=2, read="OFVlanVidMatch.read2BytesOF10(bb)", write="$name.write2BytesOF10(bb)", default="OFVlanVidMatch.NONE") \
        .op(version=ANY, read="OFVlanVidMatch.read2Bytes(bb)", write="$name.write2Bytes(bb)", default="OFVlanVidMatch.NONE")
vlan_pcp = JType("VlanPcp")\
        .op(read="VlanPcp.readByte(bb)",
            write="$name.writeByte(bb)",
            default="VlanPcp.NONE")
ip_dscp = JType("IpDscp")\
        .op(read="IpDscp.readByte(bb)",
            write="$name.writeByte(bb)",
            default="IpDscp.NONE")
ip_ecn = JType("IpEcn")\
        .op(read="IpEcn.readByte(bb)",
            write="$name.writeByte(bb)",
            default="IpEcn.NONE")
ip_proto = JType("IpProtocol")\
        .op(read="IpProtocol.readByte(bb)",
            write="$name.writeByte(bb)",
            default="IpProtocol.NONE")
icmpv4_type = JType("ICMPv4Type")\
        .op(read="ICMPv4Type.readByte(bb)",
            write="$name.writeByte(bb)",
            default="ICMPv4Type.NONE")
icmpv4_code = JType("ICMPv4Code")\
        .op(read="ICMPv4Code.readByte(bb)",
            write="$name.writeByte(bb)",
            default="ICMPv4Code.NONE")
arp_op = JType("ArpOpcode")\
        .op(read="ArpOpcode.read2Bytes(bb)",
            write="$name.write2Bytes(bb)",
            default="ArpOpcode.NONE")
ipv6_flabel = JType("IPv6FlowLabel")\
        .op(read="IPv6FlowLabel.read4Bytes(bb)",
            write="$name.write4Bytes(bb)",
            default="IPv6FlowLabel.NONE")
packet_type = JType("PacketType") \
        .op(read="PacketType.read4Bytes(bb)",
            write="$name.write4Bytes(bb)")
metadata = JType("OFMetadata")\
        .op(read="OFMetadata.read8Bytes(bb)",
            write="$name.write8Bytes(bb)",
            default="OFMetadata.NONE")
oxm = JType("OFOxm<?>")\
        .op(  read="OFOxmVer$version.READER.readFrom(bb)",
              write="$name.writeTo(bb)")
oxm_list = JType("OFOxmList") \
        .op(
            read= 'OFOxmList.readFrom(bb, $length, OFOxmVer$version.READER)', \
            write='$name.writeTo(bb)',
            default="OFOxmList.EMPTY")
connection_uri = JType("OFConnectionIndex") \
        .op(read="OFConnectionIndex.read4Bytes(bb)",
            write="$name.write4Bytes(bb)")
#Fixed Ver15 (FIXME for 1.5 + versions)
oxs = JType("OFOxs<?>")\
        .op(read="OFOxsVer15.READER.readFrom(bb)",
            write="$name.writeTo(bb)")
oxs_list = JType("OFOxsList") \
        .op(read= 'OFOxsList.readFrom(bb, $length, OFOxsVer15.READER)', \
            write='$name.writeTo(bb)',
            default="OFOxsList.EMPTY")
meter_features = JType("OFMeterFeatures")\
        .op(read="OFMeterFeaturesVer$version.READER.readFrom(bb)",
            write="$name.writeTo(bb)")
bsn_vport = JType("OFBsnVport")\
        .op(read="OFBsnVportVer$version.READER.readFrom(bb)",
            write="$name.writeTo(bb)")
flow_wildcards = JType("int") \
        .op(read='bb.readInt()',
            write='bb.writeInt($name)',
            default="OFFlowWildcardsSerializerVer$version.ALL_VAL")
table_stats_wildcards = JType("int") \
        .op(read='bb.readInt()',
            write='bb.writeInt($name)')
port_bitmap_128 = JType('OFBitMask128') \
            .op(read='OFBitMask128.read16Bytes(bb)',
                write='$name.write16Bytes(bb)',
                default='OFBitMask128.NONE')
port_bitmap_512 = JType('OFBitMask512') \
            .op(read='OFBitMask512.read64Bytes(bb)',
                write='$name.write64Bytes(bb)',
                default='OFBitMask512.NONE')
table_id = JType("TableId") \
        .op(read='TableId.readByte(bb)',
            write='$name.writeByte(bb)',
            default='TableId.ALL')
table_id_default_zero = JType("TableId") \
        .op(read='TableId.readByte(bb)',
            write='$name.writeByte(bb)',
            default='TableId.ZERO')
of_aux_id = JType("OFAuxId") \
        .op(read='OFAuxId.readByte(bb)',
            write='$name.writeByte(bb)',
            default='OFAuxId.MAIN')
of_version = JType("OFVersion", 'byte') \
            .op(read='bb.readByte()', write='bb.writeByte($name)')

port_speed = JType("PortSpeed")

error_type = JType("OFErrorType")
of_message = JType("OFMessage")\
            .op(read="OFMessageVer$version.READER.readFrom(bb)",
                write="$name.writeTo(bb)")

of_type = JType("OFType", 'byte') \
            .op(read='bb.readByte()', write='bb.writeByte($name)')
action_type= gen_enum_jtype("OFActionType")\
               .set_priv_type("short")\
               .op(read='bb.readShort()', write='bb.writeShort($name)', pub_type=False)
instruction_type = gen_enum_jtype("OFInstructionType")\
               .set_priv_type('short') \
               .op(read='bb.readShort()', write='bb.writeShort($name)', pub_type=False)
buffer_id = JType("OFBufferId") \
            .op(read="OFBufferId.of(bb.readInt())", write="bb.writeInt($name.getInt())", default="OFBufferId.NO_BUFFER")
boolean = JType("boolean", "byte") \
        .op(read='(bb.readByte() != 0)',
            write='bb.writeByte($name ? 1 : 0)',
            default="false")
datapath_id = JType("DatapathId") \
        .op(read='DatapathId.of(bb.readLong())',
            write='bb.writeLong($name.getLong())',
            default='DatapathId.NONE')
action_type_set = JType("Set<OFActionType>") \
        .op(read='ChannelUtilsVer10.readSupportedActions(bb)',
            write='ChannelUtilsVer10.writeSupportedActions(bb, $name)',
            default='ImmutableSet.<OFActionType>of()',
            funnel='ChannelUtilsVer10.putSupportedActionsTo($name, sink)')
of_group = JType("OFGroup") \
         .op(version=ANY, read="OFGroup.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="OFGroup.ALL")
of_group_default_any = JType("OFGroup") \
         .op(version=ANY, read="OFGroup.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="OFGroup.ANY")
# the outgroup field of of_flow_stats_request has a special default value
of_group_default_any = JType("OFGroup") \
         .op(version=ANY, read="OFGroup.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="OFGroup.ANY")
buffer_id = JType("OFBufferId") \
         .op(read="OFBufferId.of(bb.readInt())", write="bb.writeInt($name.getInt())", default="OFBufferId.NO_BUFFER")
lag_id = JType("LagId") \
         .op(version=ANY, read="LagId.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="LagId.NONE")
vrf = JType("VRF") \
         .op(version=ANY, read="VRF.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="VRF.ZERO")
class_id = JType("ClassId") \
         .op(version=ANY, read="ClassId.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="ClassId.NONE")
boolean_value = JType('OFBooleanValue', 'OFBooleanValue') \
        .op(read='OFBooleanValue.of(bb.readByte() != 0)', write='bb.writeByte($name.getInt())', default="OFBooleanValue.FALSE")
gen_table_id = JType("GenTableId") \
        .op(read='GenTableId.read2Bytes(bb)',
            write='$name.write2Bytes(bb)',
           )
bundle_id = JType("BundleId") \
        .op(read='BundleId.read4Bytes(bb)',
            write='$name.write4Bytes(bb)',
           )
udf = JType("UDF") \
         .op(version=ANY, read="UDF.read4Bytes(bb)", write="$name.write4Bytes(bb)", default="UDF.ZERO")
error_cause_data = JType("OFErrorCauseData") \
         .op(version=ANY, read="OFErrorCauseData.read(bb, $length, OFVersion.OF_$version)", write="$name.writeTo(bb)", default="OFErrorCauseData.NONE");

var_string = JType('String').op(
              read='ChannelUtils.readFixedLengthString(bb, $length)',
              write='ChannelUtils.writeFixedLengthString(bb, $name, $name.length())',
              default='""',
              funnel='sink.putUnencodedChars($name)'
            )

generic_t = JType("T")

table_desc = JType('OFTableDesc') \
        .op(read='OFTableDescVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

bsn_unit = JType('OFBsnUnit') \
        .op(read='OFBsnUnitVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

bsn_module_eeprom_transceiver = JType('OFBsnModuleEepromTransceiver') \
        .op(read='OFBsnModuleEepromTransceiverVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

port_desc_prop_bsn_alarm = JType('OFPortDescPropBsnAlarm') \
        .op(read='OFPortDescPropBsnAlarm$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

port_desc_prop_bsn_diag = JType('OFPortDescPropBsnDiag') \
        .op(read='OFPortDescPropBsnDiag$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

controller_status_entry = JType('OFControllerStatusEntry') \
        .op(read='OFControllerStatusEntryVer$version.READER.readFrom(bb)', \
            write='$name.writeTo(bb)')

default_mtype_to_jtype_convert_map = {
        'uint8_t' : u8,
        'uint16_t' : u16,
        'uint32_t' : u32,
        'uint64_t' : u64,
        'uint128_t' : u128,
        'of_port_no_t' : of_port,
        'list(of_action_t)' : actions_list,
        'list(of_instruction_t)' : instructions_list,
        'list(of_bucket_t)': buckets_list,
        'list(of_port_desc_t)' : port_desc_list,
        'list(of_packet_queue_t)' : packet_queue_list,
        'list(of_uint64_t)' : u64_list,
        'list(of_uint32_t)' : u32_list,
        'list(of_uint16_t)' : u16_list,
        'list(of_uint8_t)' : u8_list,
        'list(of_oxm_t)' : oxm_list,
        'list(of_oxs_t)' : oxs_list,
        'list(of_ipv4_t)' : ipv4_list,
        'list(of_ipv6_t)' : ipv6_list,
        'of_octets_t' : octets,
        'of_match_t': of_match,
        'of_stat_t' : of_stat,
        'of_controller_uri_t' : connection_uri,
        'of_fm_cmd_t': flow_mod_cmd,
        'of_mac_addr_t': mac_addr,
        'of_port_desc_t': port_desc,
        'of_desc_str_t': desc_str,
        'of_serial_num_t': serial_num,
        'of_port_name_t': port_name,
        'of_table_name_t': table_name,
        'of_str64_t': str64,
        'of_ipv4_t': ipv4,
        'of_ipv6_t': ipv6,
        'of_wc_bmap_t': flow_wildcards,
        'of_oxm_t': oxm,
        'of_oxs_t': oxs,
        'of_meter_features_t': meter_features,
        'of_bitmap_128_t': port_bitmap_128,
        'of_bitmap_512_t': port_bitmap_512,
        'of_checksum_128_t': u128,
        'of_bsn_vport_t': bsn_vport,
        'of_table_desc_t': table_desc,
        'of_bsn_unit_t': bsn_unit,
        'ofp_bsn_module_eeprom_transceiver_t': bsn_module_eeprom_transceiver,
        'of_port_desc_prop_bsn_alarm_t': port_desc_prop_bsn_alarm,
        'of_port_desc_prop_bsn_diag_t': port_desc_prop_bsn_diag,
        'of_controller_status_entry_t' : controller_status_entry,
        'of_time_t' : of_time,
        'of_header_t' : of_message,
        }

## Map that defines exceptions from the standard loxi->java mapping scheme
# map of {<loxi_class_name> : { <loxi_member_name> : <JType instance> } }
exceptions = {
        'of_packet_in': { 'data' : octets, 'reason': packetin_reason },
        'of_oxm_tcp_src' : { 'value' : transport_port },
        'of_oxm_tcp_src_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_tcp_dst' : { 'value' : transport_port },
        'of_oxm_tcp_dst_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_udp_src' : { 'value' : transport_port },
        'of_oxm_udp_src_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_udp_dst' : { 'value' : transport_port },
        'of_oxm_udp_dst_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_sctp_src' : { 'value' : transport_port },
        'of_oxm_sctp_src_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_sctp_dst' : { 'value' : transport_port },
        'of_oxm_sctp_dst_masked' : { 'value' : transport_port, 'value_mask' : transport_port },
        'of_oxm_eth_type' : { 'value' : eth_type },
        'of_oxm_eth_type_masked' : { 'value' : eth_type, 'value_mask' : eth_type },
        'of_oxm_vlan_vid' : { 'value' : vlan_vid_match },
        'of_oxm_vlan_vid_masked' : { 'value' : vlan_vid_match, 'value_mask' : vlan_vid_match },
        'of_oxm_vlan_pcp' : { 'value' : vlan_pcp },
        'of_oxm_vlan_pcp_masked' : { 'value' : vlan_pcp, 'value_mask' : vlan_pcp },
        'of_oxm_ip_dscp' : { 'value' : ip_dscp },
        'of_oxm_ip_dscp_masked' : { 'value' : ip_dscp, 'value_mask' : ip_dscp },
        'of_oxm_ip_ecn' : { 'value' : ip_ecn },
        'of_oxm_ip_ecn_masked' : { 'value' : ip_ecn, 'value_mask' : ip_ecn },
        'of_oxm_ip_proto' : { 'value' : ip_proto },
        'of_oxm_ip_proto_masked' : { 'value' : ip_proto, 'value_mask' : ip_proto },
        'of_oxm_icmpv4_type' : { 'value' : icmpv4_type },
        'of_oxm_icmpv4_type_masked' : { 'value' : icmpv4_type, 'value_mask' : icmpv4_type },
        'of_oxm_icmpv4_code' : { 'value' : icmpv4_code },
        'of_oxm_icmpv4_code_masked' : { 'value' : icmpv4_code, 'value_mask' : icmpv4_code },
        'of_oxm_arp_op' : { 'value' : arp_op },
        'of_oxm_arp_op_masked' : { 'value' : arp_op, 'value_mask' : arp_op },
        'of_oxm_arp_spa' : { 'value' : ipv4 },
        'of_oxm_arp_spa_masked' : { 'value' : ipv4, 'value_mask' : ipv4 },
        'of_oxm_arp_tpa' : { 'value' : ipv4 },
        'of_oxm_arp_tpa_masked' : { 'value' : ipv4, 'value_mask' : ipv4 },
        'of_oxm_ipv6_flabel' : { 'value' : ipv6_flabel },
        'of_oxm_ipv6_flabel_masked' : { 'value' : ipv6_flabel, 'value_mask' : ipv6_flabel },
        'of_oxm_metadata' : { 'value' : metadata },
        'of_oxm_metadata_masked' : { 'value' : metadata, 'value_mask' : metadata },

        'of_oxm_icmpv6_code' : { 'value' : u8obj },
        'of_oxm_icmpv6_code_masked' : { 'value' : u8obj, 'value_mask' : u8obj },
        'of_oxm_icmpv6_type' : { 'value' : u8obj },
        'of_oxm_icmpv6_type_masked' : { 'value' : u8obj, 'value_mask' : u8obj },
        'of_oxm_mpls_label' : { 'value' : u32obj },
        'of_oxm_mpls_label_masked' : { 'value' : u32obj, 'value_mask' : u32obj },
        'of_oxm_mpls_tc' : { 'value' : u8obj },
        'of_oxm_mpls_tc_masked' : { 'value' : u8obj, 'value_mask' : u8obj },
        'of_oxm_mpls_bos' : { 'value' : boolean_value },
        'of_oxm_mpls_bos_masked' : { 'value' : boolean_value, 'value_mask' : boolean_value },
        'of_oxm_ipv6_exthdr' : { 'value' : u16obj },
        'of_oxm_ipv6_exthdr_masked' : { 'value' : u16obj, 'value_mask' : u16obj },
        'of_oxm_pbb_uca' : { 'value' : boolean_value },
        'of_oxm_pbb_uca_masked' : { 'value' : boolean_value, 'value_mask' : boolean_value },

        'of_oxm_tcp_flags' : { 'value' : u16obj },
        'of_oxm_tcp_flags_masked' : { 'value' : u16obj, 'value_mask' : u16obj },
        'of_oxm_ovs_tcp_flags' : { 'value' : u16obj },
        'of_oxm_ovs_tcp_flags_masked' : { 'value' : u16obj, 'value_mask' : u16obj },
        'of_oxm_actset_output' : { 'value' : of_port },
        'of_oxm_actset_output_masked' : { 'value' : of_port, 'value_mask' : of_port },
        'of_oxm_packet_type' : { 'value' : packet_type },
        'of_oxm_packet_type_masked' : { 'value' : packet_type, 'value_mask' : packet_type },

        'of_oxs_byte_count' : { 'value' : u64 },
        'of_oxs_duration' : { 'value' : u64 },
        'of_oxs_flow_count' : { 'value' : u32obj },
        'of_oxs_idle_time' : { 'value' : u64 },
        'of_oxs_packet_count' : { 'value' : u64 },

        'of_oxm_bsn_in_ports_128' : { 'value': port_bitmap_128 },
        'of_oxm_bsn_in_ports_128_masked' : { 'value': port_bitmap_128, 'value_mask': port_bitmap_128 },

        'of_oxm_bsn_in_ports_512' : { 'value': port_bitmap_512 },
        'of_oxm_bsn_in_ports_512_masked' : { 'value': port_bitmap_512, 'value_mask': port_bitmap_512 },

        'of_oxm_bsn_lag_id' : { 'value' : lag_id },
        'of_oxm_bsn_lag_id_masked' : { 'value' : lag_id, 'value_mask' : lag_id },

        'of_oxm_bsn_vrf' : { 'value' : vrf },
        'of_oxm_bsn_vrf_masked' : { 'value' : vrf, 'value_mask' : vrf },

        'of_oxm_bsn_global_vrf_allowed' : { 'value' : boolean_value },
        'of_oxm_bsn_global_vrf_allowed_masked' : { 'value' : boolean_value, 'value_mask' : boolean_value },

        'of_oxm_bsn_l3_interface_class_id' : { 'value' : class_id },
        'of_oxm_bsn_l3_interface_class_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_l3_src_class_id' : { 'value' : class_id },
        'of_oxm_bsn_l3_src_class_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_l3_dst_class_id' : { 'value' : class_id },
        'of_oxm_bsn_l3_dst_class_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_egr_port_group_id' : { 'value' : class_id },
        'of_oxm_bsn_egr_port_group_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_ingress_port_group_id' : { 'value' : class_id },
        'of_oxm_bsn_ingress_port_group_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_udf0' : { 'value' : udf },
        'of_oxm_bsn_udf0_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf1' : { 'value' : udf },
        'of_oxm_bsn_udf1_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf2' : { 'value' : udf },
        'of_oxm_bsn_udf2_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf3' : { 'value' : udf },
        'of_oxm_bsn_udf3_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf4' : { 'value' : udf },
        'of_oxm_bsn_udf4_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf5' : { 'value' : udf },
        'of_oxm_bsn_udf5_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf6' : { 'value' : udf },
        'of_oxm_bsn_udf6_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_udf7' : { 'value' : udf },
        'of_oxm_bsn_udf7_masked' : { 'value' : udf, 'value_mask' : udf },

        'of_oxm_bsn_tcp_flags' : { 'value' : u16obj },
        'of_oxm_bsn_tcp_flags_masked' : { 'value' : u16obj, 'value_mask' : u16obj },

        'of_oxm_bsn_vlan_xlate_port_group_id' : { 'value' : class_id },
        'of_oxm_bsn_vlan_xlate_port_group_id_masked' : { 'value' : class_id, 'value_mask' : class_id },

        'of_oxm_bsn_l2_cache_hit' : { 'value' : boolean_value },
        'of_oxm_bsn_l2_cache_hit_masked' : { 'value' : boolean_value, 'value_mask' : boolean_value },

        'of_oxm_bsn_vxlan_network_id' : { 'value' : vxlan_ni },
        'of_oxm_bsn_vxlan_network_id_masked' : { 'value' : vxlan_ni, 'value_mask' : vxlan_ni},

        'of_oxm_bsn_inner_eth_dst' : { 'value' : mac_addr },
        'of_oxm_bsn_inner_eth_dst_masked' : { 'value' : mac_addr, 'value_mask' : mac_addr },

        'of_oxm_bsn_inner_eth_src' : { 'value' : mac_addr },
        'of_oxm_bsn_inner_eth_src_masked' : { 'value' : mac_addr, 'value_mask' : mac_addr },

        'of_oxm_bsn_inner_vlan_vid' : { 'value' : vlan_vid_match },
        'of_oxm_bsn_inner_vlan_vid_masked' : { 'value' : vlan_vid_match, 'value_mask' : vlan_vid_match },

        'of_oxm_bsn_vfi' : { 'value' : vfi },
        'of_oxm_bsn_vfi_masked' : { 'value' : vfi, 'value_mask' : vfi },

        'of_oxm_bsn_ip_fragmentation' : { 'value' : boolean_value },
        'of_oxm_bsn_ip_fragmentation_masked' : { 'value' : boolean_value, 'value_mask' : boolean_value },

        'of_oxm_bsn_ifp_class_id' : { 'value' : class_id },
        'of_oxm_bsn_ifp_class_id_masked' : { 'value' : class_id, 'value_mask' : class_id },
        
        'of_oxm_conn_tracking_state' : { 'value' : u32obj },
        'of_oxm_conn_tracking_state_masked' : { 'value' : u32obj, 'value_mask' : u32obj },

        'of_oxm_conn_tracking_zone' : { 'value' : u16obj },
        'of_oxm_conn_tracking_zone_masked' : { 'value' : u16obj, 'value_mask' : u16obj },

        'of_oxm_conn_tracking_mark' : { 'value' : u32obj },
        'of_oxm_conn_tracking_mark_masked' : { 'value' : u32obj, 'value_mask' : u32obj },
        
        'of_oxm_conn_tracking_label' : { 'value' : u128 },
        'of_oxm_conn_tracking_label_masked' : { 'value' : u128, 'value_mask' : u128 },
        
        'of_oxm_conn_tracking_nw_proto' : { 'value' : u8obj },
        'of_oxm_conn_tracking_nw_proto_masked' : { 'value' : u8obj, 'value_mask' : u8obj },

        'of_oxm_conn_tracking_nw_src' : { 'value' : u32obj },
        'of_oxm_conn_tracking_nw_src_masked' : { 'value' : u32obj, 'value_mask' : u32obj },

        'of_oxm_conn_tracking_nw_dst' : { 'value' : u32obj },
        'of_oxm_conn_tracking_nw_dst_masked' : { 'value' : u32obj, 'value_mask' : u32obj },
        
        'of_oxm_conn_tracking_ipv6_src' : { 'value' : ipv6 },
        'of_oxm_conn_tracking_ipv6_src_masked' : { 'value' : ipv6, 'value_mask' : ipv6 },
        
        'of_oxm_conn_tracking_ipv6_dst' : { 'value' : ipv6 },
        'of_oxm_conn_tracking_ipv6_dst_masked' : { 'value' : ipv6, 'value_mask' : ipv6 },

        'of_oxm_conn_tracking_tp_src' : { 'value' : transport_port },
        'of_oxm_conn_tracking_tp_src_masked' : { 'value' : transport_port, 'value_mask' : transport_port },

        'of_oxm_conn_tracking_tp_dst' : { 'value' : transport_port },
        'of_oxm_conn_tracking_tp_dst_masked' : { 'value' : transport_port, 'value_mask' : transport_port },

        'of_table_stats_entry': { 'wildcards': table_stats_wildcards },
        'of_match_v1': { 'vlan_vid' : vlan_vid_match, 'vlan_pcp': vlan_pcp,
                'eth_type': eth_type, 'ip_dscp': ip_dscp, 'ip_proto': ip_proto,
                'tcp_src': transport_port, 'tcp_dst': transport_port,
                'in_port': of_port_match_v1
                },
        'of_bsn_set_l2_table_request': { 'l2_table_enable': boolean },
        'of_bsn_set_l2_table_reply': { 'l2_table_enable': boolean },
        'of_bsn_set_pktin_suppression_request': { 'enabled': boolean },
        'of_bsn_controller_connection': { 'auxiliary_id' : of_aux_id},
        'of_flow_stats_request': { 'out_group': of_group_default_any },
        'of_aggregate_stats_request': { 'out_group': of_group_default_any },

        'of_action_bsn_mirror': { 'dest_port': of_port },
        'of_action_push_mpls': { 'ethertype': eth_type },
        'of_action_push_pbb': { 'ethertype': eth_type },
        'of_action_push_vlan': { 'ethertype': eth_type },
        'of_action_pop_mpls': { 'ethertype': eth_type },
        'of_action_set_nw_dst': { 'nw_addr': ipv4 },
        'of_action_set_nw_ecn': { 'nw_ecn': ip_ecn },
        'of_action_set_nw_src': { 'nw_addr': ipv4 },
        'of_action_set_tp_dst': { 'tp_port': transport_port },
        'of_action_set_tp_src': { 'tp_port': transport_port },
        'of_action_set_vlan_pcp': { 'vlan_pcp': vlan_pcp },
        'of_action_set_vlan_vid': { 'vlan_vid': vlan_vid },

        'of_group_mod' : { 'command' : group_mod_cmd },
        'of_group_add' : { 'command' : group_mod_cmd },
        'of_group_modify' : { 'command' : group_mod_cmd },
        'of_group_delete' : { 'command' : group_mod_cmd },
        'of_group_insert_bucket': {'command' : group_mod_cmd },
        'of_group_remove_bucket' : {'command' : group_mod_cmd },

        'of_bucket' : { 'watch_group': of_group },

        'of_bsn_tlv_vlan_vid' : { 'value' : vlan_vid },
        'of_bsn_table_set_buckets_size' : { 'table_id' : table_id },
        'of_bsn_gentable_entry_add' : { 'table_id' : gen_table_id },
        'of_bsn_log': { 'data': var_string },

        'of_features_reply' : { 'auxiliary_id' : of_aux_id},

        'of_bundle_add_msg' : { 'data' : of_message },
        'of_flow_stats_request' : { 'out_group' : of_group },
        'of_flow_lightweight_stats_request' : { 'out_group' : of_group }
}


@memoize
def enum_java_types():
    enum_types = {}
    for enum in loxi_globals.unified.enums:
        java_name = name_c_to_caps_camel(re.sub(r'_t$', "", enum.name))
        enum_types[enum.name] = gen_enum_jtype(java_name, enum.is_bitmask)
    return enum_types

def make_match_field_jtype(sub_type_name="?"):
    return JType("MatchField<{}>".format(sub_type_name))

def make_stat_field_jtype(sub_type_name="?"):
    return JType("StatField<{}>".format(sub_type_name))

def make_oxm_jtype(sub_type_name="?"):
    return JType("OFOxm<{}>".format(sub_type_name))

def make_oxs_jtype(sub_type_name="?"):
    return JType("OFOxs<{}>".format(sub_type_name))

def list_cname_to_java_name(c_type):
    m = re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type)
    if not m:
        raise Exception("Not a recgonized standard list type declaration: %s" % c_type)
    base_name = m.group(1)
    return "OF" + name_c_to_caps_camel(base_name)

#### main entry point for conversion of LOXI types (c_types) Java types.
# FIXME: This badly needs a refactoring

def convert_to_jtype(obj_name, field_name, c_type):
    """ Convert from a C type ("uint_32") to a java type ("U32")
    and return a JType object with the size, internal type, and marshalling functions"""
    if obj_name in exceptions and field_name in exceptions[obj_name]:
        return exceptions[obj_name][field_name]
    elif ( obj_name == "of_header" or loxi_utils.class_is_message(obj_name)) and field_name == "type" and c_type == "uint8_t":
        return of_type
    elif field_name == "type" and re.match(r'of_action.*', obj_name):
        return action_type
    elif field_name == "err_type":
        return JType("OFErrorType", 'short') \
            .op(read='bb.readShort()', write='bb.writeShort($name)')
    elif loxi_utils.class_is(obj_name, "of_error_msg") and field_name == "data":
        return error_cause_data
    elif field_name == "stats_type":
        return JType("OFStatsType", 'short') \
            .op(read='bb.readShort()', write='bb.writeShort($name)')
    elif field_name == "type" and re.match(r'of_instruction.*', obj_name):
        return instruction_type
    elif loxi_utils.class_is(obj_name, "of_flow_mod") and field_name == "table_id" and c_type == "uint8_t":
        return table_id_default_zero
    elif loxi_utils.class_is(obj_name, "of_flow_mod") and field_name == "out_group" and c_type == "uint32_t":
        return of_group_default_any
    elif field_name == "table_id" and c_type == "uint8_t":
        return table_id
    elif field_name == "version" and c_type == "uint8_t":
        return of_version
    elif field_name == "buffer_id" and c_type == "uint32_t":
        return buffer_id
    elif field_name == "group_id" and c_type == "uint32_t":
        return of_group
    elif field_name == 'datapath_id':
        return datapath_id
    elif field_name == 'actions' and obj_name == 'of_features_reply':
        return action_type_set
    elif field_name == "table_id" and re.match(r'of_bsn_gentable.*', obj_name):
        return gen_table_id
    elif field_name == "bundle_id" and re.match(r'of_bundle_.*', obj_name):
        return bundle_id
    elif c_type in default_mtype_to_jtype_convert_map:
        return default_mtype_to_jtype_convert_map[c_type]
    elif re.match(r'list\(of_([a-zA-Z_]+)_t\)', c_type):
        return gen_list_jtype(list_cname_to_java_name(c_type))
    elif c_type in enum_java_types():
        return enum_java_types()[c_type]
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
        "uint128_t": JType("long").op(read="bb.readLong()", write="bb.writeLong($name)"),
}

def convert_enum_wire_type_to_jtype(wire_type):
    return enum_wire_types[wire_type]

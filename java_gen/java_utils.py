import os
import errno
import re
import subprocess
import time

def name_c_to_camel(name):
    """ 'of_stats_reply' -> 'ofStatsReply' """
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
            read_op = 'ChannelUtils.read%s(this, "$name", bb)' % self.pub_type
        if write_op is None:
            write_op = 'ChannelUtils.write($name)'
        self._read_op = read_op
        self.write_op = write_op

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

    def read_op(self, version=None):
        if callable(self._read_op):
            return self._read_op(version)
        else:
            return self._read_op

hello_elem_list = JType("List<OFHelloElement>",
        read_op = 'ChannelUtils.readHelloElementList(bb)',
        write_op = 'ChannelUtils.writeHelloElementList(bb)'
        )

u8 =  JType('byte',  size=1, read_op='bb.readByte()',
        write_op='bb.writeByte($name)')
u16 = JType('int', 'int', size=2, read_op='U16.f(bb.readShort())',
        write_op='bb.writeShort(U16.t($name))')
u32 = JType('int', 'int',   size=4, read_op='bb.readInt()',
        write_op='bb.writeInt($name)')
u64 = JType('U64', 'U64', size=8, read_op='U64.of(bb.readLong())',
        write_op='bb.writeLong($name.getLong())')
of_port= JType('OFPort',size=None,
        read_op=lambda(version): 'OFPort.ofShort(bb.readShort())' if version.int_version < 2 else 'OFPort.ofInt(bb.readInt())',
        write_op='$name.writeTo(bb)')
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
        read_op = 'ChannelUtils.readActionsList(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeList(this, $name)')
instructions_list = JType('List<OFInstruction>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readInstructionsList(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeList(this, $name)')
buckets_list = JType('List<OFBucket>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readBucketList(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeList(this, $name)')
port_desc_list = JType('List<OFPhysicalPort>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readPhysicalPortList(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeList(this, $name)')
port_desc = JType('OFPhysicalPort', size='$name.getLength()',
        read_op = 'ChannelUtils.readPhysicalPort(bb)',
        write_op = 'ChannelUtils.write(this, $name)')
packet_queue_list = JType('List<OFPacketQueue>', size='ChannelUtils.calcListSize($name)',
        read_op = 'ChannelUtils.readPacketQueueList(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeList(this, $name)')
octets = JType('byte[]', size="length - MINIMUM_LENGTH",
        read_op = 'ChannelUtils.readBytes(bb, length - MINIMUM_LENGTH)',
        write_op = 'ChannelUtils.writeBytes(bb, this.$name)')
of_match = JType('Match', size="$name.getLength()",
        read_op = 'ChannelUtils.readOFMatch(bb)',
        write_op = 'ChannelUtils.writeOFMatch(this, $name)')
flow_mod_cmd = JType('OFFlowModCmd', size="$name.getLength()",
        read_op = 'ChannelUtils.readOFFlowModCmd(bb)',
        write_op = 'ChannelUtils.writeOFFlowModCmd(this, $name)')
mac_addr = JType('MacAddress', 'byte[]', size=6,
        read_op = 'MacAddress.readFrom(bb)',
        write_op = 'ChannelUtils.writeBytes(bb, $name)')
bsn_interface_list = JType("List<OFBsnInterface>",
        read_op = 'ChannelUtils.readBsnInterfaceList(bb)',
        write_op = 'ChannelUtils.writeBsnInterfaceList(bb)'
        )
meter_band_list = JType("List<OFMeterBand>",
        read_op = 'ChannelUtils.readMeterBandList(bb)',
        write_op = 'ChannelUtils.writeMeterBandList(bb, $name)'
        )





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
        'of_list_action_t' : actions_list,
        'of_list_instruction_t' : instructions_list,
        'of_list_bucket_t': buckets_list,
        'of_list_port_desc_t' : port_desc_list,
        'of_list_packet_queue_t' : packet_queue_list,
        'of_octets_t' : octets,
        'of_match_t': of_match,
        'of_fm_cmd_t': flow_mod_cmd,
        'of_mac_addr_t': mac_addr,
        'of_port_desc_t': port_desc,
        'of_list_bsn_interface_t': bsn_interface_list,
        'of_list_hello_elem_t': hello_elem_list,
        'of_list_meter_band_t': meter_band_list
        }

## This is where we drop in special case handling for certain types
exceptions = {
        'OFPacketIn': {
            'data' : octets
            },
        }


def convert_to_jtype(obj_name, field_name, c_type):
    """ Convert from a C type ("uint_32") to a java type ("U32")
    and return a JType object with the size, internal type, and marshalling functions"""
    if obj_name in exceptions and field_name in exceptions[obj_name]:
        return exceptions[obj_name][field_name]
    elif c_type in default_mtype_to_jtype_convert_map:
        return default_mtype_to_jtype_convert_map[c_type]
    else:
        print "WARN: Couldn't find java type conversion for '%s' in %s:%s" % (c_type, obj_name, field_name)
        jtype = name_c_to_caps_camel(c_type)      # not sure... try to fake it
        return JType(jtype)


def mkdir_p(path):
    """ Emulates `mkdir -p` """
    try:
        os.makedirs(path)
    except OSError as exc: # Python >2.5
        if exc.errno == errno.EEXIST:
            pass
        else: raise

def copy_file_with_boiler_plate(src_name, dst_name, with_boiler=True):
    with open("java_gen/pre-written/%s" % src_name, "r") as src:
        with open(dst_name, "w") as dst:
            if with_boiler:
                print_boiler_plate(os.path.basename(dst_name), dst)
            dst.writelines(src.readlines())

def frob(s, **kwargs):
    """ Step through string s and for each key in kwargs,
         replace $key with kwargs[key] in s.
    """
    for k,v in kwargs.iteritems():
        s = s.replace('$%s' % k, v)
    return s

def copy_prewrite_tree(basedir):
    """ Recursively copy the directory structure from ./java_gen/pre-write
       into $basedir"""
    print "Copying pre-written files into %s" % basedir
#    subprocess.call("cd java_gen/pre-written && tar cpf - . | ( cd ../../%s && tar xvpf - )" % basedir,
#            shell=True)

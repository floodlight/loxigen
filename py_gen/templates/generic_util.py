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
:: include('_copyright.py')
"""
Utility functions independent of the protocol version
"""

:: include('_autogen.py')

import loxi
import struct
import functools

def pack_list(values):
:: if pyversion == 2:
    return "".join([x.pack() for x in values])
:: else:
    return functools.reduce(lambda x,y: x+y, [x.pack() for x in values], b'')
:: #endif

def unpack_list(reader, deserializer):
    """
    The deserializer function should take an OFReader and return the new object.
    """
    entries = []
    while not reader.is_empty():
        entries.append(deserializer(reader))
    return entries

def pad_to(alignment, length):
    """
    Return a string of zero bytes that will pad a string of length 'length' to
    a multiple of 'alignment'.
    """
:: if pyversion == 2:
    return "\x00" * ((length + alignment - 1)/alignment*alignment - length)
:: else:
    return b"\x00" * (int((length + alignment - 1)/alignment)*alignment - length)
:: #endif

class OFReader(object):
    """
    Cursor over a read-only buffer

    OpenFlow messages are best thought of as a sequence of elements of
    variable size, rather than a C-style struct with fixed offsets and
    known field lengths. This class supports efficiently reading
    fields sequentially and is intended to be used recursively by the
    parsers of child objects which will implicitly update the offset.

    buf: buffer object
    start: initial position in the buffer
    length: number of bytes after start
    offset: distance from start
    """
    def __init__(self, buf, start=0, length=None):
        self.buf = buf
        self.start = start
        if length is None:
            self.length = len(buf) - start
        else:
            self.length = length
        self.offset = 0

    def read(self, fmt):
        st = struct.Struct(fmt)
        if self.offset + st.size > self.length:
            raise loxi.ProtocolError("Buffer too short")
        result = st.unpack_from(self.buf, self.start+self.offset)
        self.offset += st.size
        return result

    def read_all(self):
        s = self.buf[(self.start+self.offset):(self.start+self.length)]
        assert(len(s) == self.length - self.offset)
        self.offset = self.length
        return s

    def peek(self, fmt, offset=0):
        st = struct.Struct(fmt)
        if self.offset + offset + st.size > self.length:
            raise loxi.ProtocolError("Buffer too short")
        result = st.unpack_from(self.buf, self.start + self.offset + offset)
        return result

    def skip(self, length):
        if self.offset + length > self.length:
            raise loxi.ProtocolError("Buffer too short")
        self.offset += length

    def skip_align(self):
        new_offset = int((self.offset + 7) / 8) * 8
        if new_offset > self.length:
            raise loxi.ProtocolError("Buffer too short")
        self.offset = new_offset

    def is_empty(self):
        return self.offset == self.length

    # Used when parsing objects that have their own length fields
    def slice(self, length, rewind=0):
        if self.offset + length - rewind > self.length:
            raise loxi.ProtocolError("Buffer too short")
        reader = OFReader(self.buf, self.start + self.offset - rewind, length)
        reader.skip(rewind)
        self.offset += length - rewind
        return reader

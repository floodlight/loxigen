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
:: import itertools
:: import of_g
:: include('_copyright.py')

:: include('_autogen.py')

import struct
import const
import util
import loxi

class OXM(object):
    type_len = None # override in subclass
    pass

:: for ofclass in ofclasses:
:: nonskip_members = [m for m in ofclass.members if not m.skip]
class ${ofclass.pyname}(OXM):
:: for m in ofclass.type_members:
    ${m.name} = ${m.value}
:: #endfor

    def __init__(self, ${', '.join(["%s=None" % m.name for m in nonskip_members])}):
:: for m in nonskip_members:
        if ${m.name} != None:
            self.${m.name} = ${m.name}
        else:
            self.${m.name} = ${m.oftype.gen_init_expr()}
:: #endfor

    def pack(self):
        packed = []
:: include("_pack.py", ofclass=ofclass)
        return ''.join(packed)

    @staticmethod
    def unpack(buf):
        obj = ${ofclass.pyname}()
:: include("_unpack.py", ofclass=ofclass)
        return obj

    def __eq__(self, other):
        if type(self) != type(other): return False
:: for m in nonskip_members:
        if self.${m.name} != other.${m.name}: return False
:: #endfor
        return True

    def __ne__(self, other):
        return not self.__eq__(other)

    def show(self):
        import loxi.pp
        return loxi.pp.pp(self)

    def pretty_print(self, q):
:: include('_pretty_print.py', ofclass=ofclass)

:: #endfor

parsers = {
:: key = lambda x: int(x.type_members[0].value, 16)
:: for ofclass in sorted(ofclasses, key=key):
    ${key(ofclass)} : ${ofclass.pyname}.unpack,
:: #endfor
}

:: superclass_pyname = ofclass.superclass.pyname if ofclass.superclass else "loxi.OFObject"
:: from loxi_ir import *
:: import py_gen.oftype
:: import py_gen.util as util
:: type_members = [m for m in ofclass.members if type(m) == OFTypeMember]
:: normal_members = [m for m in ofclass.members if type(m) == OFDataMember or
::                                                 type(m) == OFDiscriminatorMember]
:: if ofclass.virtual:
:: discriminator_fmts = { 1: "B", 2: "!H", 4: "!L" }
:: discriminator_fmt = discriminator_fmts[ofclass.discriminator.length]
:: #endif
class ${ofclass.pyname}(${superclass_pyname}):
:: if ofclass.virtual:
    subtypes = {}

:: #endif
:: for m in type_members:
    ${m.name} = ${m.value}
:: #endfor

    def __init__(${', '.join(['self'] + ["%s=None" % m.name for m in normal_members])}):
:: for m in normal_members:
        if ${m.name} != None:
            self.${m.name} = ${m.name}
        else:
:: if m.name == 'xid':
:: # HACK for message xid
            self.${m.name} = None
:: else:
            self.${m.name} = ${py_gen.oftype.gen_init_expr(m.oftype, version=version)}
:: #endif
:: #endfor
        return

    def pack(self):
        packed = []
:: include("_pack.py", ofclass=ofclass)
        return ''.join(packed)

    @staticmethod
    def unpack(reader):
:: if ofclass.virtual:
        subtype, = reader.peek(${repr(discriminator_fmt)}, ${ofclass.discriminator.offset})
        subclass = ${ofclass.pyname}.subtypes.get(subtype)
        if subclass:
            return subclass.unpack(reader)

:: #endif
        obj = ${ofclass.pyname}()
:: include("_unpack.py", ofclass=ofclass)
        return obj

    def __eq__(self, other):
        if type(self) != type(other): return False
:: for m in normal_members:
        if self.${m.name} != other.${m.name}: return False
:: #endfor
        return True

    def pretty_print(self, q):
:: include('_pretty_print.py', ofclass=ofclass)

:: # Register with our superclass
:: if ofclass.superclass:
:: type_field_name = ofclass.superclass.discriminator.name
:: type_value = ofclass.member_by_name(type_field_name).value
${superclass_pyname}.subtypes[${type_value}] = ${ofclass.pyname}
:: #endif

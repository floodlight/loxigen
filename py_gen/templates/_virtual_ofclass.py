:: import py_gen.util as util
:: superclass_pyname = ofclass.superclass.pyname if ofclass.superclass else "loxi.OFObject"
:: fmts = { 1: "B", 2: "!H", 4: "!L" }
:: fmt = fmts[ofclass.discriminator.length]
:: trail = ' '.join([x.pyname for x in util.ancestors(ofclass)])
class ${ofclass.pyname}(${superclass_pyname}):
    subtypes = {}

    @staticmethod
    def unpack(reader):
        subtype, = reader.peek(${repr(fmt)}, ${ofclass.discriminator.offset})
        try:
            subclass = ${ofclass.pyname}.subtypes[subtype]
        except KeyError:
            raise loxi.ProtocolError("unknown ${trail} subtype %#x" % subtype)
        return subclass.unpack(reader)

:: # Register with our superclass
:: if ofclass.superclass:
:: type_field_name = ofclass.superclass.discriminator.name
:: type_value = ofclass.member_by_name(type_field_name).value
${superclass_pyname}.subtypes[${type_value}] = ${ofclass.pyname}
:: #endif

:: from loxi_ir import *
:: normal_members = [m for m in ofclass.members if type(m) == OFDataMember]
class ${ofclass.pyname}(${superclass}):
:: for m in ofclass.type_members:
    ${m.name} = ${m.value}
:: #endfor

    def __init__(${', '.join(['self'] + ["%s=None" % m.name for m in normal_members])}):
:: for m in normal_members:
        if ${m.name} != None:
            self.${m.name} = ${m.name}
        else:
            self.${m.name} = ${m.oftype.gen_init_expr()}
:: #endfor
        return

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
:: for m in normal_members:
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

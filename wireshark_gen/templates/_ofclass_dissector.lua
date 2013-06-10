:: from loxi_ir import *
function ${name}(buf, root)
:: for m in ofclass.members:
:: if isinstance(m, OFTypeMember):
    -- type ${m.name}
:: elif isinstance(m, OFDataMember):
    -- data ${m.name}
:: elif isinstance(m, OFLengthMember):
    -- length ${m.name}
:: elif isinstance(m, OFPadMember):
    -- pad
:: #endif
:: #endfor
end


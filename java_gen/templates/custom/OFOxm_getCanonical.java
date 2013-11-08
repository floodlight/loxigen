//:: import re
    public ${prop.java_type.public_type} getCanonical() {
        //:: if not msg.member_by_name("masked").value == "true":
        // exact match OXM is always canonical
        return this;
        //:: else:
        //:: mask_type = msg.member_by_name("mask").java_type.public_type
        if (${mask_type}.NO_MASK.equals(mask)) {
            //:: unmasked = re.sub(r'(.*)Masked(Ver.*)', r'\1\2', msg.name)
            return new ${unmasked}(value);
        } else if(${mask_type}.FULL_MASK.equals(mask)) {
            return null;
        } else {
            return this;
        }
        //:: #endif
    }

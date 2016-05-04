//:: import os
//:: for prop in msg.interface.members:
//::    if hasattr(prop, "custom_template") and prop.custom_template != None:
//::        getter_template_file_name = "%s/custom/%s" % (template_dir, prop.custom_template(builder=builder))
//::    else:
//::        getter_template_file_name = "%s/custom/%s_%s.java" % (template_dir, msg.name if not builder else msg.name + '.Builder', prop.getter_name)
//::    #endif
//::    if os.path.exists(getter_template_file_name):
//::        include(getter_template_file_name, msg=msg, builder=builder, has_parent=has_parent, prop=prop)
//::    else:
    @Override
    public ${prop.java_type.public_type} ${prop.getter_name}()${ "" if prop in msg.members else "throws UnsupportedOperationException"} {
//:: if prop in msg.members:
//::    version_prop = msg.member_by_name(prop.name)
//::    if version_prop.is_fixed_value:
        return ${version_prop.enum_value};
//::    elif version_prop.is_length_value:
        // FIXME: Hacky and inperformant way to determine a message length. Should be replaced with something better
        ByteBuf c = new LengthCountingPseudoByteBuf();
        WRITER.write(c, ${ "this" if not builder else "({0}) this.getMessage()".format(msg.name) });
        return c.writerIndex();
//::    else:
        return ${version_prop.name};
//::    #endif
//:: else:
        throw new UnsupportedOperationException("Property ${prop.name} not supported in version #{version}");
//:: #endif
    }
//:: #endif

//:: if generate_setters and prop.needs_setter:
    //:: setter_template_file_name = "%s/custom/%s_%s.java" % (template_dir, msg.name if not builder else msg.name + '.Builder', prop.setter_name)
    //:: if os.path.exists(setter_template_file_name):
    //:: include(setter_template_file_name, msg=msg, builder=builder, has_parent=has_parent)

    //:: else:
    @Override
    public ${msg.interface.name}.Builder ${prop.setter_name}(${prop.java_type.public_type} ${prop.name})${ "" if prop in msg.members else " throws UnsupportedOperationException"} {
        //:: if prop.is_writeable and prop in msg.members:
        this.${prop.name} = ${prop.name};
        this.${prop.name}Set = true;
        return this;
        //:: elif prop.is_writeable:
            throw new UnsupportedOperationException("Property ${prop.name} not supported in version #{version}");
        //:: else:
            throw new UnsupportedOperationException("Property ${prop.name} is not writeable");
        //:: #endif
    }
    //:: #endif
    //:: #endif
//:: #endfor

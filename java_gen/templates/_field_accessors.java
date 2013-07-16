//:: for prop in msg.interface.members:
    @Override
    public ${prop.java_type.public_type} get${prop.title_name}()${ "" if prop in msg.members else "throws UnsupportedOperationException"} {
//:: if prop in msg.members:
//::    version_prop = msg.get_member(prop.name)
//::    if version_prop.is_fixed_value:
        return ${version_prop.enum_value};
//::    elif version_prop.is_length_value:
        // FIXME: Hacky and inperformant way to determine a message length. Should be replaced with something better
        ChannelBuffer c = new LengthCountingPseudoChannelBuffer();
        WRITER.write(c, ${ "this" if not builder else "({0}) this.getMessage()".format(msg.name) });
        return c.writerIndex();
//::    else:
        return ${version_prop.name};
//::    #endif
//:: else:
        throw new UnsupportedOperationException("Property ${prop.name} not supported in version #{version}");
//:: #endif
    }

//:: if generate_setters and prop.is_writeable:
    @Override
    public ${msg.interface.name}.Builder set${prop.title_name}(${prop.java_type.public_type} ${prop.name})${ "" if prop in msg.members else " throws UnsupportedOperationException"} {
//:: if prop in msg.members:
        this.${prop.name} = ${prop.name};
        this.${prop.name}Set = true;
        return this;
//:: else:
            throw new UnsupportedOperationException("Property ${prop.name} not supported in version #{version}");
//:: #endif
    }
//:: #endif
//:: #endfor

OFReader = {}
OFReader.new = function(buf, offset)
    local self = {}
    offset = offset or 0

    self.read = function(len)
        local r = buf(offset, len)
        offset = offset + len
        return r
    end

    self.read_all = function()
        local r = buf(offset, buf.len() - offset)
        offset = buf.len()
        return r
    end

    self.peek = function(off, len)
        return buf(offset + off, len)
    end

    self.skip = function(len)
        offset = offset + len
    end

    self.is_empty = function()
        return offset == buf:len()
    end

    self.slice = function(len)
        r = OFReader.new(buf(offset, len))
        offset = offset + len
        return r
    end
    
    return self
end

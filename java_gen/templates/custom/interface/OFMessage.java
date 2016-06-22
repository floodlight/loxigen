    
    /**
     * Compares the two messages for equality, ignoring the XID field.
     *
     * @param obj the other message to compare
     * @return true if the messages are equal, ignoring the XID; false otherwise
     */
    boolean equalsIgnoreXid(Object obj);

    /**
     * Computes the hashcode of the message, ignoring the XID field.
     * This can be useful in hashing OFMessages where an OFMessage
     * is "the same as" another OFMessage if all fields are equal
     * except for possibly the XIDs, which may or may not be equal.
     *
     * The obvious problem is that existing hash data structure 
     * implementations will use OFMessage's hashCode() function instead.
     * In order to use the functionality of hashCodeIgnoreXid(), one 
     * must wrap the OFMessage within a user-defined class, where this 
     * user-defined class is used as the key within a hash data structure, 
     * e.g. HashMap. The user-defined class' overrideen hashCode() 
     * function must explicitly invoke hashCodeIgnoreXid() when computing 
     * the hash of the OFMessage member instead of computing it using 
     * OFMessage's hashCode().
     *
     * @return the hashcode of the message, ignoring the XID
     */
    int hashCodeIgnoreXid();

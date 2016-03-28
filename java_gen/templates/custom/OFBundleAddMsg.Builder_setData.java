    /** Custom setter that ensures the BundleAdd message inherits the XID from their
     *  contained message, as per OF Spec 1.4.0:
     *  <p>
     *  7.3.9.6 Adding messages to a bundle
     *  </p><p>
     *     Message added in a bundle should have a unique xid to help matching errors to messages,
     *     and the xid of the bundle add message must be the same.
     *  </p>
     */
    @Override
    public OFBundleAddMsg.Builder setData(OFMessage data) {
        this.data = data;
        this.dataSet = true;
        return setXid(data.getXid());
    }

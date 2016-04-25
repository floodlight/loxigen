
    public List<OFBucket> removeBucketBasedOnCommandBucketId() {
        if(commandBucketId == null)
            throw new IllegalStateException("CommandBucketId is null");
        if(commandBucketId.equals(OFGroupBucket.BUCKET_FIRST))
            buckets.remove(0);
        else if(commandBucketId.equals(OFGroupBucket.BUCKET_LAST))
            buckets.remove(buckets.size()-1);
        else if(commandBucketId.equals(OFGroupBucket.BUCKET_ALL))
            buckets.removeAll(buckets);
        return buckets;
    }


    public List<OFBucket> removeBucketBasedOnSpecificIndex(int index) {
        if(index <= buckets.size())
            buckets.remove(index);
        else
            throw new IllegalStateException("Index is not valid");
        return buckets;
    }

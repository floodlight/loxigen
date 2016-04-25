
    public List<OFBucket> insertBucketBasedOnCommandBucketId(OFBucket bucket) {
        if(commandBucketId == null)
            throw new IllegalStateException("CommandBucketId is null");
        if(commandBucketId.equals(OFGroupBucket.BUCKET_FIRST))
            buckets.add(0, bucket);
        else if(commandBucketId.equals(OFGroupBucket.BUCKET_LAST))
            buckets.add(buckets.size(), bucket);
        return buckets;
    }


    public List<OFBucket> insertBucketBasedOnSpecificIndex(OFBucket bucket, int index) {
        if(index <= buckets.size())
            buckets.add(index,bucket);
        else
            throw new IllegalStateException("Index is not valid");
        return buckets;
    }
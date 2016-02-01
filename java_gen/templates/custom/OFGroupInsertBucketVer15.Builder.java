

        private boolean commandBucketIdSet;
        private OFGroupBucket commandBucketId;

 @Override
 public int getBucketArrayLen() throws UnsupportedOperationException {
	 return buckets.size();
 }
 
 @Override
 public OFGroupInsertBucket.Builder setBucketArrayLen(int bucketArrayLen) throws UnsupportedOperationException{
     //use getBucketArratLen();
	 logger.info("Please use getBucketArrayLen() - based on list implementation");
	 throw new UnsupportedOperationException();
}
 
 @Override
 public OFGroupMod.Builder setCommandBucketId(OFGroupBucket commandBucketId) throws UnsupportedOperationException{
	 this.commandBucketId = commandBucketId;
	 this.commandBucketIdSet = true;
	 return this;
 }
 
 @Override
 public OFGroupBucket getCommandBucketId() throws UnsupportedOperationException{
	 return commandBucketId;
 }
 
public List<OFBucket> insertBucketBasedOnCommandBucketId(OFBucket bucket) {
	if(commandBucketId == null)
		throw new IllegalStateException("CommandBucketId is null");
	if(commandBucketId.equals(OFGroupBucket.BUCKET_FIRST))
		buckets.add(0, bucket);
	else if(commandBucketId.equals(OFGroupBucket.BUCKET_LAST))
		buckets.add(buckets.size(), bucket);
	return buckets;
}


public List<OFBucket> insertBucketBasedOnSpesificIndex(OFBucket bucket, int index) {
	if(index <= buckets.size())
		buckets.add(index,bucket);
	else
		throw new IllegalStateException("Index is not valid");
	return buckets;
}
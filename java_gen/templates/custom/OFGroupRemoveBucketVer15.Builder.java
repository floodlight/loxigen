
        private boolean commandBucketIdSet;
        private OFGroupBucket commandBucketId;

 @Override
 public int getBucketArrayLen() throws UnsupportedOperationException {
	 return buckets.size();
 }
 
 @Override
 public OFGroupRemoveBucket.Builder setBucketArrayLen(int bucketArrayLen) throws UnsupportedOperationException{
     //use getBucketArratLen();
	 logger.info("Please use getBucketArrayLen() - based on list implementation");
	 throw new UnsupportedOperationException();
}
 
 @Override
 public OFGroupBucket getCommandBucketId() throws UnsupportedOperationException{
	 return commandBucketId;
 }
 
 @Override
 public OFGroupMod.Builder setCommandBucketId(OFGroupBucket commandBucketId) throws UnsupportedOperationException{
	 this.commandBucketId = commandBucketId;
	 this.commandBucketIdSet = true;
	 return this;
 }
 
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


	public List<OFBucket> removeBucketBasedOnSpesificIndex(int index) {
		if(index <= buckets.size())
			buckets.remove(index);
		else
			throw new IllegalStateException("Index is not valid");
		return buckets;
	}
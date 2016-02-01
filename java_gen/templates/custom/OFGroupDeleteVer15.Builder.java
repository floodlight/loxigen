
 @Override
 public int getBucketArrayLen() throws UnsupportedOperationException {
	 return buckets.size();
 }
 
 @Override
 public OFGroupBucket getCommandBucketId() throws UnsupportedOperationException{
	 //command bucket id is used in insert bucket and remove bucket
	 throw new UnsupportedOperationException();
 }
 
 @Override
 public Builder setCommandBucketId(OFGroupBucket commandBucketId) throws UnsupportedOperationException{
	 //command bucket id is used in insert bucket and remove bucket
	 throw new UnsupportedOperationException();
 }
 
 @Override
 public OFGroupDelete.Builder setBucketArrayLen(int bucketArrayLen) throws UnsupportedOperationException{
	    //use getBucketArratLen();
		 logger.info("Please use getBucketArrayLen() - based on list implementation");
		 throw new UnsupportedOperationException();
 }
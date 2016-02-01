
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
 public OFGroupModify.Builder setBucketArrayLen(int bucketArrayLen) throws UnsupportedOperationException{
     List<OFBucket> buckets = Arrays.asList(new OFBucket[bucketArrayLen]);
     this.buckets = buckets;
     return this;
 }
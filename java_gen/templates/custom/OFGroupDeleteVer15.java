 

@Override
 public int getBucketArrayLen() throws UnsupportedOperationException {
	 return buckets.size();
 }

@Override
public OFGroupBucket getCommandBucketId() throws UnsupportedOperationException{
	 //command bucket id is used in insert bucket and remove bucket
	 throw new UnsupportedOperationException();
}
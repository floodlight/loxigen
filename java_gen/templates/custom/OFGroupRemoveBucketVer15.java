
private OFGroupBucket commandBucketId;

@Override
 public int getBucketArrayLen() throws UnsupportedOperationException {
	 return buckets.size();
 }

@Override
public OFGroupBucket getCommandBucketId() throws UnsupportedOperationException {
	 return commandBucketId;
}
package ng.dominic.awsupload.bucket;

public enum BucketName {

    PROFILE_IMAGE("dng-img-upload-123");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}

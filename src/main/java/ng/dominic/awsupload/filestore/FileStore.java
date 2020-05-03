package ng.dominic.awsupload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private final AmazonS3 s3;

    @Autowired
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    /**
     * de Optional<Map<St, St>> komt uit de tutorial... this is bad
     * @param path
     * @param filename
     * @param optionalMetadata
     * @param inputStream
     */
    public void save(String path,
                     String filename,
                     InputStream inputStream,
                     Optional<Map<String, String>> optionalMetadata) {
        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });
        try {
            s3.putObject(path, filename, inputStream, metadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("failure to upload ", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object s3Object = s3.getObject(path, key);
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("failure to download ", e);
        }
    }
}

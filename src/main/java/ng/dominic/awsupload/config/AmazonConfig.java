package ng.dominic.awsupload.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    /**
     * My SuperSecretCredentials were added to .gitignore obviously...
     *
     * @return
     */
    @Bean
    public AmazonS3 s3() {
        var accesskey = System.getenv().get("AWS_S3_ACCESSKEY");
        var secretkey = System.getenv().get("AWS_S3_SECRETKEY");
        AWSCredentials awsCredentials = new BasicAWSCredentials(accesskey, secretkey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }
}

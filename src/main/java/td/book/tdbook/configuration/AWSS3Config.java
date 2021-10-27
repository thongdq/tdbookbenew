package td.book.tdbook.configuration;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSS3Config {

    // @Value("${aws.access_key_id}")
    // private String accessKeyId;

    // @Value("${aws.secret_access_key}")
    // private String secretAccessKey;

    // @Value("${aws.s3.region}")
    // private String region;

    @Bean
    public S3Client s3client() {

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create("AKIAYV5VRRAXLJ37QGBG", "nZ92Os8qkSx8CFaK1fz+sW4J3iUKMk9csRl6BMjQ");

        S3Client s3 = S3Client.builder()
                        .region(Region.AP_SOUTHEAST_1).credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();

        return s3;
    }
    
}

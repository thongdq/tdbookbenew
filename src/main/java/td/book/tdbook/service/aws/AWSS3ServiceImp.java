package td.book.tdbook.service.aws;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import td.book.tdbook.exception.S3ImageException;

@Service
public class AWSS3ServiceImp implements AWSS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AWSS3ServiceImp.class);

    @Autowired
    private S3Client s3Client;

    @Override
    public String uploadFile(MultipartFile file, String bucket, String key) throws S3ImageException {
        String objectUrl = "";

        try {
            List<S3Object> objects = listBucketObjects(bucket);
            for (S3Object s3Object : objects) {
                if(s3Object.key().equals(key)) {
                    throw new S3ImageException("Image already exits");
                }
            }

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                .bucket(bucket)
                                                .key(key)
                                                .acl("public-read")
                                                .contentType(file.getContentType())
                                                .contentLength(file.getSize())
                                                .build();

            s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes())));

            final URL Url = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build());
            objectUrl = Url.toString();

        } catch(SdkServiceException ase) {
            logger.error("Error: " + ase);
            throw new S3ImageException(ase.getMessage());
        } catch(IOException ioe) {
            logger.error("Error: " + ioe);
            throw new S3ImageException(ioe.getMessage());
        }

        return objectUrl;
    }

    @Override
    public List<S3Object> listBucketObjects(String bucket) {
        List<S3Object> objects = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucket).build();
            ListObjectsResponse res = s3Client.listObjects(listObjects);

            objects = res.contents();

            for(ListIterator iterVals = objects.listIterator(); iterVals.hasNext();) {
                S3Object s3obj = (S3Object) iterVals.next();
                System.out.println("https://" + bucket + ".s3" + ".ap-southeast-1.amazonaws.com/" + s3obj.key());
            }
        } catch (S3Exception e) {
            System.out.println(e.getMessage());
        }

        return objects;
    }

    @Override
    public void deleteObject(String bucket, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                    .bucket(bucket)
                                                    .key(key)
                                                    .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public boolean checkS3ObjectIsExist(String bucket, String key) {
        List<S3Object> s3Objects = listBucketObjects(bucket);
        if(!s3Objects.isEmpty()) {
            for (S3Object s3Object : s3Objects) {
                if(s3Object.key().equals(key))
                    return true;
            }
        }
        return false;
    }
    
}

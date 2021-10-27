package td.book.tdbook.service.aws;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.S3Object;

public interface AWSS3Service {
    public String uploadFile(MultipartFile file, String bucket, String key);
    public List<S3Object> listBucketObjects(String bucket);
    public void deleteObject(String bucket, String key);
    public boolean checkS3ObjectIsExist(String bucket, String key);
}

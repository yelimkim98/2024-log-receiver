package naver.kiel0103.logreceiver;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class KafkaConsumer {

    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        BasicAWSCredentials awsCredentials= new BasicAWSCredentials(accessKey, secretKey);
        this.amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }


    @KafkaListener(topics = "log-topic", groupId = "yerim")
    public void listen(String message) throws IOException {
        log.debug(message);

        /*
         append 매개변수로 true 를 주면
         덮어쓰기 하지 않고 이어서 씀
         */
        String fileName = "./log.txt";
        FileWriter writer = new FileWriter(fileName, true);
        writer.write(message + "\n");
        writer.close();

        String s3FileName = CommonUtils.fileNameCreate(fileName);
        log.debug("## {}", s3FileName);

        /* S3 */
        Path source = Paths.get(fileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(Files.probeContentType(source));

        try (InputStream inputStream = new FileInputStream(source.toFile())) {
            amazonS3Client.putObject(new PutObjectRequest(
                    bucketName, s3FileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            log.warn("# 파일 업로드 실패");
        }
    }
}

package demo.JPA.service;

import io.awspring.cloud.s3.S3Template;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 파일을 S3에 업로드하고 해당 파일의 URL을 반환합니다.
     * 파일 이름은 UUID를 사용하여 고유하게 생성됩니다.
     *
     * @param file     업로드할 MultipartFile
     * @param dirName  S3 버킷 내의 디렉토리 이름 (예: "receipts")
     * @return         업로드된 파일의 S3 URL
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String upload(MultipartFile file, String dirName) throws IOException {
        // 1. 임의의 토큰(UUID)과 원본 파일명을 조합하여 고유한 파일 이름 생성
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = dirName + "/" + UUID.randomUUID().toString();

        // 2. S3Template을 사용하여 파일을 업로드하고, 업로드된 리소스의 URL을 가져옴
        S3Resource resource = s3Template.upload(bucketName, uniqueFileName, file.getInputStream());

        // 3. 생성된 URL을 문자열로 반환
        return resource.getURL().toString();
    }
}